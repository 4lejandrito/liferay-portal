#!/usr/bin/env python3
"""
atlassian_mcp.py — MCP server for Atlassian OAuth token management.

Client credentials (client_id, client_secret) are fetched from 1Password
on demand — only when a new authorization or token refresh is needed — and
are never stored beyond the duration of that call.

Each process writes its own token file named with the access token's expiry
timestamp (e.g. atlassian_token_2026-04-28_14-30-00.txt).  On startup the
server (1) deletes expired token files and (2) loads the most long-lived
valid token from disk.  A loaded token has no associated refresh token, so
when it expires the process triggers a full 3-legged browser auth.

Run:
  python atlassian_mcp.py           # stdio transport (for Claude Code / MCP clients)
  python atlassian_mcp.py --status  # print current session state and exit
"""

import argparse
import atexit
import http.server
import json
import os
import secrets
import signal
import subprocess
import sys
import threading
import time
import urllib.error
import urllib.parse
import urllib.request
import webbrowser
from pathlib import Path
from typing import Optional, Dict, Any, Tuple

from mcp.server.fastmcp import FastMCP


# ── Constants ─────────────────────────────────────────────────────────────────

OP_ITEM          = "Liferay DXP codebase Atlassian MCP"
AUTH_URL         = "https://auth.atlassian.com/authorize"
TOKEN_URL        = "https://auth.atlassian.com/oauth/token"
REVOKE_URL       = "https://auth.atlassian.com/oauth/token/revoke"
RESOURCES_URL    = "https://api.atlassian.com/oauth/token/accessible-resources"
SCOPES           = "read:jira-work write:jira-work offline_access"
REDIRECT_URI     = "http://127.0.0.1:53197/callback"
CALLBACK_TIMEOUT = 30           # seconds to wait for browser callback
ACCESS_MARGIN    = 120          # seconds before expiry to proactively refresh

AUTH_DIR          = Path.home() / ".liferay" / "auth"
TOKEN_FILE_PREFIX = "atlassian_token_"
TOKEN_FILE_GLOB   = "atlassian_token_*.txt"
TOKEN_TIME_FMT    = "%Y-%m-%d_%H-%M-%S"


# ── 1Password ─────────────────────────────────────────────────────────────────

def _fetch_client_credentials() -> Tuple[str, str]:
    """
    Read client_id and client_secret from 1Password on demand.
    Returns (client_id, client_secret) as a plain tuple — nothing is stored.
    Raises RuntimeError if the item is unavailable or credentials are missing.
    """
    try:
        result = subprocess.run(
            ["op", "item", "get", OP_ITEM, "--format", "json"],
            capture_output=True, text=True, check=True,
        )
        data   = json.loads(result.stdout)
        fields = {f["label"]: f.get("value", "") for f in data.get("fields", [])}
        client_id     = fields.get("username", "")
        client_secret = fields.get("password", "")
        if client_id and client_secret:
            return client_id, client_secret
        raise RuntimeError(f"username/password fields not found in 1Password item {OP_ITEM!r}")
    except subprocess.CalledProcessError as exc:
        raise RuntimeError(f"1Password CLI failed: {exc.stderr.strip()}") from exc
    except FileNotFoundError:
        raise RuntimeError("1Password CLI ('op') not found — install it from https://developer.1password.com/docs/cli/")


# ── HTTP helpers ──────────────────────────────────────────────────────────────

def _post(url: str, data: dict) -> dict:
    body = urllib.parse.urlencode(data).encode()
    req  = urllib.request.Request(url, data=body, method="POST")
    req.add_header("Content-Type", "application/x-www-form-urlencoded")
    req.add_header("Accept", "application/json")
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read())
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode(errors="replace")
        raise RuntimeError(f"POST {url} → HTTP {exc.code}: {detail}") from exc


# ── Token file helpers ────────────────────────────────────────────────────────

def _token_file_for(actual_expiry: float) -> Path:
    return AUTH_DIR / f"{TOKEN_FILE_PREFIX}{time.strftime(TOKEN_TIME_FMT, time.localtime(actual_expiry))}.txt"


def _parse_token_file_expiry(path: Path) -> Optional[float]:
    name = path.stem
    if not name.startswith(TOKEN_FILE_PREFIX):
        return None
    try:
        return time.mktime(time.strptime(name[len(TOKEN_FILE_PREFIX):], TOKEN_TIME_FMT))
    except ValueError:
        return None


def _cleanup_expired_token_files() -> None:
    if not AUTH_DIR.exists():
        return
    now = time.time()
    for f in AUTH_DIR.glob(TOKEN_FILE_GLOB):
        expiry = _parse_token_file_expiry(f)
        if expiry is not None and expiry <= now:
            try:
                f.unlink()
            except Exception:
                pass


def _load_best_token_file() -> Tuple[str, float]:
    """Return (token, actual_expiry) for the valid file with the longest remaining life."""
    if not AUTH_DIR.exists():
        return "", 0.0
    now = time.time()
    best_token, best_expiry = "", 0.0
    for f in AUTH_DIR.glob(TOKEN_FILE_GLOB):
        expiry = _parse_token_file_expiry(f)
        if expiry is None or expiry <= now + ACCESS_MARGIN:
            continue
        try:
            token = f.read_text().strip()
            if token and expiry > best_expiry:
                best_token, best_expiry = token, expiry
        except Exception:
            pass
    return best_token, best_expiry


def _write_token_file(token: str, actual_expiry: float) -> Path:
    AUTH_DIR.mkdir(parents=True, exist_ok=True)
    path = _token_file_for(actual_expiry)
    path.write_text(token)
    if os.name != "nt":
        path.chmod(0o600)
    return path.absolute()


# ── Token store ───────────────────────────────────────────────────────────────

class _TokenStore:
    """In-memory OAuth token state for the lifetime of the MCP server process."""

    def __init__(self):
        self._refresh_token:  str            = ""
        self._access_token:   str            = ""
        self._expires_at:    float           = 0.0   # internal cutoff (actual - ACCESS_MARGIN)
        self._actual_expiry: float           = 0.0   # server-reported expiry (used for filename)
        self._token_file:    Optional[Path]  = None  # file owned by this process
        self._lock = threading.RLock()
        self._startup()

    # ── public API ────────────────────────────────────────────────────────────

    def get_access_token(self) -> str:
        """
        Return a valid access token.
        - If the cached token is still fresh, return it immediately.
        - If a refresh token exists (from a browser auth in this session), exchange it.
        - Otherwise open the browser for a full 3-legged OAuth flow.
        """
        with self._lock:
            if self._access_token and time.time() < self._expires_at:
                return self._access_token
            if self._refresh_token:
                self._do_refresh()
            else:
                # Includes the case where a loaded token just expired.
                self._do_browser_auth()
            return self._access_token

    def ensure_token_file(self, token: str) -> Path:
        """Return this process's token file, creating it on first call."""
        with self._lock:
            if self._token_file is None:
                self._token_file = _write_token_file(token, self._actual_expiry)
            return self._token_file

    def get_expiry_str(self) -> str:
        with self._lock:
            if self._actual_expiry <= 0:
                return "Not authenticated"
            return time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(self._actual_expiry))

    def revoke(self) -> None:
        """Revoke the refresh token server-side and clear in-memory state and our token file."""
        with self._lock:
            if self._refresh_token:
                try:
                    client_id, client_secret = _fetch_client_credentials()
                    _post(REVOKE_URL, {
                        "token":           self._refresh_token,
                        "token_type_hint": "refresh_token",
                        "client_id":       client_id,
                        "client_secret":   client_secret,
                    })
                except Exception:
                    pass  # best-effort — clear local state regardless
            self._refresh_token = ""
            self._access_token  = ""
            self._expires_at    = 0.0
            self._actual_expiry = 0.0
            self.cleanup_file()

    def cleanup_file(self) -> None:
        """Delete this process's token file without touching any server-side state."""
        with self._lock:
            if self._token_file:
                try:
                    self._token_file.unlink()
                except Exception:
                    pass
                self._token_file = None

    def status(self) -> dict:
        """Return a status snapshot (no secrets exposed)."""
        with self._lock:
            now       = time.time()
            remaining = self._expires_at - now
            if self._actual_expiry <= 0:
                expiry_str = "Not authenticated"
            else:
                expiry_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(self._actual_expiry))
            return {
                "has_refresh_token":              bool(self._refresh_token),
                "has_access_token":               bool(self._access_token),
                "access_token_valid":             bool(self._access_token) and remaining > 0,
                "access_token_expires_in_seconds": max(0, int(remaining)),
                "expires_at":                     expiry_str,
                "token_file":                     str(self._token_file) if self._token_file else None,
            }

    # ── internals ─────────────────────────────────────────────────────────────

    def _startup(self) -> None:
        """(1) Delete expired token files. (2) Load the best live token from disk."""
        _cleanup_expired_token_files()
        token, actual_expiry = _load_best_token_file()
        if token:
            self._access_token  = token
            self._actual_expiry = actual_expiry
            self._expires_at    = actual_expiry - ACCESS_MARGIN
            # No refresh_token — when this expires, fall through to full browser auth.
            print(
                f"[atlassian-mcp] Loaded existing token (expires "
                f"{time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(actual_expiry))}).",
                file=sys.stderr, flush=True,
            )

    def _store_tokens(self, tokens: dict) -> None:
        """Unpack a token response, update state, and write this process's token file."""
        actual_expiry = time.time() + int(tokens.get("expires_in", 3600))
        self._access_token  = tokens["access_token"]
        self._actual_expiry = actual_expiry
        self._expires_at    = actual_expiry - ACCESS_MARGIN
        new_refresh = tokens.get("refresh_token")
        if new_refresh:
            self._refresh_token = new_refresh
        # Replace our owned file with a freshly-named one.
        self.cleanup_file()
        self._token_file = _write_token_file(self._access_token, actual_expiry)

    def _do_refresh(self) -> None:
        client_id, client_secret = _fetch_client_credentials()
        try:
            tokens = _post(TOKEN_URL, {
                "grant_type":    "refresh_token",
                "client_id":     client_id,
                "client_secret": client_secret,
                "refresh_token": self._refresh_token,
            })
            self._store_tokens(tokens)
        except RuntimeError:
            # Refresh token invalid or expired — clear and fall back to browser auth.
            self._refresh_token = ""
            self._access_token  = ""
            self._expires_at    = 0.0
            self._actual_expiry = 0.0
            self._do_browser_auth()

    def _do_browser_auth(self) -> None:
        """Run the authorization code flow. Blocks until callback or timeout."""
        client_id, client_secret = _fetch_client_credentials()
        state_val = secrets.token_urlsafe(16)
        auth_url  = AUTH_URL + "?" + urllib.parse.urlencode({
            "audience":      "api.atlassian.com",
            "client_id":     client_id,
            "scope":         SCOPES,
            "redirect_uri":  REDIRECT_URI,
            "state":         state_val,
            "response_type": "code",
            "prompt":        "consent",
        })

        result: dict = {}

        class _Handler(http.server.BaseHTTPRequestHandler):
            def do_GET(self):
                params = urllib.parse.parse_qs(urllib.parse.urlparse(self.path).query)
                if secrets.compare_digest(params.get("state", [""])[0], state_val):
                    if "code" in params:
                        result["code"] = params["code"][0]
                    elif "error" in params:
                        result["error"] = params["error"][0]
                self.send_response(200)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.end_headers()
                msg = ("Authorization complete — return to your agent."
                       if "code" in result else "Authorization failed.")
                self.wfile.write(
                    f"<html><body style='font-family:sans-serif;padding:2em'>"
                    f"<p>{msg}</p></body></html>".encode()
                )

            def log_message(self, *_):
                pass

        server = http.server.HTTPServer(("127.0.0.1", 53197), _Handler)
        server.timeout = 1  # make handle_request() poll so we can check result

        def _serve() -> None:
            deadline = time.time() + CALLBACK_TIMEOUT
            while not result and time.time() < deadline:
                server.handle_request()

        thread = threading.Thread(target=_serve, daemon=True)
        thread.start()

        print(f"[atlassian-mcp] Opening browser for Atlassian authorization…", file=sys.stderr, flush=True)
        webbrowser.open(auth_url)

        thread.join(timeout=CALLBACK_TIMEOUT + 2)
        server.server_close()

        if "error" in result:
            raise RuntimeError(f"Atlassian OAuth error: {result['error']}")
        if "code" not in result:
            raise RuntimeError(f"Timed out waiting for OAuth callback ({CALLBACK_TIMEOUT} s).")

        tokens = _post(TOKEN_URL, {
            "grant_type":    "authorization_code",
            "client_id":     client_id,
            "client_secret": client_secret,
            "code":          result["code"],
            "redirect_uri":  REDIRECT_URI,
        })
        self._store_tokens(tokens)
        print("[atlassian-mcp] Authorization successful.", file=sys.stderr, flush=True)


# ── Singleton store ───────────────────────────────────────────────────────────

_store = _TokenStore()


# ── MCP server ────────────────────────────────────────────────────────────────

mcp = FastMCP(
    "atlassian-auth",
    instructions=(
        "Provides temporary Atlassian OAuth access token handles. "
        "Call get_atlassian_token_handle to obtain a JSON object containing the absolute path "
        "to the token file and its human-readable expiry timestamp. "
        "The token file contains ONLY the raw token string for use in 'Authorization: Bearer <content>'. "
        "Each MCP server process owns its own token file; expired files are cleaned up automatically."
    ),
)


@mcp.tool()
def get_atlassian_token_handle() -> Dict[str, Any]:
    """
    Return the path to a temporary file containing a valid Atlassian access token and its expiry.

    The file contains ONLY the raw token string. Use 'cat <path>' to get the bearer value.
    The expiry timestamp is provided for your context/validation.
    """
    token  = _store.get_access_token()
    path   = _store.ensure_token_file(token)
    expiry = _store.get_expiry_str()
    return {
        "token_file_path": str(path),
        "expires_at": expiry,
        "note": "The file contains only the raw token. Use it directly as Bearer <content>."
    }


@mcp.tool()
def get_atlassian_auth_status() -> str:
    """Return the current Atlassian OAuth session status without triggering auth."""
    s = _store.status()
    if not s["has_refresh_token"] and not s["has_access_token"]:
        return "No session. Call get_atlassian_token_handle to start browser auth."
    parts = []
    if s["has_refresh_token"]:
        parts.append("refresh token: present")
    if s["has_access_token"]:
        if s["access_token_valid"]:
            parts.append(f"access token: valid (expires at {s['expires_at']})")
        else:
            parts.append("access token: expired (will re-authenticate on next call)")
    if s["token_file"]:
        parts.append(f"token file: {s['token_file']}")
    return "Session active — " + ", ".join(parts) + "."


@mcp.tool()
def revoke_atlassian_token() -> str:
    """Revoke the current session server-side and delete this process's token file."""
    _store.revoke()
    return "Session revoked. Token file deleted."


# ── Shutdown hook ─────────────────────────────────────────────────────────────

def _revoke_on_shutdown() -> None:
    if _store.status()["has_refresh_token"]:
        print("[atlassian-mcp] Revoking refresh token on shutdown…", file=sys.stderr, flush=True)
        _store.revoke()
    else:
        _store.cleanup_file()
    _cleanup_expired_token_files()


# ── Entry point ───────────────────────────────────────────────────────────────

def main() -> None:
    parser = argparse.ArgumentParser(description="Atlassian OAuth MCP server")
    parser.add_argument("--status", action="store_true", help="Print status and exit")
    args = parser.parse_args()

    if args.status:
        s = _store.status()
        print(f"Token dir:      {AUTH_DIR}")
        print(f"Session:        {'Active' if s['has_access_token'] else 'None'}")
        if s["has_access_token"]:
            print(f"Expires at:     {s['expires_at']}")
            print(f"Remaining:      {s['access_token_expires_in_seconds']} s")
        if _store._token_file:
            print(f"Token file:     {_store._token_file}")
        return

    atexit.register(_revoke_on_shutdown)
    signal.signal(signal.SIGTERM, lambda _sig, _frame: sys.exit(0))

    mcp.run(transport="stdio")


if __name__ == "__main__":
    main()

# atlassian-auth MCP Server

An MCP server that manages Atlassian OAuth 2.0 tokens for use by AI agents (Claude Code, etc.).

## What it does

Handles the full OAuth 2.0 authorization code flow against Atlassian's identity service and exposes the resulting access token as a file on disk. Agents never see the raw token in the conversation — they get a file path and `cat` it when making API calls.

Key behaviours:

- **Browser auth on demand** — opens the system browser for 3-legged OAuth when no valid token is available.
- **Silent refresh** — if the session was established in the current process, the refresh token is used to silently renew the access token before it expires.
- **Per-process token files** — each MCP server process writes its own file named with the token's expiry timestamp (e.g. `atlassian_token_2026-04-28_14-30-00.txt`). The filename makes it easy to see at a glance when a token expires and which files belong to which session.
- **Startup recovery** — on start, the server deletes expired token files and loads the longest-lived valid token from disk. If a loaded token later expires, a fresh browser auth is triggered (no silent refresh for cross-process tokens).
- **Safe shutdown** — on exit the server revokes its refresh token server-side and deletes its own token file.

## Prerequisites

- Python 3.9+
- `pip install -r requirements.txt`
- [1Password CLI (`op`)](https://developer.1password.com/docs/cli/) — used to fetch OAuth client credentials at auth time.

## Installation

```bash
pip install -r requirements.txt
```

## Usage

### As an MCP server (Claude Code)

Add to `.claude/settings.json` in your project:

```json
{
  "enableAllProjectMcpServers": true,
  "mcpServers": {
    "atlassian-auth": {
      "command": "python3",
      "args": ["/path/to/atlassian_mcp.py"]
    }
  }
}
```

Or equivalently via `.mcp.json`:

```json
{
  "mcpServers": {
    "atlassian-auth": {
      "command": "python3",
      "args": ["/path/to/atlassian_mcp.py"]
    }
  }
}
```

### Standalone status check

```bash
python3 atlassian_mcp.py --status
```

## MCP tools

| Tool | Description |
|---|---|
| `get_atlassian_token_handle` | Returns the path to a file containing a valid Bearer token and its expiry. Triggers browser auth if needed. |
| `get_atlassian_auth_status` | Returns session state (token validity, expiry, token file path) without triggering auth. |
| `revoke_atlassian_token` | Revokes the session server-side and deletes the token file. |

### Example agent usage

```bash
# Get the token file path from the MCP tool, then use it directly:
curl -H "Authorization: Bearer $(cat /home/user/.liferay/auth/atlassian_token_2026-04-28_14-30-00.txt)" \
     https://api.atlassian.com/...
```

## Token file location

Token files are stored in `~/.liferay/auth/` with the naming pattern:

```
atlassian_token_YYYY-MM-DD_HH-MM-SS.txt
```

The timestamp is the token's actual expiry (as reported by the Atlassian server). Files are automatically cleaned up on the next server startup or when the owning session is revoked.

## Multi-agent behaviour

When multiple Claude Code sessions run simultaneously, each spawns its own MCP server process. Each process:

- Cleans up expired files on startup
- Loads the longest-lived valid token from disk (no browser popup if a sibling session already has one)
- Writes its own token file once `get_atlassian_token_handle` is called
- Deletes only its own file on shutdown — sibling files are never touched

## 1Password integration

Client credentials are read from the 1Password item `"Liferay DXP codebase Atlassian MCP"` on demand (only when a new auth or token refresh is needed) and are never stored. The `op` CLI must be installed and signed in — the server raises an error if it is unavailable.

#!/usr/bin/env python3
"""Fetch failure data from Testray for a case result ID, test name, or build URL.

Usage: fetch-failure-data.py <caseResultId | testName | testrayBuildUrl>

Environment:
  TESTRAY_CLIENT_ID      OAuth2 client ID (required)
  TESTRAY_CLIENT_SECRET  OAuth2 client secret (required)
  REPO_PATH              Path to liferay-portal checkout (default: cwd)

Output (JSON on stdout):
  caseResultId  integer
  name          test name
  status        PASSED | FAILED | ...
  type          Java Integration | Java Unit | Playwright | Poshi | ...
  errorTrace    error trace string (omitted when status is PASSED)
  failureDate   ISO timestamp (omitted when status is PASSED)
  firstFailSha  git hash or null (omitted when status is PASSED)
  lastPassSha   git hash or null (omitted when status is PASSED)

Exits non-zero and writes a message to stderr on any error.
"""

import base64
import fnmatch
import json
import os
import re
import subprocess
import sys
import urllib.parse
import urllib.request

TESTRAY_BASE = "https://testray.liferay.com"
MASTER_PROJECT_ID = 35392

_TYPE_MAP = {
    "Automated Functional Test": "Poshi",
    "JS Unit Test": "JavaScript",
    "Modules Integration Test": "Java Integration",
    "Modules Semantic Versioning Test": "Java Semantic Versioning",
    "Modules Unit Test": "Java Unit",
    "Playwright Test": "Playwright",
}


def _get_token():
    client_id = os.environ.get("TESTRAY_CLIENT_ID")
    client_secret = os.environ.get("TESTRAY_CLIENT_SECRET")

    if not client_id or not client_secret:
        raise RuntimeError("TESTRAY_CLIENT_ID and TESTRAY_CLIENT_SECRET must be set")

    credentials = base64.b64encode(
        f"{client_id}:{client_secret}".encode()
    ).decode()
    data = urllib.parse.urlencode({"grant_type": "client_credentials"}).encode()
    req = urllib.request.Request(
        f"{TESTRAY_BASE}/o/oauth2/token",
        data=data,
        headers={
            "Authorization": f"Basic {credentials}",
            "Content-Type": "application/x-www-form-urlencoded",
        },
        method="POST",
    )

    with urllib.request.urlopen(req) as resp:
        return json.load(resp)["access_token"]


def _get(token, path, params=None):
    url = f"{TESTRAY_BASE}{path}"

    if params:
        url += "?" + urllib.parse.urlencode(params)

    req = urllib.request.Request(
        url,
        headers={
            "Accept": "application/json",
            "Authorization": f"Bearer {token}",
        },
    )

    with urllib.request.urlopen(req) as resp:
        return json.load(resp)


def _find_test_file(repo_path, test_name):
    class_name = test_name.split("#")[0].split(".")[-1]
    patterns = [
        f"{class_name}.java",
        f"{class_name}.spec.ts",
        f"{class_name}.spec.tsx",
    ]
    args = ["find", repo_path]

    for i, pattern in enumerate(patterns):
        if i > 0:
            args.append("-o")
        args += ["-name", pattern]

    result = subprocess.run(args, capture_output=True, text=True)
    files = [f for f in result.stdout.strip().splitlines() if f]

    if not files:
        raise RuntimeError(
            f"Could not find test file for '{test_name}' in {repo_path}"
        )

    test_files = [f for f in files if "/test/" in f or "/testIntegration/" in f]

    return (test_files or files)[0]


def _get_team_from_codeowners(repo_path, file_path):
    codeowners_path = os.path.join(repo_path, ".github", "CODEOWNERS")

    if not os.path.exists(codeowners_path):
        raise RuntimeError(f"CODEOWNERS not found at {codeowners_path}")

    rel_path = "/" + os.path.relpath(file_path, repo_path)
    matched_team = None

    with open(codeowners_path) as f:
        for line in f:
            line = line.strip()

            if not line or line.startswith("#"):
                continue

            parts = line.split()

            if len(parts) < 2:
                continue

            pattern, owners = parts[0], parts[1:]

            if not pattern.startswith("/"):
                pattern = "/**/" + pattern

            if fnmatch.fnmatch(rel_path, pattern) or rel_path.startswith(
                pattern.rstrip("*")
            ):
                for owner in owners:
                    if owner.startswith("@liferay/"):
                        matched_team = owner[len("@liferay/"):]

    if not matched_team:
        raise RuntimeError(
            f"No @liferay/ team found for {rel_path} in CODEOWNERS"
        )

    return matched_team


def _resolve_name_to_case_result_id(token, name, repo_path):
    data = _get(
        token,
        "/o/c/cases",
        {
            "filter": f"name eq '{name}'",
            "pageSize": 20,
        },
    )
    cases = [
        c
        for c in data.get("items", [])
        if c.get("r_projectToCases_c_projectId") == MASTER_PROJECT_ID
    ]

    if len(cases) != 1:
        raise RuntimeError(
            f"Expected 1 case named '{name}' in master project, found {len(cases)}"
        )

    case_id = cases[0]["id"]
    test_file = _find_test_file(repo_path, name)
    team = _get_team_from_codeowners(repo_path, test_file)
    routine_name = f"[master] ci:test:{team}"
    routine_data = _get(
        token,
        "/o/c/routines",
        {
            "filter": (
                f"name eq '{routine_name}'"
                f" and r_routineToProjects_c_projectId eq '{MASTER_PROJECT_ID}'"
            ),
            "pageSize": 1,
        },
    )
    routines = routine_data.get("items", [])

    if not routines:
        raise RuntimeError(
            f"Routine '{routine_name}' not found in master project"
        )

    routine_id = routines[0]["id"]
    results_data = _get(
        token,
        "/o/c/caseresults",
        {
            "filter": f"r_caseToCaseResult_c_caseId eq '{case_id}'",
            "pageSize": 50,
            "sort": "dateCreated:desc",
        },
    )

    for cr in results_data.get("items", []):
        if cr.get("dueStatus", {}).get("key") == "UNTESTED":
            continue

        build_id = cr.get("r_buildToCaseResult_c_buildId")

        if not build_id:
            continue

        build = _get(token, f"/o/c/builds/{build_id}")

        if build.get("r_routineToBuilds_c_routineId") == routine_id:
            return cr["id"]

    raise RuntimeError(
        f"No case result found for '{name}' on routine '{routine_name}'"
    )


def _resolve_url_to_case_result_id(token, url):
    match = re.search(r"/build/(\d+)", url)

    if not match:
        raise RuntimeError(f"Could not parse build ID from URL: {url}")

    build_id = match.group(1)
    parsed = urllib.parse.urlparse(url)
    fragment = parsed.fragment
    team_ids = []

    if "?" in fragment:
        fragment_qs = fragment.split("?", 1)[1]
        filter_vals = urllib.parse.parse_qs(fragment_qs).get("filter", [])

        if filter_vals:
            try:
                filter_data = json.loads(urllib.parse.unquote(filter_vals[0]))
                team_ids = filter_data.get("testrayTeamIds", [])
            except (json.JSONDecodeError, KeyError):
                pass

    print(f"Build ID: {build_id}, Team IDs: {team_ids}", file=sys.stderr)
    filter_parts = [
        f"r_buildToCaseResult_c_buildId eq '{build_id}'",
        "dueStatus eq 'FAILED'",
    ]

    if team_ids:
        team_filter = " or ".join(
            f"r_teamToCaseResult_c_teamId eq '{t}'" for t in team_ids
        )
        filter_parts.append(f"({team_filter})")

    data = _get(
        token,
        "/o/c/caseresults",
        {
            "filter": " and ".join(filter_parts),
            "pageSize": 50,
            "sort": "dateCreated:desc",
        },
    )
    items = data.get("items", [])

    if not items:
        raise RuntimeError(f"No failed case results found for build {build_id}")

    return items[0]["id"]


def _fetch_failure_data(token, case_result_id):
    cr = _get(token, f"/o/c/caseresults/{case_result_id}")
    status = cr.get("dueStatus", {}).get("key", "UNKNOWN")
    case_id = cr.get("r_caseToCaseResult_c_caseId")
    case_data = _get(
        token,
        "/o/c/cases",
        {
            "filter": f"id eq '{case_id}'",
            "pageSize": 1,
        },
    )
    case_obj = case_data["items"][0] if case_data.get("items") else {}
    name = case_obj.get("name")

    if status == "PASSED":
        return {"caseResultId": case_result_id, "name": name, "status": "PASSED"}

    if name and "PortalLogAssertor" in name:
        test_type = "Java Log Assertor"
    else:
        case_type_id = case_obj.get("r_caseTypeToCases_c_caseTypeId")

        if case_type_id:
            type_data = _get(
                token,
                "/o/c/casetypes",
                {"filter": f"id eq '{case_type_id}'"},
            )
            raw_type = (
                type_data["items"][0]["name"] if type_data.get("items") else "Unknown"
            )
            test_type = _TYPE_MAP.get(raw_type, raw_type)
        else:
            test_type = "Unknown"

    error_trace = cr.get("errors", "")
    failure_date = cr.get("dateCreated", "")
    last_pass_sha = None
    first_fail_sha = None
    skip_history = not name or "Top Level Build" in name or test_type == "Java Log Assertor"

    if not skip_history:
        build_id = cr.get("r_buildToCaseResult_c_buildId")
        build = _get(token, f"/o/c/builds/{build_id}")
        routine_id = build.get("r_routineToBuilds_c_routineId")
        history = _get(
            token,
            f"/o/testray-rest/v1.0/testray-case-result-history/{case_id}",
            {
                "pageSize": 300,
                "sort": "executionDate:desc",
            },
        )
        entries = [
            e
            for e in history.get("items", [])
            if str(e.get("testrayRoutineId")) == str(routine_id)
        ]
        pass_idx = None

        for i, entry in enumerate(entries):
            if entry.get("status") == "PASSED":
                last_pass_sha = entry.get("gitHash")
                pass_idx = i
                break

        if pass_idx is not None:
            failed_before_pass = [
                e for e in entries[:pass_idx] if e.get("status") == "FAILED"
            ]

            if failed_before_pass:
                first_fail_sha = failed_before_pass[-1].get("gitHash")
        else:
            failed_entries = [e for e in entries if e.get("status") == "FAILED"]

            if failed_entries:
                first_fail_sha = failed_entries[-1].get("gitHash")

    return {
        "caseResultId": case_result_id,
        "errorTrace": error_trace,
        "failureDate": failure_date,
        "firstFailSha": first_fail_sha,
        "lastPassSha": last_pass_sha,
        "name": name,
        "status": status,
        "type": test_type,
    }


def main():
    if len(sys.argv) < 2:
        print(
            "Usage: fetch-failure-data.py <caseResultId | testName | testrayBuildUrl>",
            file=sys.stderr,
        )
        sys.exit(1)

    arg = sys.argv[1]
    repo_path = os.environ.get("REPO_PATH", os.getcwd())

    try:
        token = _get_token()

        if arg.isdigit():
            case_result_id = int(arg)
        elif arg.startswith("https://testray.liferay.com"):
            case_result_id = _resolve_url_to_case_result_id(token, arg)
        else:
            case_result_id = _resolve_name_to_case_result_id(token, arg, repo_path)

        result = _fetch_failure_data(token, case_result_id)
        print(json.dumps(result, indent=2))

    except RuntimeError as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()

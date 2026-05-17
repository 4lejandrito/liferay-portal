# Testray Fetch Script

The script at `.claude/skills/test-fix/fetch-failure-data.py` handles all Testray API calls for the `test-fix` skill.

## Input

A single positional argument — one of:

- A positive integer case result ID.
- A Testray build URL (`https://testray.liferay.com/#/project/.../build/<buildId>?filter=...`).
- A test name string (resolved through the master project's team routine via `.github/CODEOWNERS`).

## Environment

- `TESTRAY_CLIENT_ID` and `TESTRAY_CLIENT_SECRET` — OAuth2 client credentials (required).
- `REPO_PATH` — path to the `liferay-portal` checkout used for CODEOWNERS lookup (default: `cwd`).

## Output

JSON on stdout:

| Field | Type | Description |
| --- | --- | --- |
| `caseResultId` | integer | Resolved case result ID. |
| `name` | string | Test name (class, spec, or method). |
| `status` | string | Testray status key (`PASSED`, `FAILED`, …). |
| `type` | string | Test type (`Java Integration`, `Java Unit`, `JavaScript`, `Playwright`, `Poshi`, `Java Semantic Versioning`). Omitted when `status` is `PASSED`. |
| `errorTrace` | string | Error trace from the test framework. Omitted when `status` is `PASSED`. |
| `failureDate` | string | ISO timestamp of the case result. Omitted when `status` is `PASSED`. |
| `lastPassSha` | string\|null | Git hash of the most recent passing run, or `null`. Omitted when `status` is `PASSED`. |
| `firstFailSha` | string\|null | Git hash of the oldest failing run before the last pass, or `null`. Omitted when `status` is `PASSED`. |

## Errors

Any error prints a message to stderr and exits non-zero.
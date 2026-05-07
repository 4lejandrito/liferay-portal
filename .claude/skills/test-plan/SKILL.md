---

allowed-tools: [Agent, Bash, Glob, Grep, Read, Write]
argument-hint: ""
description: Generate a targeted local test plan for branch changes. Use when the user wants to know which tests to run before merging, asks for a test plan, wants to validate changes locally, or mentions running tests for their branch.
name: test-plan

---

# Test Plan Generator

Produce a runnable shell script that executes the tests most likely to regress given the current branch's changes compared to `master`.

The full test suite takes hours to run, so the team merges aggressively and relies on a daily full-suite run that delivers results within 24 hours of merge. This skill produces a focused pre-merge script (under 20 minutes) that mitigates risk without attempting full coverage — the goal is to catch the most likely regressions, not every possible one.

## Input

### Branch Changes

The set of commits on top of `master` and the files they touch. Read what the changes actually do — not merely which files were touched, but what behavior changed. The understanding of behavior drives the test selection.

### Test Organization

Liferay's test layout (which test type lives where, naming patterns, project conventions). The detailed reference is at `${CLAUDE_SKILL_DIR}/references/test-organization.md`; consult it to translate "code area X regressed" into specific test files.

## Expected Output

### Test Script

A self-contained `bash` script written to `<repo-root>/test.sh`, executable via `bash test.sh`. Replace any existing `test.sh`. The script:

- Has a header naming the branch, generation date, estimated runtime against the 20-minute budget, commit count, file count, and affected areas.

- Sets `EXIT_CODE=0` at the top, suffixes every test command with `|| EXIT_CODE=1` so failures are recorded without halting execution, and exits with `${EXIT_CODE}` so the script returns `0` only when all tests pass.

- Resolves a `REPO_ROOT` variable with `REPO_ROOT="$(cd "$(dirname "${0}")" && pwd)"` and uses it in every path.

- Calls Gradle through `"${REPO_ROOT}/gradlew" --project-dir "${REPO_ROOT}/modules"` and Playwright through `npx --prefix "${REPO_ROOT}/modules/test/playwright" playwright test`.

- Assumes the portal is already running. Unit, Integration, Playwright, and Poshi tests all run directly.

- Precedes each test command with a single-line comment stating **why** the test was selected — the rationale, not a restatement of the test name or module.

When the changes are purely cosmetic (formatting, comments), the script exits `0` with a header explaining why.

After writing the script, mark it executable and instruct the user to run it via `./test.sh`.

### Selected Tests

Tests are chosen by blast radius and prioritized within the 20-minute budget.

**Blast radius — what could regress:**

- **API or interface changes** (`portal-kernel`, `*-api` modules) — anything that depends on the changed API. Search for consumers.

- **Mechanical or repetitive changes** (e.g., adding a property across 200 files) — the core logic test plus a representative sample of end-to-end tests from affected modules.

- **Service implementation changes** — tests for the service itself plus tests for features that depend on it.

- **Shared infrastructure changes** (a registry, a framework class, a base class) — representative tests spanning the modules that rely on that infrastructure.

- **Web layer changes** — Playwright and Poshi tests for the affected UI.

The objective is not to "find every test in modules that were touched" — it is to find tests that exercise the code paths that changed.

**Priority order within the budget:**

Always include:

1. Unit tests for directly changed code — fast (approximately 5–15 seconds per class) and highest signal.

1. Integration tests that directly exercise the changed functionality.

1. Tests that exercise the core logic change end-to-end.

Include when budget allows:

1. Representative integration tests from affected downstream modules — a few that cover distinct usage patterns rather than all.

1. Playwright tests for affected web modules (approximately 1–3 minutes per spec).

Include when still within budget:

1. Poshi tests (approximately 2–5 minutes each).

1. Additional downstream module tests for broader coverage.

When the change affects many modules (for example, a framework change), do not test every module. Choose a diverse sample that covers different usage patterns of the changed code.

Verify every test file exists before adding it to the script.
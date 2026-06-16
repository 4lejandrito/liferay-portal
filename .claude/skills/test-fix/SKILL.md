---

allowed-tools: [Bash, Edit, Glob, Grep, Read, Skill, Write]
argument-hint: '<testflowUrl | caseResultId | testName | testrayBuildUrl>'
description: Resolve a single Liferay test failure end-to-end, or analyze all failures in a Testray testflow and create one Jira ticket per distinct root cause.
name: test-fix

---

# Fix a Test Failure or Analyze a Testflow

## Input Routing

Inspect `${ARGUMENTS}` and route to the appropriate mode:

- `${ARGUMENTS}` matches `testray.liferay.com/#/testflow/` → **Testflow Batch Mode**
- Everything else (case result ID, test name, build URL) → **Single-Test Mode**

---

## Single-Test Mode

Resolve a single test failure end-to-end.

## Preconditions

- Tomcat is running (required for `Java Integration`, `Playwright`, and `Poshi`). Start it if it is not.

## Input

### Case Result ID

When `${ARGUMENTS}` is a positive integer, use it directly as the Testray case result ID.

### Testray Build URL

When `${ARGUMENTS}` is a URL of the form `https://testray.liferay.com/#/project/<projectId>/routines/<routineId>/build/<buildId>?filter=<urlencoded-json>`, resolve it to a case result ID by following [`references/testray.md`](references/testray.md). The procedure returns a case result ID that the rest of the workflow consumes identically to a user-supplied one.

### Test Name

When `${ARGUMENTS}` is anything else, resolve it to a case result ID by following [`references/testray.md`](references/testray.md). When the resolution aborts, surface the reason and ask the user to retry with the case result ID directly.

### Failure Data

Fetched at the start of the run by following [`references/testray.md`](references/testray.md), which covers authentication, name-to-ID resolution, and how to derive each field. When a test name was passed and the resolution aborts, surface the reason and ask the user to retry with the case result ID directly. When the case result is already `PASSED`, skip the workflow and exit with `Verdict: No fix needed`. When it is `BLOCKED` — a tester deliberately flagged it, so it must not be autofixed — skip the workflow and exit reporting that the case is blocked. Otherwise, the procedure returns these fields:

- **buildSha** — commit the failing build was tested against.
- **errorTrace** — error trace produced by the test framework.
- **failureDate** — timestamp when the case result was recorded, used to scope the duplicate-ticket check in **Claim the Failure**.
- **firstFailSha** — first commit where the test failed (may be `null` when the case has no recorded failure history).
- **lastPassSha** — commit where the test last passed (may be `null` when the case has no recent pass on record).
- **name** — test name (class, spec, or method).
- **type** — one of `Java Integration`, `Java Semantic Versioning`, `Java Unit`, `JavaScript`, `Playwright`, `Poshi`.

## Expected Output

### Name

The test name (class, spec, or method) returned by the Testray fetch. When the fetch fails before a name is known, use `case-result <CASE_RESULT_ID>`.

### Type

The test type returned by the Testray fetch (one of the values listed under **Input**). Use `Unknown` when the fetch fails before a type is known.

### Verdict

One of:

- `Bug in portal` — product code carried the fix.
- `No fix needed` — the test passed locally on the first reproduction; nothing was changed.
- `Outdated test` — the test carried the fix.
- `Unresolved` — investigation did not converge, or any step aborted.

### Conclusion

One sentence describing the outcome:

- For `Bug in portal` and `Outdated test`, name the offending commit (short SHA and subject) and what it changed.
- For `No fix needed`, the literal string `Test passes locally`.
- For `Unresolved`, an honest handover summary listing hypotheses considered, attempts made, observed effects, and the most plausible remaining lead.

### Resolution Time

The elapsed time of the run, formatted as `<minutes>m <seconds>s`.

### Jira Tickets

The Task created in **Claim the Failure** is the persistent ticket of record for every verdict. Update it at the end of the run based on the verdict:

- **Bug in portal** — invoke the `jira-bug` skill to create a separate Bug describing the regression. The title summarizes the regression. The description carries the failing test name, the trace, and the reproduction steps derived from the test scenario. Do **not** add the `claude-test-fix` label to the Bug — that label belongs to the Task alone, so the duplicate-ticket check in **Claim the Failure** matches one ticket per failure. Link the Bug to the Task with the **Fix** issue link type so the Task surfaces it as **is fixed by**. Return the Bug URL alongside the Task URL.

- **Outdated test** — return the Task URL.

- **No fix needed** — close the Task as `Won't Do` with a comment containing the literal `Test passes locally`. No PR is opened.

- **Unresolved** — leave the Task in **In Progress**, append the handover summary as a comment, and return its URL so the human picking it up has a single landing page.

### Pull Request

Only when the test was fixed (verdict `Bug in portal` or `Outdated test`): the URL of the pull request opened for the fix.

Make a commit, then find the owner of the changed files using `<repo-root>/.github/CODEOWNERS` and invoke the `pr` skill with it as the target repository. Override the user's title-only default and pass the body content explicitly so the pull request explains the regression.

Use this template. The browse URL on the first line points at the ticket the `pr` skill resolves (the Technical Task subtask, not the parent Task), so reviewers land on the same ticket where the pull request URL is recorded:

```markdown
https://liferay.atlassian.net/browse/<TICKET>

## Failing Test

`<test-name>`

`<test-path>`

\`\`\`
<errorTrace>
\`\`\`

## Root Cause

Commit `<short-sha>` ("<subject>") <one or two sentences>.

## Fix

<one paragraph explaining the change and why it works>.

- `<file-1>`
- `<file-2>`
```

## Workflow

### Claim the Failure

1. Check Jira for an LPD ticket whose summary contains `<test-name>` and is labeled `claude-test-fix`. Decide whether it already covers this failure by its state:

	- **Unresolved** (Open, In Progress, or any nonresolved state) → claimed, skip. Someone is already working on it.
	- **Resolved** → find the ticket's PR by following the `pr` skill's rules for where it is recorded, derive its fix commit, and test whether it already reached the build that failed, judging by `git merge-base --is-ancestor <fixSha> <buildSha>`:
		- **Fix commit is an ancestor of `<buildSha>`** → the fix was already present when this build ran, yet the test still failed, so it does not cover this occurrence → proceed.
		- **Fix commit is not yet in `<buildSha>`** → Testray has not retested since the fix merged, so the failure is already addressed → skip.
		- **No fix commit found** (test-only ticket, unmerged commit, or a nonfixing resolution) → fall back to the resolution date: resolved before `<failureDate>` proceeds, resolved on or after skips.

	When skipping and other candidates remain, retry with the next one.

1. Invoke the `jira-task` skill with summary `<test-name>` and a description that names the case result ID, the source build, and the failure trace excerpt. Add the `claude-test-fix` label.

1. Invoke the `start-work` skill on the new Task.

### Reproduce Locally

This step runs **before** any range or commit analysis. The test may already pass locally — when it does, the run ends here without any further investigation.

#### Set Feature Flags

Inspect the test source to discover which feature flags it depends on. Mirror the CI setup before reproducing. Otherwise, the test path differs.

- **Poshi tests** require flags in `<bundles>/portal-ext.properties` with Tomcat restarted to pick them up. Before editing the file for the first time in this run, snapshot it so it can be restored later. Then, strip every existing `feature.flag.*` entry and add only the flags the test requires — the file must end up with the test's flags and nothing else, so unrelated flags left over from previous runs cannot interfere. The original snapshot is restored later in **Restore the Portal**. Bounce Tomcat for the new flag values to take effect.

- **Playwright tests** declare flags through the `featureFlagsTest` fixture under `modules/test/playwright/fixtures`. The fixture toggles them per test — no portal change is needed.

#### Run the Test

Run the test, deploying first when the type requires it. For `Java Semantic Versioning`, the "test" is `<gradlew> baseline` from the failing module — strictly an API contract check, not a behavioral test. For test types that exercise the runtime (`Java Integration`, `Playwright`, `Poshi`), also read the server log after the run; it captures portal-side exceptions, deployment errors, and stack traces that never reach **errorTrace**, and frequently names the real failure. Then compare the local outcome with **errorTrace**:

- **Test passes** → check whether a commit between `${FIRST_FAIL_SHA}` and `HEAD` already addresses the failure. When one does, exit with `Verdict: No fix needed`. Otherwise, reason about why the test failed in CI (the test may be flaky or fail for environmental reasons). Try to fix it and rerun to confirm. When no plausible cause surfaces, exit with `Verdict: No fix needed`. Skip **Identify Suspect Commits** and **Iterate Through Suspects** in either case.
- **Same failure** → continue to **Identify Suspect Commits**.
- **Different failure** → surface the diff and ask the user whether to proceed. When the user is unreachable or declines, mark the failure as `Unresolved` with a `Conclusion` summarizing both traces (the one returned by the Testray fetch and the one observed locally) and exit.

### Identify Suspect Commits

The breaking change lies between `${LAST_PASS_SHA}` and `${FIRST_FAIL_SHA}`. List candidates from the diff between those two commits, then narrow by tracing the line history of the file owning the line nearest the failing assertion or the topmost frame in **errorTrace**.

When that does not point to a single commit, rank candidates: files in the test's own module first, then modules whose packages the test imports, then `*-api` / `portal-kernel` / shared `frontend-js-*`, then `portal-impl` / `petra-*` / shared infrastructure.

### Iterate Through Suspects

Apply candidate fixes as uncommitted changes; the **Pull Request** step commits them later. For each suspect in ranked order:

1. Read its documented intent — the commit message and diff, the linked `LPD-XXXXX` ticket (summary, issue type, description) when the subject carries one, and the body of the merged pull request that introduced the commit:

	```bash
	gh pr list --json number,title,body --repo brianchandotcom/liferay-portal --search "<sha>" --state merged
	```

	Look for explicit references to the failing test or asserted behavior, and for any sign that the change deliberately drops the contract the assertion was checking.

1. Apply a fix that touches the suspect's hunks. The fix must live inside the diff between `${LAST_PASS_SHA}` and `${FIRST_FAIL_SHA}` — that is the only place the regression can live, and a fix outside that range means the diagnosis is wrong. Never escalate the scope of the fix to force convergence. Adapt the test (`Outdated test`) — including removing, weakening, or `@Ignore`-ing an assertion — only when the offending commit's documentation (subject, linked Jira ticket, or PR body) explicitly states the contract change the assertion was checking; without that documented justification, the assertion is correct and the regression lives in product code (`Bug in portal`).

1. Run the test again.

When the test turns green, do **not** lock in the verdict immediately — keep reading the remaining suspects to confirm none of them is a stronger explanation. Settling on the first green fix is how a wrong fix gets shipped; only commit once no better candidate surfaces.

When the current candidate set is exhausted without green, broaden it (next-ranked files, infrastructure) and iterate again — up to **three rounds**. After the third round without convergence, or when candidates are exhausted, mark the failure as `Unresolved` with a `Conclusion` listing the suspects analyzed, attempts made, what each changed about the failure, and the most plausible remaining lead. Run the cleanup in **Restore the Portal** and exit.

Once the verdict is locked in (only ever after a green local run — never commit or open a PR otherwise), record the offending commit (short SHA + subject) and one sentence explaining how it broke the test — reused in the PR body's Root Cause section (see **Pull Request**).

### Restore the Portal

This step is idempotent: the portal must end the run in the same state it started — Tomcat running with the original `portal-ext.properties` loaded.

When **Set Feature Flags** changed `<bundles>/portal-ext.properties`, restore the snapshot and bounce Tomcat to pick the original properties back up.

When **Set Feature Flags** was skipped because the test does not need flag changes, Tomcat keeps running untouched and there is nothing to do.

---

## Testflow Batch Mode

Analyzes all non-passing case results in a testflow for a configurable team. Downloads every console log from GCS, groups failures by distinct root-cause error, bisects the causative commit from git log, then creates one Jira ticket per confirmed distinct error and links it back to the Testray subtasks.

### Preconditions

- Testray auth: `${TESTRAY_CLIENT_ID}` + `${TESTRAY_CLIENT_SECRET}`, **or** `${ACCESS_TOKEN}` set directly. When none are present, follow the Browser Token Fallback in `references/testray.md` before aborting.
- `~/google-cloud-sdk/bin/gsutil` installed and authenticated via `gcloud auth login` (used to download console logs from GCS)
- Jira credentials in `~/.bashrc.d/jira.sh` (exports `JIRA_API_USER`, `JIRA_API_TOKEN`)

### Authentication

Resolve auth once per run by following `references/testray.md`. That procedure sets either `${ACCESS_TOKEN}` or `${TESTRAY_COOKIE}` and defines the `testray_curl` shell function and the `testray_fetch` Python helper. Use `testray_curl` for shell-level API calls and `testray_fetch` for every curl inside a Python script throughout this mode.

### Step 1 — Extract Task ID and Resolve Team

```bash
TASK_ID=$(echo "${ARGUMENTS}" | grep -oE 'testflow/([0-9]+)' | grep -oE '[0-9]+')

echo "Task ID: ${TASK_ID}"

# Scope all intermediate files to this run — prevents collisions when two
# testflows are analyzed concurrently.
export TASK_ID

CR_ALL="/tmp/testray-${TASK_ID}-cr-all.json"
META="/tmp/testray-${TASK_ID}-meta.json"
FINDINGS="/tmp/testray-${TASK_ID}-findings.json"
GROUPS="/tmp/testray-${TASK_ID}-groups.json"
HISTORIES="/tmp/testray-${TASK_ID}-histories.json"
SUBTASK_ISSUES="/tmp/testray-${TASK_ID}-subtask-issues.json"
EARLY_JIRA="/tmp/testray-${TASK_ID}-early-jira.json"
JIRA_CHECK="/tmp/testray-${TASK_ID}-jira-check.json"
TICKET_MAP="/tmp/testray-${TASK_ID}-ticket-map.json"
ANALYSIS="/tmp/testray-${TASK_ID}-analysis.json"
```

If `${TASK_ID}` is empty, ask the user to provide the testflow URL.

Fetch the full teams list and resolve which team to filter by. Cache it in `/tmp/testray-teams.json` for name resolution throughout the run. The default team is stored in `~/.config/testray-analyze/config.json` and persists across sessions.

```bash
CONFIG_FILE="${HOME}/.config/testray-analyze/config.json"
mkdir -p "$(dirname "${CONFIG_FILE}")"

testray_curl \
	--header "Accept: application/json" \
	--silent \
	--url "https://testray.liferay.com/o/c/teams?pageSize=200" \
	> /tmp/testray-teams.json

python3 << 'PYEOF'
import json
teams = json.load(open('/tmp/testray-teams.json')).get('items', [])
for i, t in enumerate(sorted(teams, key=lambda x: x['name']), 1):
    print(f'  {i:>3}.  {t["id"]:>10}  {t["name"]}')
PYEOF
```

If `~/.config/testray-analyze/config.json` does not exist, pause and ask the user to pick a default team from the printed list and provide values for the four Jira field IDs, then save their choice:

```bash
DEFAULT_TEAM_ID="<id from list>"
DEFAULT_TEAM_NAME="<name from list>"
JIRA_COMPONENT_ID="<component id>"
JIRA_TEAM_FIELD_ID="<team custom field id>"
JIRA_ISSUETYPE_ID="<issue type id>"
JIRA_VERSION_ID="<version id>"

python3 - << PYEOF
import json
json.dump(
    {
        'default_team_id': '${DEFAULT_TEAM_ID}',
        'default_team_name': '${DEFAULT_TEAM_NAME}',
        'jira_component_id': '${JIRA_COMPONENT_ID}',
        'jira_issuetype_id': '${JIRA_ISSUETYPE_ID}',
        'jira_team_field_id': '${JIRA_TEAM_FIELD_ID}',
        'jira_version_id': '${JIRA_VERSION_ID}',
    },
    open('${CONFIG_FILE}', 'w'), indent=2)
print(f'Default saved: ${DEFAULT_TEAM_NAME} (${DEFAULT_TEAM_ID})')
PYEOF
```

If the config file exists but is missing any of the four Jira field keys (`jira_component_id`, `jira_issuetype_id`, `jira_team_field_id`, `jira_version_id`), prompt for the missing values and update the file before continuing.

On all subsequent runs, resolve the team ID in this order: explicit argument → config default → abort with team list.

```bash
TEAM_ARG=$(echo "${ARGUMENTS}" | sed "s|.*testflow/[0-9]*/\?||" | xargs)

if [[ "${TEAM_ARG}" =~ ^[0-9]+$ ]]; then
    TEAM_ID="${TEAM_ARG}"
    TEAM_NAME="(ID ${TEAM_ID})"
elif [ -n "${TEAM_ARG}" ]; then
    TEAM_ID=$(python3 - << PYEOF
import json
arg = '${TEAM_ARG}'.lower()
teams = json.load(open('/tmp/testray-teams.json')).get('items', [])
match = next((t for t in teams if arg in t['name'].lower()), None)
print(match['id'] if match else '')
PYEOF
)
    TEAM_NAME="${TEAM_ARG}"
    if [ -z "${TEAM_ID}" ]; then
        echo "No team matched '${TEAM_ARG}' — check the list above and try again."
        exit 1
    fi
elif [ -f "${CONFIG_FILE}" ]; then
    TEAM_ID=$(python3 -c "import json; print(json.load(open('${CONFIG_FILE}')).get('default_team_id', ''))")
    TEAM_NAME=$(python3 -c "import json; print(json.load(open('${CONFIG_FILE}')).get('default_team_name', ''))")
else
    echo "No default team configured and no team argument provided."
    exit 1
fi

echo "Team: ${TEAM_NAME} (${TEAM_ID})"
```

### Step 2 — Fetch Build ID and All Non-Passing Case Results

Use `dueStatus ne 'PASSED'` — not `eq 'FAILED'` — to capture both FAILED and BLOCKED case results.

```bash
TASK_JSON=$(testray_curl \
	--header "Accept: application/json" \
	--silent \
	--url "https://testray.liferay.com/o/c/tasks/${TASK_ID}")

BUILD_ID=$(echo "${TASK_JSON}" | python3 -c \
	"import json,sys; print(json.load(sys.stdin).get('r_buildToTasks_c_buildId',''))")

export ACCESS_TOKEN BUILD_ID CSRF TEAM_ID TESTRAY_COOKIE
```

```bash
python3 << 'PYEOF'
import json, os, subprocess, urllib.parse

build_id = os.environ['BUILD_ID']
task_id  = os.environ['TASK_ID']
team_id  = os.environ['TEAM_ID']
token    = os.environ.get('ACCESS_TOKEN', '')
cookie   = os.environ.get('TESTRAY_COOKIE', '')
csrf     = os.environ.get('CSRF', '')
auth     = (['--header', f'Authorization: Bearer {token}'] if token
             else ['--compressed', '--header', f'Cookie: {cookie}', '--header', f'x-csrf-token: {csrf}'])

FIELDS = (
    'attachments,dueStatus,errors,id'
    ',r_caseToCaseResult_c_caseId'
    ',r_subtaskToCaseResults_c_subtaskId'
)
FILTER = urllib.parse.quote(
    f"r_buildToCaseResult_c_buildId eq '{build_id}'"
    f" and r_teamToCaseResult_c_teamId eq '{team_id}'"
    f" and dueStatus ne 'PASSED'"
)

def fetch_page(page):
    url = (
        f'https://testray.liferay.com/o/c/caseresults'
        f'?fields={FIELDS}&filter={FILTER}&page={page}&pageSize=200'
    )
    r = subprocess.run(
        ['curl', '--header', 'Accept: application/json', '--silent', '--url', url] + auth,
        capture_output=True, text=True)
    try:
        return json.loads(r.stdout)
    except json.JSONDecodeError:
        print(f'  WARN page {page}: non-JSON response: {r.stdout[:120]}')
        return {'items': [], 'totalCount': 0}

all_items = []
page      = 1
total     = None

while True:
    data  = fetch_page(page)
    if total is None:
        total = data.get('totalCount', 0)
    items = data.get('items', [])
    all_items.extend(items)
    print(f'  Page {page}: {len(items)} items  (running total {len(all_items)}/{total})')
    if len(all_items) >= total or not items:
        break
    page += 1

json.dump({'totalCount': total, 'items': all_items},
          open(f'/tmp/testray-{task_id}-cr-all.json', 'w'), indent=2)
print(f'\nFetched {len(all_items)}/{total} non-passing case results')

# Try to read each unique subtask's `issues` field (linked Jira tickets).
# The Testray UI shows these in the "Ticket" column for each subtask. Fetching
# them here lets Step 7 find already-linked tickets without relying on Jira text
# search, which can miss recently-created tickets due to index lag.
subtask_ids = list({
    str(item.get('r_subtaskToCaseResults_c_subtaskId', ''))
    for item in all_items
    if item.get('r_subtaskToCaseResults_c_subtaskId')
})

subtask_issues = {}
print('\nFetching subtask issues:')
for st_id in subtask_ids:
    url = f'https://testray.liferay.com/o/c/subtasks/{st_id}?fields=id,issues'
    r = subprocess.run(
        ['curl', '--header', 'Accept: application/json', '--silent', '--url', url] + auth,
        capture_output=True, text=True)
    try:
        data   = json.loads(r.stdout)
        issues = data.get('issues', '') or ''
        # The Testray UI stores ticket keys with U+2013 en dash instead of hyphen.
        # Normalize before splitting so "LPD–91364" becomes "LPD-91364".
        issues = issues.replace('–', '-').replace('—', '-')
        keys   = [k.strip() for k in issues.split(',') if k.strip()]
        if keys:
            subtask_issues[st_id] = keys
            print(f'  Subtask {st_id}: {keys}')
        else:
            print(f'  Subtask {st_id}: no issues linked')
    except Exception:
        print(f'  Subtask {st_id}: unable to read (permission or parse error)')

json.dump(subtask_issues, open(f'/tmp/testray-{task_id}-subtask-issues.json', 'w'), indent=2)
PYEOF
```

### Step 3 — Download All Console Logs

Iterate over every case result. Skip already-cached logs to make reruns fast.

```bash
python3 << 'PYEOF'
import json, os, subprocess

task_id  = os.environ['TASK_ID']
cr_data  = json.load(open(f'/tmp/testray-{task_id}-cr-all.json'))
items    = cr_data.get('items', [])
results  = []
gsutil   = os.path.expanduser('~/google-cloud-sdk/bin/gsutil')

for item in items:
    cr_id      = item.get('id')
    subtask_id = str(item.get('r_subtaskToCaseResults_c_subtaskId', ''))
    case_id    = item.get('r_caseToCaseResult_c_caseId')
    errors     = item.get('errors', '')
    status     = item.get('dueStatus', {}).get('key', '?')

    attachments = item.get('attachments') or []
    if isinstance(attachments, str):
        attachments = json.loads(attachments)

    gcs_path = next(
        (a.get('value', '') for a in attachments if a.get('name') == 'Jenkins Console'),
        None)

    record = {'cr_id': cr_id, 'case_id': case_id, 'subtask_id': subtask_id,
              'subtask_error': errors, 'status': status}

    if not gcs_path:
        record['log_path']  = None
        record['gsutil_ok'] = False
        results.append(record)
        print(f'  SKIP {cr_id} (subtask {subtask_id}, {status}) — no Jenkins Console attachment')
        continue

    gcs_url  = 'gs://testray-results/' + gcs_path.split('?')[0]
    log_path = f'/tmp/testray-log-{cr_id}.log'
    record['gcs_url']  = gcs_url
    record['log_path'] = log_path

    if os.path.exists(log_path):
        record['gsutil_ok'] = True
        print(f'  CACHED {cr_id} (subtask {subtask_id}, {status})')
        results.append(record)
        continue

    print(f'  DL    {cr_id} (subtask {subtask_id}, {status}) ...')
    proc = subprocess.run([gsutil, 'cp', gcs_url, log_path],
                          capture_output=True, text=True)
    record['gsutil_ok'] = proc.returncode == 0
    if not record['gsutil_ok']:
        print(f'        FAILED: {proc.stderr[:100]}')
    results.append(record)

json.dump(results, open(f'/tmp/testray-{task_id}-meta.json', 'w'), indent=2)
ok = sum(1 for r in results if r.get('gsutil_ok'))
print(f'\nDownloaded {ok}/{len(results)} logs')
PYEOF
```

### Step 4 — Extract Test Name and Root-Cause Error from Each Log

Capture both `ERROR`/`WARN` lines and `Caused by:` lines so the normalize step can find the innermost DB exception even when wrapped in multiple layers.

```bash
python3 << 'PYEOF'
import json, os, re

task_id  = os.environ['TASK_ID']
meta     = json.load(open(f'/tmp/testray-{task_id}-meta.json'))
findings = []

for item in meta:
    log_path      = item.get('log_path')
    subtask_id    = item.get('subtask_id', '')
    case_id       = item.get('case_id')
    cr_id         = item.get('cr_id')
    subtask_error = item.get('subtask_error', '')
    status        = item.get('status', '')

    if not log_path or not item.get('gsutil_ok'):
        findings.append({'subtask_id': subtask_id, 'case_id': case_id, 'cr_id': cr_id,
                         'status': status, 'test': f'cr-{cr_id}',
                         'error': subtask_error or '(no log)'})
        continue

    try:
        log = open(log_path).read()
    except OSError:
        findings.append({'subtask_id': subtask_id, 'case_id': case_id, 'cr_id': cr_id,
                         'status': status, 'test': f'cr-{cr_id}',
                         'error': '(log download failed)'})
        continue

    method_match = re.match(r'^([a-z][a-zA-Z]+):', subtask_error)

    if method_match:
        method    = method_match.group(1)
        tc_match  = re.search(rf'Testcase: ([^\(]+\({re.escape(method)}\))', log)
        test_name = tc_match.group(1).strip() if tc_match else method
        err_match = re.search(
            rf'{re.escape(method)}.*?FAILED.*?\n(.*?)(?=\n\n|\Z)', log, re.DOTALL)
        error_text = err_match.group(1).strip()[:600] if err_match else subtask_error
    else:
        # Poshi/Ant echo format: LocalFile.TestClass#TestMethod
        echo_match = re.search(
            r'\[echo\]\s+(LocalFile\.[A-Za-z]+#[A-Za-z]+)\.',
            log)
        if echo_match:
            test_name = echo_match.group(1)
        else:
            tc_match  = re.search(r'test\.class\.method\.name=(.+)', log)
            test_name = tc_match.group(1).strip() if tc_match else f'cr-{cr_id}'
        error_lines = [
            line.strip() for line in log.splitlines()
            if re.search(r'\b(ERROR|WARN)\b', line)
               or re.search(r'Caused by:', line)
        ]
        error_text = '\n'.join(error_lines) if error_lines else '(no ERROR/WARN lines found in log)'

    findings.append({'subtask_id': subtask_id, 'case_id': case_id, 'cr_id': cr_id,
                     'status': status, 'test': test_name, 'error': error_text})

json.dump(findings, open(f'/tmp/testray-{task_id}-findings.json', 'w'), indent=2)
print(f'Extracted findings for {len(findings)} case results')
PYEOF
```

### Step 5 — Group by Distinct Root-Cause Error

Walk the `Caused by:` chain to reach the innermost exception. Strip log prefixes, Oracle/DB2 table aliases, UUIDs, and setup noise before grouping.

```bash
python3 << 'PYEOF'
import json, os, re
from collections import defaultdict

task_id  = os.environ['TASK_ID']
findings = json.load(open(f'/tmp/testray-{task_id}-findings.json'))

NOISE = re.compile(
    r'database "[^"]+" does not exist'
    r'|role "[^"]+" does not exist'
    r'|Unable to decode part'
    r'|psql:/tmp/create\.sql'
    r'|psql:/tmp/postgresql\.sql'
)

WRAPPER = re.compile(
    r'SystemException'
    r'|ORMException'
    r'|PersistenceException'
    r'|UpgradeException'
    r'|org\.hibernate\.exception\.SQLGrammarException'
)

def strip_log_prefix(line):
    return re.sub(r'^(\s*\[[^\]]+\]\s*)+', '', line).strip()

def normalize(error):
    lines = error.splitlines()

    caused_by = []
    for l in lines:
        if 'Caused by:' not in l:
            continue
        stripped = strip_log_prefix(l)
        if not NOISE.search(stripped):
            caused_by.append(stripped)

    if caused_by:
        e = caused_by[-1]
        for cb in caused_by:
            if not WRAPPER.search(cb):
                e = cb
                break
    else:
        meaningful = []
        for l in lines:
            stripped = strip_log_prefix(l)
            if stripped and not NOISE.search(stripped):
                meaningful.append(stripped)
        e = meaningful[0] if meaningful else '(unknown — setup noise only)'

    if re.match(r'\d{4}-\d{2}-\d{2}.*INFO\b', e) or re.match(r'<ts>\s+INFO\b', e):
        e = '(INFO only — no real error)'

    e = re.sub(
        r'(PortalInstanceLifecycleListenerManagerImpl:\d+\] Unable to register portal instance).*',
        r'\1', e)
    e = re.sub(r'ORA-00904: "[A-Z][A-Z0-9_]*"\."([^"]+)"', r'ORA-00904: "\1"', e)
    e = re.sub(r'SQLERRMC=[A-Z0-9_]+\.([A-Z][A-Z0-9_]+)', r'SQLERRMC=\1', e)
    e = re.sub(r'[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}', '<uuid>', e)
    e = re.sub(r'\(conn=\d+\)\s*', '', e)
    e = re.sub(r'\b[a-z][a-z0-9]+_\d*\.', '', e)
    e = re.sub(r'\b0x[0-9a-fA-F]+\b', '<addr>', e)
    e = re.sub(r'\d{4}-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}[^\s]*', '<ts>', e)

    return e.strip() or '(unknown)'

groups = defaultdict(list)
for f in findings:
    key = normalize(f['error'])
    groups[key].append(f)

result = [{'key': k, 'cases': v} for k, v in sorted(groups.items(), key=lambda x: -len(x[1]))]
json.dump(result, open(f'/tmp/testray-{task_id}-groups.json', 'w'), indent=2)

print(f'{len(result)} distinct error group(s) across {len(findings)} case result(s):\n')
for i, g in enumerate(result, 1):
    subtasks = sorted({c["subtask_id"] for c in g["cases"] if c["subtask_id"]})
    statuses = sorted({c["status"] for c in g["cases"]})
    print(f'  [{i}] [{len(g["cases"])}x] {g["key"][:100]}')
    print(f'        subtasks={subtasks}  statuses={statuses}')
PYEOF
```

### Step 6 — Case History: Find Last Pass and First Fail

For each error group, pick one representative case ID. Query its full history, find the last-pass / first-fail boundary, then fetch the git SHA for each build and run `git log` to identify causative commits.

```bash
export ACCESS_TOKEN TESTRAY_COOKIE CSRF
```

```bash
python3 << 'PYEOF'
import json, os, re, subprocess, urllib.parse

task_id = os.environ['TASK_ID']
groups  = json.load(open(f'/tmp/testray-{task_id}-groups.json'))
team_id = os.environ['TEAM_ID']
token   = os.environ.get('ACCESS_TOKEN', '')
cookie  = os.environ.get('TESTRAY_COOKIE', '')
csrf    = os.environ.get('CSRF', '')
auth    = (['--header', f'Authorization: Bearer {token}'] if token
           else ['--compressed', '--header', f'Cookie: {cookie}', '--header', f'x-csrf-token: {csrf}'])

def fetch(url):
    r = subprocess.run(
        ['curl', '--header', 'Accept: application/json', '--silent', '--url', url] + auth,
        capture_output=True, text=True)
    try:
        return json.loads(r.stdout)
    except json.JSONDecodeError:
        print(f'  WARN fetch failed: {url[-60:]}  raw: {r.stdout[:80]}')
        return {}

# items are sorted closedDate:desc, so items[:20] covers the most-recent runs.
# A clean regression shows exactly one PASSED→FAILED transition at the boundary;
# a flaky test oscillates multiple times within those 20 entries.
def is_clean_regression(items):
    statuses = [i.get('dueStatus', {}).get('key') for i in items[:20]]
    transitions = sum(
        1 for a, b in zip(statuses, statuses[1:])
        if (a == 'PASSED') != (b == 'PASSED'))
    # Allow one transition each way (PASS→FAIL, and possibly FAIL→PASS in the
    # most recent entry if the run was re-triggered). More than 2 = flaky.
    return transitions <= 2

histories  = []
done_keys  = set()
if os.path.exists(f'/tmp/testray-{task_id}-histories.json'):
    existing  = json.load(open(f'/tmp/testray-{task_id}-histories.json'))
    histories = existing
    done_keys = {e['key'] for e in existing}
    if done_keys:
        print(f'Resuming — {len(done_keys)} group(s) already processed, skipping.')

for g in groups:
    if g['key'] in done_keys:
        continue
    case_id = next((c['case_id'] for c in g['cases'] if c.get('case_id')), None)
    if not case_id:
        histories.append({'key': g['key'], 'error': 'no case_id'})
        continue

    hist_filter = urllib.parse.quote(
        f"r_caseToCaseResult_c_caseId eq '{case_id}'"
        f" and r_teamToCaseResult_c_teamId eq '{team_id}'")
    hist = fetch(
        f'https://testray.liferay.com/o/c/caseresults'
        f'?fields=closedDate,dueStatus,r_buildToCaseResult_c_buildId'
        f'&filter={hist_filter}'
        f'&pageSize=200&sort=closedDate:desc')

    items      = hist.get('items', [])
    total      = hist.get('totalCount', 0)
    clean      = is_clean_regression(items)
    last_pass  = next((i for i in items if i.get('dueStatus', {}).get('key') == 'PASSED'), None)
    first_fail = next((i for i in items if i.get('dueStatus', {}).get('key') in ('FAILED', 'BLOCKED')), None)

    entry = {'key': g['key'], 'case_id': case_id, 'total_history': total,
             'clean_regression': clean}

    for label, item in [('last_pass', last_pass), ('first_fail', first_fail)]:
        if item:
            build_id = item.get('r_buildToCaseResult_c_buildId')
            build    = fetch(f'https://testray.liferay.com/o/c/builds/{build_id}')
            entry[label] = {
                'date':     item.get('closedDate', '')[:10],
                'build_id': build_id,
                'sha':      build.get('gitHash', ''),
                'name':     build.get('name', ''),
            }
        else:
            entry[label] = None

    histories.append(entry)
    verdict = 'CLEAN REGRESSION' if clean else 'FLAKY / PRE-EXISTING'
    print(f'\nGroup: {g["key"][:80]}')
    print(f'  History: {total} total entries — {verdict}')
    if entry.get('last_pass'):
        lp = entry['last_pass']
        print(f'  Last pass : {lp["date"]}  build={lp["build_id"]}  sha={lp["sha"][:12]}')
    else:
        print('  Last pass : not found in last 200 entries')
    if entry.get('first_fail'):
        ff = entry['first_fail']
        print(f'  First fail: {ff["date"]}  build={ff["build_id"]}  sha={ff["sha"][:12]}')

json.dump(histories, open(f'/tmp/testray-{task_id}-histories.json', 'w'), indent=2)
print(f'\nHistories saved ({len(histories)} total).')
PYEOF
```

For each group, run `git log` scoped by the history verdict:

- **Clean regression** — `git log last_pass_sha..first_fail_sha` scoped to paths relevant to the error.
- **Flaky / pre-existing** — `git log --oneline -10` on the specific test and implementation files.
- **No last pass found** — treat as pre-existing.

Extract the SHAs from the histories JSON for the group being analyzed, then run `git log`. For example:

```bash
LAST_SHA=$(python3 -c "
import json, os
h = json.load(open(f'/tmp/testray-{os.environ[\"TASK_ID\"]}-histories.json'))
lp = h[GROUP_INDEX].get('last_pass')
print(lp['sha'] if lp else '')
")
FAIL_SHA=$(python3 -c "
import json, os
h = json.load(open(f'/tmp/testray-{os.environ[\"TASK_ID\"]}-histories.json'))
ff = h[GROUP_INDEX].get('first_fail')
print(ff['sha'] if ff else '')
")

git log \
	--no-merges \
	--oneline \
	"${LAST_SHA}..${FAIL_SHA}" \
	-- \
	portal-impl/src/com/liferay/portal/upgrade/ \
	portal-impl/src/META-INF/portal-hbm.xml \
	modules/apps/layout/
```

| Error Type | Relevant Paths |
| --- | --- |
| `column X does not exist` | `portal-impl/src/com/liferay/portal/upgrade/`, `portal-impl/src/META-INF/portal-hbm.xml`, module owning the table |
| `ElementNotFoundPoshi` | The feature module under `modules/apps/` |
| Unit test assertion failure | The specific test file and its implementation |
| `NullPointerException` in class X | Source file for class X |

### Step 7 — Early Jira Ticket Check

Before spending time on deep analysis, search Jira for each error group. An open ticket means someone is already working on it — skip the analysis for that group. A resolved ticket means the fix may or may not have reached the failing build — check before deciding.

```bash
source ~/.bashrc.d/jira.sh

python3 << 'PYEOF'
import json, os, re, subprocess

task_id        = os.environ['TASK_ID']
groups         = json.load(open(f'/tmp/testray-{task_id}-groups.json'))
meta           = json.load(open(f'/tmp/testray-{task_id}-meta.json'))
user           = os.environ['JIRA_API_USER']
token          = os.environ['JIRA_API_TOKEN']

meta_by_cr_id  = {str(m['cr_id']): m for m in meta}

# Tickets already linked to each subtask in the Testray UI — the primary source.
# Populated by Step 2 when the auth level permits reading subtask records.
try:
    subtask_issues = json.load(open(f'/tmp/testray-{task_id}-subtask-issues.json'))
except (FileNotFoundError, json.JSONDecodeError):
    subtask_issues = {}

def jira_get(ticket_key):
    r = subprocess.run([
        'curl', '--header', 'Accept: application/json', '--silent',
        '--url', f'https://liferay.atlassian.net/rest/api/3/issue/{ticket_key}?fields=summary,status,issuetype',
        '--user', f'{user}:{token}',
    ], capture_output=True, text=True)
    try:
        d = json.loads(r.stdout)
        return d if 'key' in d else None
    except Exception:
        return None

_jql_permitted = True   # set to False once we know the token lacks search permission

def jql_search(jql, max_results=5):
    global _jql_permitted
    if not _jql_permitted:
        return []
    r = subprocess.run([
        'curl', '--get',
        '--data-urlencode', f'jql={jql}',
        '--data-urlencode', f'maxResults={max_results}',
        '--data-urlencode', 'fields=summary,status,issuetype',
        '--header', 'Accept: application/json',
        '--silent',
        '--url', 'https://liferay.atlassian.net/rest/api/3/issue/search',
        '--user', f'{user}:{token}',
    ], capture_output=True, text=True)
    try:
        d    = json.loads(r.stdout)
        errs = d.get('errorMessages', [])
        if errs:
            # Typical cause: the API token lacks project-level search permission.
            # Direct GET /issue/{key} still works; only JQL is blocked.
            print(f'  [jql_search] permission error — disabling JQL for this run: {errs[0][:80]}')
            _jql_permitted = False
            return []
        return d.get('issues', [])
    except Exception:
        return []

def fmt(iss):
    f = iss['fields']
    return {
        'key':     iss['key'],
        'status':  f['status']['name'],
        'type':    f['issuetype']['name'],
        'summary': f['summary'][:80],
        'url':     f'https://liferay.atlassian.net/browse/{iss["key"]}',
    }

def enrich_from_log(test_name, cases):
    """Scan the JUnit stack trace in the log to get the full ClassName.methodName
    for display in Step 9 output. JQL search is typically blocked by API token
    permissions, so this result is not used for Jira search — it is printed
    alongside the stored test name so the analyst can read it.
    """
    fragment = re.escape(test_name.lstrip('_'))
    # Match: at com.liferay...ClassName.methodName(ClassName.java:NNN)
    pattern  = re.compile(
        rf'at [\w$.]+\.([\w$]+)\.(_?{fragment}[\w$]*)\([\w$]+\.java:\d+\)')
    for c in cases:
        m        = meta_by_cr_id.get(str(c.get('cr_id')), {})
        log_path = m.get('log_path')
        if not log_path or not os.path.exists(log_path):
            continue
        try:
            log = open(log_path, errors='replace').read()
        except OSError:
            continue
        match = pattern.search(log)
        if match:
            return f'{match.group(1)}.{match.group(2)}'
    return test_name

def add_unique(found, issues):
    for iss in issues:
        entry = fmt(iss)
        if not any(x['key'] == entry['key'] for x in found):
            found.append(entry)

results = []
for i, g in enumerate(groups):
    test_name  = g['cases'][0].get('test', '') if g.get('cases') else ''
    subtask_id = str(g['cases'][0].get('subtask_id', '')) if g.get('cases') else ''
    method     = test_name.split('#')[-1] if '#' in test_name else test_name.split('(')[0]
    found      = []

    # Try to get the full ClassName.methodName from the log stack trace so the
    # search term matches Jira tickets whose summary uses the real method name
    rich_name = enrich_from_log(method, g.get('cases', []))

    print(f'\nGroup {i + 1}: {g["key"][:80]}')
    print(f'  Test: {test_name}  →  search term: {rich_name}')

    # 0. Testray subtask `issues` field — checked first because it is always
    #    authoritative: a human tester already linked the ticket via the UI.
    #    Jira text search is only a fallback for groups with no subtask link.
    if subtask_id in subtask_issues:
        for key in subtask_issues[subtask_id]:
            iss = jira_get(key)
            if iss:
                add_unique(found, [iss])
                print(f'  From Testray subtask issues: {key}')

    # 1. Full enriched name (most specific — catches ClassName.methodName tickets)
    if not found and rich_name != method:
        add_unique(found, jql_search(
            f'project = LPD AND text ~ "{rich_name}" ORDER BY updated DESC'))

    # 2. Full stored test name
    if not found and test_name:
        add_unique(found, jql_search(
            f'project = LPD AND text ~ "{test_name}" ORDER BY updated DESC'))

    # 3. Method fragment alone
    if not found and method and method != test_name:
        add_unique(found, jql_search(
            f'project = LPD AND text ~ "{method}" ORDER BY updated DESC'))

    # 4. Last resort: first meaningful words of the normalized error key
    if not found:
        kw = ' '.join(re.sub(r'[^a-zA-Z0-9 ]', ' ', g['key']).split()[:4])
        if kw:
            add_unique(found, jql_search(
                f'project = LPD AND text ~ "{kw}" ORDER BY updated DESC'))

    if found:
        for f in found:
            open_marker = '(OPEN)' if f['status'] not in ('Resolved', 'Closed', 'Done') else '(resolved)'
            print(f'  {open_marker} {f["key"]} [{f["status"]}] — {f["summary"]}')
            print(f'           {f["url"]}')
    else:
        if subtask_id not in subtask_issues and not _jql_permitted:
            print('  Could not determine — subtask issues unreadable (auth) and JQL search blocked')
        elif subtask_id not in subtask_issues:
            print('  Could not determine — subtask issues unreadable (auth); JQL returned nothing')
        else:
            print('  No existing ticket found')

    results.append({'group_index': i, 'key': g['key'], 'test': test_name,
                    'rich_name': rich_name, 'existing': found})

json.dump(results, open(f'/tmp/testray-{task_id}-early-jira.json', 'w'), indent=2)
PYEOF
```

Pause here. For each group with an open ticket, skip deep analysis — note the ticket URL in the Step 9 report instead of "(pending)". For groups with only resolved tickets, confirm whether the fix reached the failing build before deciding to skip. Proceed with analysis only for groups with no covering ticket.

### Step 8 — Analyze Each Error Group

For each group produce:

1. **Description** — 1–2 sentences: what failed, which tests, what the error says.
1. **Regression window** — last pass build/date, first fail build/date, and the specific commit(s) from the git log most likely responsible. If unclear, write "Root cause could not be determined."
1. **Most likely cause** — grounded in the commit found. If no clear candidate: "Root cause could not be determined."
1. **Responsible team** — inferred from the commit author, ticket, and affected module. If unclear: "Unknown — needs triage."

| Signal | Responsible Team |
| --- | --- |
| `stylebook*` column added to Layout without portal-level upgrade | Frontend Infrastructure (Stylebook) |
| `resourceac0_` / `ResourceAction` / `ResourcePermission` | Permissions / Portal Core |
| `mvccVersion` missing on a system table | Portal Infrastructure |
| Module-level upgrade step exists but no portal-level step | Team that authored the module upgrade |
| `PSQLException: connection refused` or network timeout | Infrastructure / CI — not a code defect |
| Poshi `ElementNotFoundPoshi` in a feature test | Team that owns the feature under test |
| Unit test assertion string mismatch | Team that last modified the implementation string |

After completing the per-group analysis above, write the results to disk so Step 10 can read the causative commit for each group. Expand the list to contain one entry per group (using the 0-based index) filled with the values derived above, then run the script:

```python
python3 << 'PYEOF'
import json, os
task_id  = os.environ['TASK_ID']
analysis = [
    {
        'group_index': 0,           # 0-based index matching groups list
        'causative_commit': '',     # e.g. "abc1234 LPD-XXXXX Fix something"
        'description': '',
        'most_likely_cause': '',
        'responsible_team': '',
    },
    # one entry per group
]
json.dump(analysis, open(f'/tmp/testray-{task_id}-analysis.json', 'w'), indent=2)
PYEOF
```

### Step 9 — Report

For each group output:

```
## Error N: <error signature>

**Affected tests** (K total — subtasks ST-X, ST-Y — FAILED/BLOCKED):
- `<test name>` — ST-<N>

**Error message**:
<first 10 lines of the representative error>

**Regression window**:
- Last pass : <date>  build <N>  sha <short>
- First fail: <date>  build <N>  sha <short>
- Causative commit: `<sha> LPD-XXXXX <title>` by <author>

**Description**: <1–2 sentences>

**Most likely cause**: <1–2 sentences>

**Responsible team**: <team>

**Jira ticket**: (pending)
```

After all groups, print a summary table:

```
| # | Error | Count | Subtasks | Causative Commit | Responsible Team | Ticket |
|---|-------|-------|----------|-----------------|-----------------|--------|
| 1 | column stylebookentryscopeerc ... | 19 | ST-2, ST-10 | LPD-88081 (G. Lima) | Frontend Infra | (pending) |
```

Pause here. Ask the user to confirm which errors should proceed to ticket creation.

### Step 10 — Check for Existing Jira Tickets

Before creating any ticket, search Jira for each confirmed error group.

The **primary** search uses the causative commit's LPD number. Every commit subject that contains `LPD-XXXXX` names the ticket that introduced the change; that ticket's subtasks and linked issues are where a tracking bug is most likely to already exist. Text-based search is only a fallback for groups whose causative commit has no LPD number.

For each group:

1. Extract the LPD number from the causative commit subject (e.g. `LPD-84041` from `LPD-84041 Fix source formatter rule`).
2. If an LPD number is found, fetch that ticket and inspect:
   - Its subtasks (`subtasks` field).
   - Its linked issues (`fields.issuelinks`).
   - Search by JQL: `project = LPD AND text ~ "LPD-XXXXX" ORDER BY created DESC` to catch tickets that mention the LPD number in their description.
3. If no LPD number is found, fall back to text search on the error key.

```bash
source ~/.bashrc.d/jira.sh

python3 << 'PYEOF'
import json, os, re, subprocess

task_id = os.environ['TASK_ID']
groups  = json.load(open(f'/tmp/testray-{task_id}-groups.json'))
user    = os.environ['JIRA_API_USER']
token   = os.environ['JIRA_API_TOKEN']

# Load causative commit info written by Step 8
analysis = {}
try:
    for a in json.load(open(f'/tmp/testray-{task_id}-analysis.json')):
        analysis[a['group_index']] = a
except (FileNotFoundError, json.JSONDecodeError):
    pass

def curl_jira(url):
    r = subprocess.run([
        'curl',
        '--header', 'Accept: application/json',
        '--silent',
        '--url', url,
        '--user', f'{user}:{token}',
    ], capture_output=True, text=True)
    try:
        return json.loads(r.stdout)
    except json.JSONDecodeError:
        print(f'  WARN curl_jira: non-JSON response for {url[-60:]}: {r.stdout[:80]}')
        return {}

def jql_search(jql):
    r = subprocess.run([
        'curl', '--get',
        '--data-urlencode', f'jql={jql}',
        '--data-urlencode', 'maxResults=5',
        '--data-urlencode', 'fields=summary,status,issuetype',
        '--header', 'Accept: application/json',
        '--silent',
        '--url', 'https://liferay.atlassian.net/rest/api/3/issue/search',
        '--user', f'{user}:{token}',
    ], capture_output=True, text=True)
    try:
        d    = json.loads(r.stdout)
        errs = d.get('errorMessages', [])
        if errs:
            print(f'  [jql_search] error: {errs[0][:80]}')
            return []
        return d.get('issues', [])
    except json.JSONDecodeError:
        print(f'  WARN jql_search: non-JSON response: {r.stdout[:80]}')
        return []

def fmt(iss):
    f = iss['fields']
    return {
        'key': iss['key'],
        'status': f['status']['name'],
        'type': f['issuetype']['name'],
        'summary': f['summary'][:80],
        'url': f'https://liferay.atlassian.net/browse/{iss["key"]}',
    }

results = []
for i, g in enumerate(groups, 1):
    commit_subject = analysis.get(i - 1, {}).get('causative_commit', '')
    lpd_match      = re.search(r'LPD-\d+', commit_subject)
    found          = []

    print(f'\nGroup {i}: {g["key"][:80]}')

    if lpd_match:
        lpd_key = lpd_match.group(0)
        print(f'  Causative commit references {lpd_key} — checking subtasks and links')

        ticket = curl_jira(
            f'https://liferay.atlassian.net/rest/api/3/issue/{lpd_key}'
            f'?fields=summary,status,issuetype,subtasks,issuelinks'
        )

        for sub in ticket.get('fields', {}).get('subtasks', []):
            sf = sub['fields']
            found.append({
                'key': sub['key'],
                'status': sf['status']['name'],
                'type': sf['issuetype']['name'],
                'summary': sf['summary'][:80],
                'url': f'https://liferay.atlassian.net/browse/{sub["key"]}',
                'via': f'subtask of {lpd_key}',
            })

        for link in ticket.get('fields', {}).get('issuelinks', []):
            linked = link.get('outwardIssue') or link.get('inwardIssue')
            if linked:
                lf = linked['fields']
                found.append({
                    'key': linked['key'],
                    'status': lf['status']['name'],
                    'type': lf['issuetype']['name'],
                    'summary': lf['summary'][:80],
                    'url': f'https://liferay.atlassian.net/browse/{linked["key"]}',
                    'via': f'linked from {lpd_key} ({link.get("type",{}).get("name","")})',
                })

        for iss in jql_search(f'project = LPD AND text ~ "{lpd_key}" ORDER BY created DESC'):
            if iss['key'] != lpd_key and not any(f['key'] == iss['key'] for f in found):
                found.append({**fmt(iss), 'via': f'mentions {lpd_key}'})

    if not found:
        kw = g['key'].split(':')[-1].strip()[:80]
        for iss in jql_search(f'project = LPD AND text ~ "{kw}" ORDER BY created DESC'):
            found.append({**fmt(iss), 'via': 'text search'})

    entry = {'group_index': i, 'key': g['key'], 'existing': found}
    if found:
        for f in found:
            print(f'  EXISTING [{f["status"]}] {f["key"]} ({f["type"]}) via {f["via"]}: {f["summary"]}')
            print(f'           {f["url"]}')
    else:
        print('  NEW — no existing ticket found')
    results.append(entry)

json.dump(results, open(f'/tmp/testray-{task_id}-jira-check.json', 'w'), indent=2)
PYEOF
```

Pause here. Present the duplicate-check results and confirm with the user before creating any ticket.

### Step 11 — Create One Jira Ticket per Confirmed New Error

```bash
source ~/.bashrc.d/jira.sh

GROUP_INDEX=0
SUMMARY="<one-line error title, under 80 characters>"

DESCRIPTION_TEXT="Affected tests:
- TestClass#method (ST-N)

Testray testflow: https://testray.liferay.com/#/testflow/${TASK_ID}

Regression window:
- Last pass: <date> build <N>
- First fail: <date> build <N>
- Causative commit: <sha> LPD-XXXXX <title>

Error:
<first 15 lines of error text>

Most likely cause: <cause hypothesis>"

printf '%s' "${DESCRIPTION_TEXT}" > /tmp/testray-ticket-desc.txt

ERROR_FILE=$(python3 - << PYEOF
import json, os
task_id = os.environ['TASK_ID']
groups = json.load(open(f'/tmp/testray-{task_id}-groups.json'))
g      = groups[${GROUP_INDEX}]
meta   = json.load(open(f'/tmp/testray-{task_id}-meta.json'))
meta_by_id = {str(m['cr_id']): m for m in meta}
rep_cr = next((c for c in g['cases'] if c.get('cr_id')), None)
if rep_cr:
    m = meta_by_id.get(str(rep_cr['cr_id']), {})
    print(m.get('log_path', ''))
PYEOF
)

BODY=$(python3 - << PYEOF
import json, os
task_id       = os.environ['TASK_ID']
summary       = "${SUMMARY}"
desc          = open('/tmp/testray-ticket-desc.txt').read()
cfg           = json.load(open(os.path.expanduser('~/.config/testray-analyze/config.json')))
component_id  = cfg.get('jira_component_id', '')
team_field_id = cfg.get('jira_team_field_id', '')
issuetype_id  = cfg.get('jira_issuetype_id', '10004')
version_id    = cfg.get('jira_version_id', '')

fields = {
    'description': {
        'content': [{'content': [{'text': desc, 'type': 'text'}],
                     'type': 'paragraph'}],
        'type': 'doc',
        'version': 1
    },
    'issuetype': {'id': issuetype_id},
    'project': {'key': 'LPD'},
    'summary': summary,
}

if component_id:
    fields['components'] = [{'id': component_id}]
if team_field_id:
    fields['customfield_10979'] = [{'id': team_field_id}]
if version_id:
    fields['versions'] = [{'id': version_id}]

print(json.dumps({'fields': fields}))
PYEOF
)

ISSUE_KEY=$(curl \
	--data "${BODY}" \
	--header 'Accept: application/json' \
	--header 'Content-Type: application/json' \
	--request POST \
	--silent \
	--url 'https://liferay.atlassian.net/rest/api/3/issue' \
	--user "${JIRA_API_USER}:${JIRA_API_TOKEN}" \
	| python3 -c "import json,sys; print(json.load(sys.stdin).get('key',''))")

echo "Created: ${ISSUE_KEY}"
echo "URL: https://liferay.atlassian.net/browse/${ISSUE_KEY}"

python3 - << PYEOF
import json, os
task_id = os.environ['TASK_ID']
path = f'/tmp/testray-{task_id}-ticket-map.json'
m = json.load(open(path)) if os.path.exists(path) else {}
m[str(${GROUP_INDEX})] = '${ISSUE_KEY}'
json.dump(m, open(path, 'w'))
PYEOF

if [ -n "${ERROR_FILE}" ] && [ -f "${ERROR_FILE}" ]; then
	curl \
		--form "file=@${ERROR_FILE}" \
		--header 'Accept: application/json' \
		--header 'X-Atlassian-Token: no-check' \
		--request POST \
		--silent \
		--url "https://liferay.atlassian.net/rest/api/3/issue/${ISSUE_KEY}/attachments" \
		--user "${JIRA_API_USER}:${JIRA_API_TOKEN}" \
		| python3 -c "
import json,sys
items=json.load(sys.stdin)
if isinstance(items, list) and items:
    print(f'Attached: {items[0][\"filename\"]}')
else:
    print(f'Attach failed: {items}')
"
fi
```

### Step 12 — Link Jira Tickets Back to Testray Subtasks

```bash
export ACCESS_TOKEN TESTRAY_COOKIE CSRF

python3 << 'PYEOF'
import json, os, subprocess

task_id    = os.environ['TASK_ID']
groups     = json.load(open(f'/tmp/testray-{task_id}-groups.json'))
ticket_map = json.load(open(f'/tmp/testray-{task_id}-ticket-map.json'))

subtask_tickets = {}
for idx, g in enumerate(groups):
    ticket = ticket_map.get(str(idx))
    if not ticket:
        continue
    for c in g['cases']:
        st = c.get('subtask_id', '')
        if st:
            subtask_tickets.setdefault(st, set()).add(ticket)

token  = os.environ.get('ACCESS_TOKEN', '')
cookie = os.environ.get('TESTRAY_COOKIE', '')
csrf   = os.environ.get('CSRF', '')
auth   = (['--header', f'Authorization: Bearer {token}'] if token
           else ['--compressed', '--header', f'Cookie: {cookie}', '--header', f'x-csrf-token: {csrf}'])
ok     = 0
fail   = 0

for st, new_tickets in sorted(subtask_tickets.items()):
    # Read existing issues first so we do not overwrite tickets linked by other runs.
    url = f'https://testray.liferay.com/o/c/subtasks/{st}?fields=id,issues'
    r = subprocess.run(
        ['curl', '--header', 'Accept: application/json', '--silent', '--url', url] + auth,
        capture_output=True, text=True)
    try:
        existing_issues = json.loads(r.stdout).get('issues', '') or ''
    except json.JSONDecodeError:
        existing_issues = ''

    # Normalize en dashes to hyphens (Testray stores "LPD–XXXXX" with U+2013).
    existing_issues = existing_issues.replace('–', '-').replace('—', '-')
    existing_keys   = {k.strip() for k in existing_issues.split(',') if k.strip()}

    merged      = sorted(existing_keys | new_tickets)
    issues_str  = ', '.join(merged)

    body = json.dumps({'issues': issues_str})
    r = subprocess.run(
        ['curl', '--data', body,
         '--header', 'Accept: application/json',
         '--header', 'Content-Type: application/json',
         '--request', 'PATCH',
         '--silent',
         '--url', f'https://testray.liferay.com/o/c/subtasks/{st}'] + auth,
        capture_output=True, text=True)
    try:
        d = json.loads(r.stdout)
        if d.get('id'):
            print(f'  OK   {st} → {issues_str}')
            ok += 1
        else:
            print(f'  FAIL {st}: {r.stdout[:150]}')
            fail += 1
    except Exception as e:
        print(f'  FAIL {st}: {e}')
        fail += 1

print(f'\n{ok} updated, {fail} failed')
PYEOF
```

Replace every `(pending)` in the Step 8 summary table with the actual ticket keys.
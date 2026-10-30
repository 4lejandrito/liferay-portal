---

allowed-tools: [Bash, Read, Write]
description: Squash a branch's many small commits into one logical commit per area (backend, frontend, tests, etc.) while preserving the exact same diff against master. Use when the user says the branch has too many commits to review, asks to squash commits by file, or wants to reorganize commit history before raising a PR.
name: squash-by-area

---

# Squash Commits by Area

Reorganize a branch's commits into one logical commit per area so the PR is easy to review. The final diff against master must be byte-for-byte identical to the original.

## Preconditions

- Working tree is clean. Run `git status --porcelain` and abort if it is not empty.
- Current branch is not `master`.

## Step 1 — Create a Backup Branch

```bash
git branch "${CURRENT_BRANCH}_backup"
```

The backup is the safety net for the final verification and for recovery if anything goes wrong.

## Step 2 — Find the Branch Base

Find the oldest commit that belongs to this branch's work and record its parent as the base:

```bash
git log origin/master..HEAD --oneline | tail -1
```

If that command returns too many commits (more than the actual branch commits, indicating the local `origin/master` is stale), fall back to finding the branch's ticket prefix from the branch name (e.g., `LPD-68626`) and filtering:

```bash
git log --oneline | grep "LPD-XXXXX\|LPD-YYYYY" | tail -1
```

Record the parent of that oldest commit as `BASE_SHA`:

```bash
BASE_SHA=$(git rev-parse <oldest-branch-commit>^)
```

Verify the base is correct: `git log "${BASE_SHA}..HEAD" --oneline` must list only the commits that belong to this branch's work (nothing unrelated).

## Step 3 — Catalogue All Changed Files

```bash
git diff "${BASE_SHA}..HEAD" --name-only | sort
```

For each commit in the range, record which files it touches:

```bash
git log "${BASE_SHA}..HEAD" --oneline | awk '{print $1}' | while read sha; do
    echo "=== $sha $(git log --oneline -1 $sha | sed 's/^[a-f0-9]* //')"
    git show --name-only --format="" "$sha"
    echo
done
```

## Step 4 — Define Logical Groups

Group the files into logical areas. Do not reorder commits or try to cherry-pick patches — that causes merge conflicts when commits have inter-dependencies. Instead, assign every changed file to exactly one group and create each group's commit from the **final tree state** of the original branch.

Typical groups for a Liferay feature branch:

| Group | Files |
| --- | --- |
| Backend service/search | Document contributors, entity models, service impl, build.gradle |
| Frontend FDS | FDS filter classes, view classes, TypeScript, CSS |
| Elasticsearch mappings | `index-mappings.json` |
| Unit and integration tests | `*Test.java`, `*TestIntegration.java` |
| Playwright tests | `*.spec.ts`, page-object `*.ts` |
| Separate ticket (e.g., LPD-XXXXX) | Files belonging to a different ticket |

**Production code and test files must never share a commit.** Each production code group gets its own commit; the corresponding tests go in a separate commit that follows it. This is required because hotfixes and backports typically cherry-pick only the production commit, leaving the tests behind.

Each file must appear in exactly one group. When a file is touched by commits from multiple logical areas (e.g., a rename commit that touches both production and test files), assign the production file to the production group and the test file to the test group — never merge them into one commit.

## Step 5 — Create a Temp Branch and Commit Each Group

```bash
git checkout -b temp_squash "${BASE_SHA}"
```

For each group (in dependency order — production code before tests, backend before frontend when one depends on the other):

```bash
git checkout "${ORIGINAL_BRANCH}" -- <file1> <file2> ...
git commit -m "<TICKET> <Meaningful summary of what this group does>"
```

The `git checkout <branch> -- <files>` command stages the **final state** of each file from the original branch tip. This avoids all cherry-pick ordering issues.

Commit messages must follow the project convention (see `.claude/rules/commit.md`): ticket prefix, sentence case, under 72 characters, no trailing period.

## Step 6 — Review Commit Messages Against the Actual Diff

Before resetting the original branch, read the full diff of each new commit and verify its message covers the complete scope of changes — not just the most obvious files:

```bash
git log temp_squash --oneline | awk '{print $1}' | while read sha; do
    echo "=== $(git log --oneline -1 $sha)"
    git show --stat --format="" "$sha"
    echo
done
```

For any commit where the message does not match the full scope of changes, amend it before proceeding:

```bash
EDITOR_SCRIPT=$(mktemp)
cat > "${EDITOR_SCRIPT}" << 'EOF'
#!/bin/bash
echo "<TICKET> <Corrected message>" > "$1"
EOF
chmod +x "${EDITOR_SCRIPT}"
GIT_SEQUENCE_EDITOR="sed -i 's/^pick <SHA>/reword <SHA>/'" GIT_EDITOR="${EDITOR_SCRIPT}" git rebase -i "${BASE_SHA}"
rm "${EDITOR_SCRIPT}"
```

Common failure mode: a commit message names one subsystem (e.g., "search index") when the commit also touches another (e.g., OData entity model, REST resource). The message must reflect every layer changed.

## Step 7 — Reset the Original Branch

```bash
git checkout "${ORIGINAL_BRANCH}"
git reset --hard temp_squash
git branch -d temp_squash
```

## Step 8 — Verify

Run both checks. Both must pass before proceeding.

```bash
# Check 1: the two branch tips are identical
git diff "${ORIGINAL_BRANCH}_backup" HEAD

# Check 2: the diff against master is identical
git diff master..HEAD > /tmp/current_vs_master.txt
git diff "master..${ORIGINAL_BRANCH}_backup" > /tmp/backup_vs_master.txt
diff /tmp/current_vs_master.txt /tmp/backup_vs_master.txt && echo "IDENTICAL" || echo "DIFFERENCES FOUND"
```

If either check shows differences, do not proceed. Restore the original branch:

```bash
git reset --hard "${ORIGINAL_BRANCH}_backup"
```

Then diagnose which file was missed or misassigned and repeat from Step 5. When a message needs correction, repeat from Step 6.

## Step 9 — Remove the Backup Branch

Only after both checks pass:

```bash
git branch -d "${ORIGINAL_BRANCH}_backup"
```

## Key Rules

**Never reorder commits with interactive rebase when squashing.** Reordering commits that share files causes merge conflicts because each commit's diff was created against a specific file state. The `git checkout <branch> -- <files>` approach avoids this entirely by applying the final state of each file directly, bypassing all ordering dependencies.

**Every changed file must appear in exactly one group.** Run `git diff "${BASE_SHA}..HEAD" --name-only | sort` before and after building the groups to confirm nothing is missing or duplicated.

**Production code and test files must never share a commit.** Every production code group has a matching test group that follows it as a separate commit. This rule has no exceptions — hotfixes and backports cherry-pick production commits without their tests, and a mixed commit makes that impossible.

**Skip source formatter.** The original commits were already formatted. Rerunning the formatter on the squashed commits is unnecessary and risks introducing noise into the diff.
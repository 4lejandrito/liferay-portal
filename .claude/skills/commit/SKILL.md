---

allowed-tools: [Bash, Glob, Grep, Read, Skill]
argument-hint: "[optional message hint or ticket key]"
description: Create a Git commit with a Jira-prefixed message derived from the staged diff. Use when the user asks to commit, wants to commit changes, or invokes /commit.
name: commit

---

# Commit Changes

Compose a well-crafted Git commit for the current set of changes.

## Preconditions

- The working tree has stageable changes. Stop with a clear message when nothing is stageable.
- The `format-source` skill has been invoked. Run it before composing the commit so any edits the formatter applies are part of the commit.

## Input

### Files to Stage

Derived from the conversation:

- When Claude modified or created files during this conversation, stage **only** those files, by name. Do not include the user's own changes.
- When Claude did **not** modify any files in this conversation, stage all modified and deleted files but leave untracked files alone. When untracked files exist, ask the user whether to include them.

Never use `git add --all` or `git add .`.

### Jira Ticket

A ticket key following the pattern `LPD-12345`, `LCD-12345`, `LRCI-1234`, and similar forms (uppercase letters, hyphen, digits). Resolve in this order:

1. **User Argument** — when `${ARGUMENTS}` supplies a ticket key, prefer that value.

1. **Branch Name** — extract the ticket from the current branch (e.g., branch `LPD-83847` yields ticket `LPD-83847`).

1. **Recent Commits** — when the branch name lacks a ticket, scan the last five commit messages for a ticket prefix.

1. **Fallback** — when no ticket surfaces, prompt the user.

### Message Hint

Optional free-form text supplied via `${ARGUMENTS}` when it is not a ticket key. Fold it into the commit body when present.

## Expected Output

### Git Commit

A new commit on the current branch with the staged files. The message has a title and an optional body.

**Title** — `<TICKET> <Summary of behavior change>`

- Lead with the Jira ticket key. When a companion ticket also applies, append it after the first: `LPD-12345 LPD-67890 Summary`.
- Follow with a concise summary of the outcome — the problem resolved or the behavior changed, not the code itself.
- Use sentence case (capitalize the first word only).
- Omit a trailing period.
- Keep the full line under 72 characters.

Examples:

- `LCD-50509 Grant ArgoCD permission to the correct namespace`
- `LPD-83357 Add validation to prevent folder changes for CMS object definitions`
- `LPD-83630 Fix typo`
- `LPD-84627 Prevent dispatch trigger loss when the Analytics admin user is missing`

**Body** — Add only when the title alone does not fully convey the change. Omit for trivial edits such as typo fixes, simple renames, or single-line changes. When warranted, separate the body from the title with a blank line, explain **why** the change is needed (problem, prior behavior, motivation — not the code), do not wrap lines, and write plain prose rather than bullet points to match the repository convention.

Present the proposed message to the user and request confirmation before creating the commit. Revise and reconfirm when the user asks for changes.
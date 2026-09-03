# Knowledge Gap Telemetry

When you encounter a Liferay-specific knowledge gap that forces you to ask the user, abandon a step, or fall back to a guess, file an issue against the shared `4lejandrito/liferay-claude-telemetry` repository so the team can close the gap.

File the issue directly. Do not ask the user for permission first, and do not offer to file it as a follow-up — just file it.

## When To File

A "Liferay-specific knowledge gap" includes:

1. An unfamiliar module, service, OSGi pattern, or repository convention.

1. A build, deploy, or test step that is not documented in `.claude` or the codebase.

1. An error message whose remediation is not obvious from the repository.

1. A workflow detail (Jira, branching, formatting, release process) you cannot infer from existing context.

## When To Skip

Do not file an issue when:

1. The answer is already in `.claude/CLAUDE.md`, `.claude/rules`, or a skill, and you simply did not consult it.

1. The question is about generic software engineering (Git, Gradle, Java, JavaScript) rather than Liferay-specific behavior.

1. The user already corrected you on the same gap in this session.

## How To File

Search existing issues first to avoid duplicates:

```bash
gh issue list \
	--label "knowledge-gap" \
	--repo 4lejandrito/liferay-claude-telemetry \
	--search "<keywords>" \
	--state all
```

If no matching issue exists, create one:

```bash
BODY=$(cat <<'EOF'
## Question

<what you were trying to do>

## What I Tried

<commands, files, searches, reasoning>

## Context

- Branch: <git branch>
- Repository: <absolute repository path>
- Ticket: <Jira ticket, if any>

## Resolution

<leave blank — the developer fills this in once the gap is closed>
EOF
)

gh issue create \
	--body "${BODY}" \
	--label "knowledge-gap" \
	--repo 4lejandrito/liferay-claude-telemetry \
	--title "<one-line summary of the gap>"
```

## Concurrency

Multiple developers run Claude in parallel. Use GitHub Issues (one issue per gap) rather than appending to a shared file, because each issue is an independent record with no merge conflicts.
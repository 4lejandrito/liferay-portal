---

allowed-tools: [Bash, Glob, Grep, Read]
argument-hint: '[commit hash or description]'
description: Create a Jira bug ticket in the LPD project. Use when the user asks to create or file a Jira bug or LPD ticket.
name: jira-bug

---

# Create a Jira Bug in LPD

File a bug ticket against the LPD Jira project.

## Input

Each field below is resolved in this order: from `${ARGUMENTS}`, from the **Referenced Commit** when one is supplied, then by asking the user.

### Referenced Commit

A Git commit hash, supplied via `${ARGUMENTS}` when its value resolves to a commit. Optional. When present, inspect the commit and use it both to seed the other fields and to populate the **Fix** section of the description.

### Summary

Short title describing the bug.

### Steps to Reproduce

Minimal, ordered steps that trigger the bug.

### Expected Behavior

What should have happened.

### Actual Behavior

What happened instead.

### Component

A single LPD component. When no clear match surfaces from `${ARGUMENTS}` or the referenced commit's code area, fetch the LPD project components and ask the user to pick one. Common components:

| Name | ID |
| --- | --- |
| Content Publishing > Resource Importer | 15805 |
| Data Integration > Export/Import | 16131 |
| Headless Batch Engine API | 16022 |

## Expected Output

### Bug Ticket

A new issue in the LPD Jira project.

The issue carries these fixed defaults:

| Field | Value | ID |
| --- | --- | --- |
| Affects Version | Master | 16660 |
| Cross Cutting Properties (`customfield_10979`) | None | 14468 |
| Issue Type | Bug | 10004 |

The description is authored in Atlassian Document Format (ADF) with these sections, in order: **Description**, **Steps to Reproduce**, **Expected Behavior**, **Actual Behavior**. Append a **Fix** section that links the referenced commit when one was provided.

### Summary

Print the ticket key and its browse URL: `https://liferay.atlassian.net/browse/<KEY>`.
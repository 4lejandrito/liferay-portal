---

allowed-tools: [Bash, Glob, Grep, Read]
argument-hint: '[commit hash or description]'
description: Create a Jira task in the LPD project. Use when the user asks to create a Jira task or LPD task ticket.
name: jira-task

---

# Create a Jira Task in LPD

File a task ticket against the LPD Jira project.

## Input

Each field below is resolved in this order: from `${ARGUMENTS}`, from the **Referenced Commit** when one is supplied, then by asking the user.

### Referenced Commit

A Git commit hash, supplied via `${ARGUMENTS}` when its value resolves to a commit. Optional. When present, inspect the commit and use it both to seed the other fields and to populate the **Reference** section of the description.

### Summary

Short title describing the task.

### Description

What needs to be done and why.

### Acceptance Criteria

List of conditions that mark the task done. Optional.

### Component

A single LPD component. When no clear match surfaces from `${ARGUMENTS}` or the referenced commit's code area, fetch the LPD project components and ask the user to pick one. Common components:

| Name | ID |
| --- | --- |
| Content Publishing > Resource Importer | 15805 |
| Data Integration > Export/Import | 16131 |
| Headless Batch Engine API | 16022 |

## Expected Output

### Task Ticket

A new issue in the LPD Jira project.

The issue carries this fixed default:

| Field | Value | ID |
| --- | --- | --- |
| Issue Type | Task | 10002 |

Tasks do not require **Affects Version** or **Cross Cutting Properties**.

The description is authored in Atlassian Document Format (ADF) with these sections, in order: **Description**, **Acceptance Criteria** (when applicable). Append a **Reference** section that links the referenced commit when one was provided.

### Summary

Print the ticket key and its browse URL: `https://liferay.atlassian.net/browse/<KEY>`.
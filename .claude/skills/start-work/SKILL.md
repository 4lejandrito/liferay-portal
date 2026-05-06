---

allowed-tools: [Bash, Read, Skill]
argument-hint: "<ticket-key-or-url>"
description: Start work on a Liferay Jira ticket. Use when the user asks to start work on a ticket or invokes /start-work.
name: start-work

---

# Start Work on a Jira Ticket

Prepare a Liferay ticket for development.

## Preconditions

- The working tree has no uncommitted changes. Abort otherwise.

## Input

### Ticket Key

A Jira key (e.g., `LPD-86295`) or a browse URL. Resolve in this order:

1. **User Argument** — when `${ARGUMENTS}` supplies a key or URL, use it.

1. **Working Directory** — when `${PWD}` matches `*/liferay-portal-<KEY>` and `<KEY>` matches `[A-Z]+-[0-9]+`, derive the key from the directory name.

1. **Fallback** — ask the user.

## Expected Output

### Target Ticket

The "target" is the ticket whose status reflects active work. Resolve it from the input ticket's issue type:

| Input Ticket Type | Target |
| --- | --- |
| Bug (`10004`) | The bug itself |
| Story (`10001`) or Task (`10002`) | The Technical Task (`10153`) subtask |

The target is assigned to the user and transitioned to in-progress (see **Workflow**).

### Git Branch

A branch named after the **target** key, branched off the current `HEAD`. When the branch already exists, check it out instead.

When `${PWD}` matches `*/liferay-portal-<name>`, the session is already in a Liferay worktree with branch `<name>` checked out — skip branch creation and invoke the `worktree-setup` skill (action `new`) to provision the bundle, ports, and database.

### Plan

A development plan, produced in plan mode after reading both parent and child tickets.

## Workflow

### 1. Start Work on the Parent

Assign the parent to the user and apply the transitions below. When the parent is already in an in-progress status by a different user, refuse to continue.

| Parent Type | Destination | Transition IDs |
| --- | --- | --- |
| Bug | In Progress | `61` |
| Story | In Development | `41`, then `61` |
| Task | In Progress | `21` |

For a Story, apply the two transitions in sequence: `41` moves it to **Ready for Development**, which triggers Jira to autocreate the **Technical Task** subtask, then `61` moves it to **In Development**.

### 2. Start Work on the Child

Skip for **Bug**. For **Story** or **Task**, refetch the parent's subtasks until the **Technical Task** appears, then assign it to the user and transition it:

| Child Type | Destination | Transition ID |
| --- | --- | --- |
| Technical Task | In Progress | `41` |

### 3. Create the Branch

See **Git Branch** above.

### 4. Plan

Enter plan mode and read both parent and child tickets to produce the plan.
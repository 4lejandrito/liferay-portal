---

allowed-tools: [Bash, Edit, Glob, Grep, Read, Skill]
argument-hint: "<commit-sha> [aspect hint]"
description: Add a new rule to the format-source skill, derived from a Git commit. Use when the user wants to encode a formatting convention from a commit, or invokes /format-source-add-rule.
name: format-source-add-rule

---

# Format Source: Add Rule

Capture a formatting convention from a Git commit and append it to the `format-source` skill so it can be applied mechanically by future reviewers. The rule may target any file type the source formatter handles — Java, `.properties`, XML, JSP, Markdown, Gradle, `.gitignore`, YAML — so do not bias toward Java.

## Input

### Commit SHA

A Git commit SHA, supplied via `${ARGUMENTS}`. Required. Inspect its full diff to derive the rule.

### Aspect Hint

Optional free-form text after the SHA in `${ARGUMENTS}`. Commits often touch multiple things; the hint narrows the scope to a single aspect of the diff.

## Expected Output

### New Rule

A new rule appended to the end of `.claude/skills/format-source/SKILL.md` using this exact Markdown template:

````markdown
### Rule <next-number>: <Title Case name, for example "Method Parameter Ordering">

**Why:** <one-sentence explanation of what consistency the rule buys, not what the rule does>

**Examples:**

```diff
- <one or more before lines demonstrating the rule, using abstract identifiers — not the commit's literal code>
+ <matching after lines>
```
````

`<next-number>` is one greater than the highest existing rule number in the file.

Use abstract identifiers in the diff rather than the verbatim names from the commit — for example `methodA`, `fieldB`, `Foo` for Java; `some.property`, `myTag`, `someKey` for properties, XML, YAML, or Markdown. Examples should illustrate the general pattern; reproducing the commit's exact text overfits the rule to one case and makes future readers match on names rather than on the underlying pattern.

For rules with nuance, use several diff blocks for each additional case. Add a single line of prose before a diff block when the diff alone is unclear.

### Self-Test

Validation that the appended rule reproduces the input commit. Create a new branch at the commit's parent, apply only the manual rules (skip the automatic formatter) scoped to the files the commit touched, and compare the result with the commit. When the result diverges, revise the rule and repeat until the rule reproduces the commit.

### Commit

A commit staging only `.claude/skills/format-source/SKILL.md`, authored by invoking the `commit` skill with this commit message:

```
<ticket> Add rule derived from <sha>
```

## Workflow

### 1. Inspect the Commit

Identify a single, learnable formatting pattern in the commit's diff — something a future reviewer could apply mechanically to other code. Stop and ask the user to clarify when:

- Multiple distinct rules are present. Ask the user to pick one, or run the skill once per rule.

- The commit mixes formatting changes with logic or refactoring changes that cannot be separated.

- The pattern is not generalizable beyond the specific file or context.

### 2. Check for Duplicates

Read the existing rules in `.claude/skills/format-source/SKILL.md` and compare against the pattern from step 1. When the pattern duplicates or substantially overlaps an existing rule, flag it to the user and ask whether to skip, refine the existing rule, or proceed anyway.

### 3. Write the Rule

Append the new rule using the template under **New Rule** above.

### 4. Self-Test

Run the validation under **Self-Test** above. Revise and repeat until the rule reproduces the commit.

### 5. Commit

Invoke the `commit` skill with the message format under **Commit** above.
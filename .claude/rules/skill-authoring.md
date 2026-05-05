---

paths:
  - ".claude/skills/*/SKILL.md"

---

# Skill Authoring

Follow this template when authoring a skill.

```markdown
---

allowed-tools: [<sorted alphabetically>]
argument-hint: '<positional argument shape>'
description: <One or two sentences: the problem solved + the trigger that invokes the skill. Do not describe the workflow.>
name: <skill-name>

---

# <Title in Title Case>

<One-sentence intro restating the goal in plain prose.>

## Preconditions

<Bullet list of conditions verified once, at the start. Fail fast if any is missing.>

- <condition 1>
- <condition 2>

## Input

<One `### <Field>` sub-section per input. Title each sub-section after what the input *is* (e.g., `### Case Result ID`, `### Jira Ticket`, `### Target Repository`), not after the variable that carries it — never use `### ${ARGUMENTS}`. Inputs may come from `${ARGUMENTS}`, a Git or filesystem fact the skill reads, or data fetched at the start (e.g., via another skill).>

### <Field 1>

<What this input is, where it comes from (`${ARGUMENTS}`, a sub-skill, a file…), the shape it must have, how to validate it, and the fallback when it is absent or invalid.>

### <Field 2>

<…>

## Expected Output

<One `### <Field>` sub-section per artifact or data piece the skill produces.>

### <Field 1>

<What this output is — an artifact (a generated file, a Git artifact, an external resource) or a piece of data (a name, a status, a measurement). Describe its shape, the conditions under which it applies, and how to produce it (which sub-skill to invoke, which template to use, which value to compute). Include any fallback for missing or not-applicable cases.>

### <Field 2>

<…>

## Workflow

<Optional. Include only when control flow is non-trivial (multi-stage process, conditional branches, retries) and cannot be inferred from the declarative sections above. Most skills should omit this section.>

<Optional preamble: cross-cutting flow control (abort handling, exit semantics, etc.).>

<Inside each step, describe the goal in prose. Inline any rule that governs the step at the point it applies.>

### 1. <Step Title>

<Step goal in prose. Sub-steps use `#### 1.1. <Substep Title>`.>

### 2. <Step Title>

<…>
```

## Describe What, Not How

State the outcome and the shape of the result. Leave the mechanics to the model.

A skill written this way stays correct as tools, flags, and idioms change underneath it; the intent is visible instead of buried in incidental detail; the model picks the best mechanism for the moment instead of replaying a frozen one; and other skills can compose against the contract without depending on its implementation.

`## Preconditions`, `## Input`, and `## Expected Output` are the natural home for this specification. `## Workflow` is the escape hatch for the rare case where the procedure itself is load-bearing.

## Avoid Restating What the Model Already Knows

- Shell commands for routine operations (`git log`, `find`, `grep`, `cp`, …). Describe the operation; the model picks the command.
- Computation snippets (timestamps, durations, arithmetic). Describe the result; the model picks the formula.
- Standard development flows (branching, committing, pushing). Reference the skill that owns the flow.
- Language idioms and general programming knowledge.

## Avoid Restating What the Context Already Holds

- Test commands from CLAUDE.md's `Test` section. Refer to the test type instead.
- Deploy commands from CLAUDE.md's `Deploy` section.
- Repository layout from CLAUDE.md's architecture section.
- Other skills' workflows in full. Invoke them by name.

When a project-wide rule is missing, add it to CLAUDE.md or `.claude/rules/` and reference it; do not duplicate it in the skill body.
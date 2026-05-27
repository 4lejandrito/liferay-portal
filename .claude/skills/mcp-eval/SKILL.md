---

allowed-tools: [Agent, AskUserQuestion, Bash, Read, TaskCreate, TaskList, TaskUpdate, Write]
description: Evaluate Liferay MCP discoverability and usability by attempting a list of user-supplied use cases against a live Liferay instance, with a bounded three-strike budget per case. Produces a per-case verdict (OK / PARTIAL / FAIL), the roadblocks hit (discovery cost, scope ambiguity, missing prerequisites, schema confusion, MCP wrapper bugs, missing endpoints, auth or permission), and a concrete fix for each defect tagged by the surface that owns the change (OpenAPI spec, resource impl, MCP wrapper, external), rendered as a self-contained HTML report. Use when the user asks to evaluate the Liferay MCP, test its discoverability, or report on how well an AI can accomplish typical Liferay operations through it.
name: mcp-eval

---

# Liferay MCP Evaluation

Drive the Liferay MCP through a list of user-supplied use cases and report how discoverable and usable it is for an AI agent. The output is an evidence-based critique — not a list of "what I did", but a list of what got in the way and why.

## Core Principle: MCP-Only

The entire point of this evaluation is to prove that the MCP, on its own, is enough to accomplish realistic Liferay tasks. The moment work leaks out to any non-MCP channel, the result stops measuring the MCP and starts measuring your ingenuity at routing around it.

So this constraint is **hard**, not aspirational. Every operation in a use case — discovery, read, write, verification — must go through a `mcp__liferay-mcp__*` tool. The following are all out of bounds, even when they would make the case faster or cleaner:

- `curl`, `wget`, `httpie`, or any direct HTTP request against `/o/...`, `/api/...`, `/c/portal/...`, or any other Liferay endpoint.
- The Liferay UI (Site Administration, Forms, Workflow Designer, Object Builder, etc.) in any browser, including Playwright.
- Java client JARs (`com.liferay.*.rest.client`), Liferay CLI tools, `cliferay`, Blade, or Gogo shell.
- Direct database access, file-system access under `<bundles>`, or log scraping as a means of accomplishing or verifying a case.
- Reading Liferay REST documentation, the OpenAPI YAML in the repo, JSPs, or service interfaces to "know" what to call. Discovery must come from `getToolSets` / `getToolSummaries` / `getTool` — that is the surface under test.
- Recalled facts from prior conversations or auto-memory about specific Liferay endpoints, scope keys, or tool sets. Start cold.

The only allowed exceptions are bookkeeping tools that do not touch Liferay: `TaskCreate` / `TaskUpdate` / `TaskList` for tracking, `Bash` for trivial local operations like `grep` on a saved tool-result file, and `Read` / `Write` to produce the final report.

When a case cannot be completed under this constraint, **that is the finding**. Record it as a roadblock and move on. Do not reach for a workaround — the workaround would erase the data point the evaluation exists to collect.

This constraint governs the **attempt** — every step taken to accomplish or verify the use case. It does not govern the post-mortem. Once a case has been scored **FAIL**, reading the bundle logs at `<bundles>/logs/liferay.<yyyy-MM-dd>.log` becomes available for a single purpose: diagnosing *why* the attempt failed so the report can name a root cause instead of guessing at one. This is the only exception, and it is narrow: it applies to log reading only (database and file-system access stay out of bounds throughout), it opens only after the verdict is already fixed at **FAIL**, and it may never be used to retroactively complete the case or change the verdict. The logs sharpen the report; they do not rescue the attempt.

## Input

The user supplies a list of use cases, clearly split from one another — typically by numbering. Each case is a natural-language description of a Liferay operation. Cases range from simple to complex:

- A simple case fits on one line: "Create a web content article".

- A complex case spans multiple lines, carrying preconditions or several steps.

Treat each item as one case in full, however many lines it occupies — do not assume one line means one case. When the split is ambiguous, ask the user how they intend the list to divide rather than guessing.

When the user asks for an evaluation without providing cases, ask them for the list before proceeding. Do not invent cases.

## Case Isolation

Each use case runs in its own fresh `general-purpose` sub-agent, spawned one at a time via the `Agent` tool. A fresh sub-agent gives the cold-start isolation the evaluation needs: no leaked tool sets, no remembered IDs, no shortcut from "the previous case found this in `c-mcpevalcustomers`".

The orchestrator — the agent running this skill — never invokes Liferay MCP tools itself. For each case it spawns one sub-agent, waits for the per-case JSON object to come back, collects that object into the running array, then starts the next case.

Run the cases sequentially, never in parallel: each sub-agent's MCP traffic is independently rate-limited, and the runtime does not currently support parallel sub-agent spawning. Spawn the next sub-agent only after the previous one has returned.

## Workflow

The evaluation runs through two layers: an **orchestrator** — the agent running this skill — and a **sub-agent** spawned per case. The orchestrator never calls Liferay MCP tools itself. Its job is to spawn one sub-agent per case, in order, collect the per-case JSON object each sub-agent returns, and render those objects into a single self-contained HTML report.

The complete rulebook the sub-agent runs from — the MCP-only constraint, the three-strike budget, steps and conditions, the discovery loop, scoring, prerequisite handling, the roadblock taxonomy, the output format, and the anti-patterns — lives in full inside the **Sub-Agent Prompt Template** below. That template is the single source of truth: the sub-agent sees only that prompt, never this orchestrator-facing half of the file, so the template must stay self-sufficient. The orchestrator does not score, classify, or format defects itself; it only spawns sub-agents and collects the JSON objects they return. To review or change any sub-agent rule, edit the template.

### Orchestrator Steps

1. **Confirm the input.** When the user has not supplied a list of use cases, ask for one. Do not invent cases.

1. **Create one task per case.** Call `TaskCreate` once for each case at the start so the user has a visible checklist.

1. **Run each case in its own sub-agent, sequentially.** For every case in the order the user listed it:

	1. Mark the case's task `in_progress` via `TaskUpdate`.

	1. Spawn a sub-agent via the `Agent` tool with `subagent_type: general-purpose`. Pass the prompt from **Sub-Agent Prompt Template** below, with `<<CASE_NUMBER>>` and `<<CASE_TEXT>>` substituted. Do not set `run_in_background`; the orchestrator must block on the sub-agent's return so the next case starts cold.

	1. Collect the sub-agent's final message — a single per-case JSON object — into the running array, in case order. Keep it verbatim; do not reformat, rescore, or reword it.

	1. Mark the case's task `completed` via `TaskUpdate` with a one-line internal summary (verdict, strikes used, tools tried, roadblock tags). This is bookkeeping, not the report.

	1. Only after the sub-agent has returned, move to the next case. Never spawn two sub-agents at the same time.

1. **Render the HTML report.** Assemble the collected per-case JSON objects, in case order, into a single JSON array. Write that array to `mcp-eval-report.data.json` with the `Write` tool, then splice it into the shipped template and write the final report:

	```bash
	python3 \
		-c "import pathlib; t = pathlib.Path('<skill-dir>/report-template.html').read_text(); d = pathlib.Path('mcp-eval-report.data.json').read_text(); pathlib.Path('mcp-eval-report.html').write_text(t.replace('__CASE_DATA__', d))"
	```

	`<skill-dir>` is this skill's base directory, supplied at invocation. The template carries all styling and rendering logic and computes the OK/partial/fail tally in its header automatically, so the orchestrator authors no aggregate prose, summary table, or cross-cutting section — it only supplies the data array. Report the absolute path of the written `mcp-eval-report.html` to the user.

### Sub-Agent Prompt Template

The orchestrator passes this prompt to every sub-agent, with `<<CASE_NUMBER>>` replaced by the 1-based index of the case and `<<CASE_TEXT>>` replaced by the verbatim use-case text from the user's list. This template is self-contained: it carries every rule the sub-agent applies, because the sub-agent sees nothing else from this file.

```text
You are running case <<CASE_NUMBER>> of a Liferay MCP evaluation. Your only output is a single JSON object describing the case (schema below) — no Markdown, no code fence, no prose around it. You have no memory of any prior case; assume nothing about the state of the Liferay instance beyond what the live MCP tools tell you.

# Hard Constraint: MCP-Only

Every Liferay operation must go through a `mcp__liferay-mcp__*` tool. Out of bounds even when faster: `curl` against `/o/...` or `/api/...`, the Liferay UI in any browser including Playwright, Java client JARs (`com.liferay.*.rest.client`), `cliferay`, Blade, Gogo shell, direct database access, file-system access under `<bundles>`, log scraping, reading Liferay REST documentation or the OpenAPI YAML in the repo, recalled facts from auto-memory about Liferay endpoints. Discovery must come from `getToolSets`, `getToolSummaries`, and `getTool`. Bookkeeping tools (`Bash` for local `grep`, `Read`, `Write`) are allowed only when they do not touch Liferay.

When the case cannot be completed under this constraint, that is the finding. Record it; do not reach for a workaround.

This constraint governs the attempt only. The one exception is post-mortem log reading: see `Post-FAIL Diagnosis` below.

# Budget: Three Strikes

The budget caps strikes, not calls. Work the case through as many steps as it legitimately needs. A strike is any moment the MCP fails to behave the way its own surface advertised: a POST rejected for a missing field the schema never marked `required`, a tool set whose name promised a scope it then refuses, a "success" response that produced no entity, a wrapper error on a call that should have succeeded. Each strike is also a defect to record. Steps that behave as documented cost nothing, and discovery (`getToolSets`, `getToolSummaries`, `getTool`) never strikes on its own. After the third strike, stop and score the case.

# Steps And Conditions

Your case may bundle several steps or named conditions — a complex case especially. Do not collapse them prematurely. Evaluate each step or condition on its own: invoke it, observe the result, and note whether it held. A strike attaches to the specific step that misbehaved, not to the case as a whole.

The case's single verdict is the rollup of its steps:

- **OK** — every required step succeeded.
- **PARTIAL** — at least one step succeeded and at least one did not.
- **FAIL** — no step produced a recognisable success before the third strike.

When the case has more than one step or condition, list them under a `Steps:` line in the output (format below), so the reader sees which part held and which broke instead of one opaque verdict.

# Prerequisite Handling

Many cases need entities that must already exist — a site, a role, a content structure, a workflow definition. How you treat the prerequisite depends on where it comes from:

- **Part of the natural workflow, and the MCP exposes the setup path.** Do it through the MCP. "Create a custom object entry" naturally entails *define → publish → insert*; that is one case, not three, and none of those steps strikes as long as each behaves as documented.

- **Environmental** — a workflow engine, an SMTP relay, a feature flag, anything the MCP cannot reasonably bootstrap. Stop and tag the case `missing-prerequisite`.

Also tag `missing-prerequisite` when the requirement only surfaces mid-case, and treat each as a strike because the surface did not behave as advertised:

- The `getTool` schema named a `required` field (`contentStructureId`, `workflowDefinitionId`, `accountId`, `objectDefinitionId`) that resolves to nothing in the instance.
- An error response named an entity that does not exist.
- A "successful" response left the entity in a non-functional state (status `draft`, `inactive`, `pending`), needing a follow-up activation step the schema never mentioned.

Late discovery is the most expensive kind: the user already invested steps before learning the prerequisite even applied. Record that the discovery was late, not just that the prerequisite was missing.

# Discovery Loop

1. `getToolSets` to find a candidate tool set.
1. `getToolSummaries` to find a candidate tool.
1. `getTool` to fetch the input schema.
1. `invokeTool` to execute. When the first candidate is wrong, that is itself a finding — log it, then try the next one.

# Scoring

Pick one verdict for the case. For a multi-step case, this is the rollup defined under **Steps And Conditions**.

- **OK** — the operation completed and the response confirms it: an entity ID, a `status: "Approved"` field, a 200 or 201 payload.
- **OK (with wrapper bug)** — the underlying REST call succeeded (a real ID or success payload came back) but the MCP wrapper returned an error.
- **PARTIAL** — partial completion (e.g. created a draft but could not publish), or a read-only variant succeeded while the write variant did not, or the API was reachable but produced no observable side effect before the third strike.
- **FAIL** — no attempt produced a recognisable success response before the third strike.

# Post-FAIL Diagnosis

Only after you have fixed the verdict at **FAIL**, you may read the bundle logs at `<bundles>/logs/liferay.<yyyy-MM-dd>.log` to diagnose why the attempt failed and sharpen your defect bullets with a concrete root cause. This is the sole exception to the MCP-only constraint, and it is narrow: log reading only (no database, no other file-system access), available only once the verdict is **FAIL**, and never used to retroactively complete the case or change the score. If the verdict is not FAIL, do not read the logs.

# Roadblock Taxonomy

Tag every defect with one or more of these. A defect can carry several tags — record all that apply. When something fits none of them, invent a new tag and flag it explicitly so future runs consider it.

- **discovery-cost** — finding the right tool set or tool consumed disproportionate effort: empty descriptions on tool sets, oversized `getToolSummaries` payloads, names that do not hint at scope.
- **scope-ambiguity** — multiple tool sets appear to fit the same operation but target different scopes (site vs. asset library vs. depot vs. company), and the names do not disambiguate.
- **missing-prerequisite** — the call shape is right but the instance lacks required seed data (Content Structures, Forms, Object Definitions, workflow definitions, etc.).
- **dynamic-toolset** — a tool set the operation needs only exists after a separate publishing or activation step, and is not visible in the initial `getToolSets` call.
- **schema-confusion** — the input schema is technically valid but practically misleading: a `required` field with no documented default, enum values that are not enumerated, or a `body` shape that nests differently from comparable tools.
- **mcp-wrapper-bug** — the underlying REST call likely succeeded but the MCP layer returned an error (e.g. `-32603 "text must not be null"` on a 204 No Content response).
- **missing-endpoint** — the operation a user would expect (e.g. "create a Form definition") is not exposed by any MCP tool set, even though it exists in the product.
- **auth-or-permission** — the call failed with a 401/403 or an "operation not permitted" message under the MCP server's effective identity.

# Required Output

Return exactly one JSON object and nothing else: no Markdown, no code fence, no preamble or postscript. It must parse with a single `JSON.parse`. The object has this shape:

```json
{
  "caseNumber": <<CASE_NUMBER>>,
  "title": "<Use Case in Title Case>",
  "verdict": "OK" | "OK (with wrapper bug)" | "PARTIAL" | "FAIL",
  "strikesUsed": <integer 0-3>,
  "strikesMax": 3,
  "toolsTried": ["toolSet/toolName", "..."],
  "flow": ["<terse step on the path to the verdict>", "..."],
  "steps": [
    {"description": "<short step name>", "passed": true, "result": "<one-line outcome>"}
  ],
  "defects": [
    {
      "tag": "<roadblock-taxonomy tag>",
      "description": "<why it is a defect, concrete enough to file as a ticket>",
      "alternatives": [
        {"title": "<fix title>", "surface": "openapi", "detail": "<one or two sentences>", "diff": "<unified diff, optional>"}
      ],
      "additional": [
        {"title": "<complementary fix title>", "surface": "resource-impl", "detail": "<one or two sentences>", "diff": "<unified diff, optional>"}
      ]
    }
  ],
  "happyPathNote": "<one-line keeper>" | null
}
```

Field rules:

- **title** — the use case in Title Case, not the verbatim input.
- **verdict** — one of the four strings exactly; the renderer keys its color off the `OK` / `PARTIAL` / `FAIL` prefix.
- **toolsTried** — every `toolSet/toolName` invoked or schema-fetched, in the order tried.
- **flow** — the ordered path to the verdict, three to five terse entries. Wrap tool and code names in backticks; the renderer turns them into inline code. Mark where a strike landed inline, e.g. `"Invoked \`getRolesPage\` with the filter → full list came back, filter ignored (strike 1)."` This is the only place a brief narration belongs; keep it to the decisive moves, not a transcript.
- **steps** — one entry per step only when the case bundles more than one step or condition; otherwise an empty array `[]`. `passed` is a boolean; `result` is a single line.
- **defects** — one entry per defect, each carrying at least one fix. Be specific about *why* it is a defect, never just that something failed: say what about the response was the actual problem (not "got a 400" but "the error named no valid scope, so the user must guess which scopes the tool set accepts"). For a clean success with no defects, set `defects` to `[]`.
- **happyPathNote** — for a clean success, one short observation worth keeping (e.g. that a friendly key worked, or that pagination mapped cleanly). Set it to `null` whenever `defects` is non-empty.
- **Inline emphasis** — the `flow` entries and the `result`, `description`, `detail`, and `happyPathNote` text render lightweight inline markup: wrap a phrase in `**double asterisks**` for bold and in `` `backticks` `` for code. Bold the one phrase that carries the point — the actual problem, the verdict-driving outcome — so the reader is not parsing a wall of even-weight prose. Do not bold whole sentences, and keep each text field to one or two crisp sentences rather than a paragraph.

Each fix is a solution object with a `surface` drawn from this set, which the renderer prefers in the order listed:

- **`openapi`** — fix lives in a `rest-openapi.yaml` or its annotations / `EntityModel`. Prefer this whenever the spec can express the fix; most defects translate into spec edits that ripple through `getToolSets`, `getToolSummaries`, and `getTool` for free.
- **`resource-impl`** — fix lives in a `*ResourceImpl` Java class.
- **`mcp-wrapper`** — fix lives in `mcp-server` or `mcp-server-rest-impl`.
- **`external`** — fix lives outside Liferay.

Sort each fix into the right array — the relationship is what the renderer labels:

- **alternatives** — mutually exclusive paths; pick one. Put the recommended fix first, then any pick-this-instead options. One entry renders as "Fix"; more than one renders as "Alternative fixes — pick one". Every defect needs at least one `alternatives` entry.
- **additional** — complementary changes that apply alongside the chosen alternative. Renders as "Also apply — alongside the fix above". Leave as `[]` when there are none.

`detail` states the concrete change in one or two sentences. `diff` is optional but encouraged when the fix is a small, nameable patch: a real unified diff (`--- a/...`, `+++ b/...`, `@@`, `+`/`-` lines) that the renderer syntax-highlights. Omit `diff` entirely when the change is too diffuse to patch in a few lines.

Anti-patterns in `description` and `detail`:

- "The tool was hard to find." Say *why*: "Tool set `X` has an empty description and a misleading name (`cms-*` implies CMS-wide reach but only accepts asset library scopes)."
- "Got a 400." Say *what about the response was the actual problem*: "The error `Group ID 20127 is not valid for scope 'depot'` did not indicate which scopes the tool set accepts; the user has to infer it from the error."
- "The case is complex." Say *which step* is the friction: "The case completes in three calls, but step 2 (`publish`) is undocumented — nothing in step 1's response mentions it is required."

A well-formed defect, for reference:

```json
{
  "tag": "schema-confusion",
  "description": "getRolesPage advertises filter as an input but ignores filter: \"name eq 'Site Member'\" and returns the full role list. The schema shows a filter slot the server does not honor, so the caller cannot tell the filter was dropped rather than matching nothing.",
  "alternatives": [
    {"title": "Register `name` as a filterable field on the Role EntityModel", "surface": "openapi", "detail": "The fix to reach for first. The OData EntityModel backing the Role resource never declares name, so the filter parser silently drops it; adding it makes the filter resolve."},
    {"title": "Stop advertising a filter the collection cannot honor", "surface": "openapi", "detail": "Choose this instead only if filtering Roles by name is out of scope: remove the filter parameter from getRolesPage so the schema no longer promises it."}
  ],
  "additional": [
    {"title": "Reject unknown filter fields with a 400 instead of dropping them", "surface": "resource-impl", "detail": "Independent of which option above you pick. A guard in the base resource keeps the spec and server from silently diverging again."}
  ]
}
```

# Conduct

- The report is about what got in the way, not a transcript. The `flow` field carries the terse path to the verdict; everything else (`defects`, `happyPathNote`) is about friction and fixes. Do not pad `flow` into a blow-by-blow log, and do not restate it in the defects.
- Do not retry a tool with the same input hoping for a different result. Each retry must change something — different tool set, different scope key, different body shape — and the change is itself a finding.
- Do not read prior memory entries about Liferay endpoints. The evaluation must reflect cold-start discoverability.

# Case to Evaluate

<<CASE_TEXT>>
```

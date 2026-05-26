---

allowed-tools: [Bash, Read, Write, TaskCreate, TaskUpdate, TaskList, mcp__liferay-mcp__getToolSets, mcp__liferay-mcp__getToolSummaries, mcp__liferay-mcp__getTool, mcp__liferay-mcp__invokeTool]
description: Evaluate Liferay MCP discoverability and usability by attempting a list of user-supplied use cases against a live Liferay instance, with a bounded retry budget per case. Produces a per-case verdict (OK / PARTIAL / FAIL), the roadblocks hit (discovery cost, scope ambiguity, missing prerequisites, schema confusion, MCP wrapper bugs, missing endpoints, auth or permission), and a concrete fix for each defect tagged by the surface that owns the change (OpenAPI spec, resource impl, MCP wrapper, external). Use when the user asks to evaluate the Liferay MCP, test its discoverability, or report on how well an AI can accomplish typical Liferay operations through it.
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
- Direct database access, file-system access under `<bundles>`, log scraping for verification.
- Reading Liferay REST documentation, the OpenAPI YAML in the repo, JSPs, or service interfaces to "know" what to call. Discovery must come from `getToolSets` / `getToolSummaries` / `getTool` — that is the surface under test.
- Recalled facts from prior conversations or auto-memory about specific Liferay endpoints, scope keys, or tool sets. Start cold.

The only allowed exceptions are bookkeeping tools that do not touch Liferay: `TaskCreate` / `TaskUpdate` / `TaskList` for tracking, `Bash` for trivial local operations like `grep` on a saved tool-result file, and `Read` / `Write` to produce the final report.

When a case cannot be completed under this constraint, **that is the finding**. Record it as a roadblock and move on. Do not reach for a workaround — the workaround would erase the data point the evaluation exists to collect.

## Input

A list of use cases, one per line, supplied by the user. Each use case is a short natural-language description of a Liferay operation, e.g.:

- "Create a web content article"
- "Upload a document to a site"
- "List pending workflow tasks"

When the user asks for an evaluation without providing cases, ask them for the list before proceeding. Do not invent cases.

## Constraints

- **Bounded retries.** At most **3 invocation attempts** per use case (across all tools). When that budget is spent, record the case as **PARTIAL** or **FAIL** and move on. Discovery steps (`getToolSets`, `getToolSummaries`, `getTool`) do not count against the budget.

- **Case isolation (temporary: sequential, in-context).** The ideal execution model is one cold sub-agent per use case, running in parallel — each agent starts with no memory of the others, which makes isolation automatic. That is not currently viable due to infrastructure limitations, so for now cases are run **sequentially in the same context**, and isolation must be enforced by discipline: do not reuse IDs, external reference codes, scopes, or tool sets discovered in a prior case; re-run the discovery steps every time, even when you "know" the answer from the previous case. The signal we are measuring is cold-start discoverability, and any context that leaks between cases inflates it. When the parallel-agent infrastructure becomes available, switch to that model and delete this caveat.

- **Prerequisite handling.** Many cases require entities that must already exist (a site, a role, a content structure, a workflow definition). When the prerequisite is part of the natural workflow and the MCP exposes the setup path, do it via the MCP and count those calls against the case's 3-attempt budget — e.g. "create a custom object entry" naturally entails *define → publish → insert*, which is one case, not three. When the prerequisite is **environmental** — a workflow engine, an SMTP relay, a feature flag, anything the MCP cannot reasonably bootstrap inside the retry budget — stop and tag the case `missing-prerequisite`. The retry budget itself is the backstop: when setup eats the whole budget before the real case begins, that *is* the finding.

  Also tag `missing-prerequisite` when the requirement only surfaces **mid-case** — the `getTool` schema named a `required` field (`contentStructureId`, `workflowDefinitionId`, `accountId`, `objectDefinitionId`) that resolves to nothing in the instance; an error response named an entity that does not exist; or a "successful" response left the entity in a non-functional state (status `draft`, `inactive`, `pending`) requiring a follow-up activation step the schema never mentioned. Late discovery is the most expensive kind, because the user already spent attempts before learning the prerequisite even applied — record that the discovery was late, not just that the prerequisite was missing.

## Workflow

The evaluation runs sequentially through the cases. Each case follows the same four steps.

### Create A Task Per Use Case

Use `TaskCreate` once per case at the start. This produces a checklist the user can watch and ensures every case ends with a verdict.

### Run Each Case In Isolation

Follow the four-step discovery pattern documented by the MCP server itself:

1. `getToolSets` — find a candidate tool set.
1. `getToolSummaries` — find a candidate tool inside it.
1. `getTool` — fetch the input schema.
1. `invokeTool` — execute.

Record every misstep along the way (see the Roadblock Taxonomy section). When the first candidate tool set or tool turns out to be wrong, that is itself a finding — log it, then try the next one. After three invocation attempts, stop.

### Score The Case

Pick one verdict:

- **OK** — the operation completed and the response confirms it (an entity ID, a `status: "Approved"` field, a 200 or 201 payload).

- **PARTIAL** — the operation partially completed (e.g. created a draft object but could not publish), a read-only variant of the operation succeeded while the write variant did not, or the API was reachable but produced no observable side effect within budget.

- **FAIL** — no attempt produced a recognisable success response within the budget.

Append `(with wrapper bug)` to **OK** when the underlying REST call succeeded but the MCP wrapper returned an error.

### Move On

Mark the `TaskCreate` task `completed` with a short internal summary (verdict, attempts used, tools tried, roadblock tags) — this is bookkeeping, not the report. Then start the next case. Do not return to a failed case later — the evaluation measures first-pass discoverability.

## Roadblock Taxonomy

Classify each roadblock under one of these categories. When something does not fit, invent a new category — flag it explicitly in the case's defect bullet so future runs know to consider it.

- **discovery-cost** — finding the right tool set or tool consumed disproportionate effort: empty descriptions on tool sets, oversized `getToolSummaries` payloads, names that do not hint at scope.
- **scope-ambiguity** — multiple tool sets appear to fit the same operation but target different scopes (site vs. asset library vs. depot vs. company), and the names do not disambiguate.
- **missing-prerequisite** — the call shape is right but the instance lacks required seed data (Content Structures, Forms, Object Definitions, workflow definitions, etc.).
- **dynamic-toolset** — a tool set that the operation needs only exists after a separate publishing or activation step, and is not visible in the initial `getToolSets` call.
- **schema-confusion** — the input schema is technically valid but practically misleading: a `required` field with no documented default, enum values that are not enumerated, or a `body` shape that nests differently from comparable tools.
- **mcp-wrapper-bug** — the underlying REST call likely succeeded but the MCP layer returned an error (e.g. `-32603 "text must not be null"` on a 204 No Content response).
- **missing-endpoint** — the operation a user would expect (e.g. "create a Form definition") is not exposed by any MCP tool set, even though it exists in the product.
- **auth-or-permission** — the call failed with a 401/403 or an "operation not permitted" message under the MCP server's effective identity.

A case can carry multiple roadblock tags. Record all of them.

## Output

End with a single report containing one block per case. Do not add a summary table, cross-cutting observations, or any other aggregate section — each case's block stands alone.

### Per-Case Block

One block per case. The block heading is `### Case <#> — <Use Case in Title Case>`. Inside the block, render this metadata as a tight bullet list:

- **Verdict:** `OK`, `OK (with wrapper bug)`, `PARTIAL`, or `FAIL`.

- **Attempts:** `<used> of 3`.

- **Tools tried:** comma-separated `toolSet/toolName` entries.

After the metadata, list defects as bullets under a `Defects:` line. Each defect bullet leads with the taxonomy tag in bold, followed by an em dash, followed by the specific defect — concrete enough to file as a ticket without further context. Capture what the MCP server team would need to change to remove the friction next time, not what you did and not what failed.

**Every defect must include at least one `Fix` sub-bullet** stating the concrete change that would remove the friction, tagged by the surface that owns the change. The discipline is to **prefer OpenAPI-level fixes whenever the friction can be solved by editing a `rest-openapi.yaml` or its surrounding annotations**, falling back to resource-impl or wrapper changes only when the spec cannot express the fix. The MCP surface is driven by what the OpenAPI documents say, so most defects translate into spec edits that ripple through `getToolSets`, `getToolSummaries`, and `getTool` for free.

When a defect has multiple fix sub-bullets, **the bullet's leading word makes the relationship explicit**:

- **`Fix [tag]`** — the primary fix, listed first.

- **`Also [tag]`** — a complementary change that applies together with the `Fix` above. Apply both.

- **`Or [tag]`** — an alternative path. Pick this instead of the `Fix` above; do not apply both.

Order the bullets so the recommended path reads top-down: `Fix` first, then any `Also`s that go with it, then any `Or` alternatives. A reader scanning the block should be able to draw the dependency from the leading words alone, without parsing the prose.

Fix-surface tags:

- **`[openapi]`** — the fix lives in a `rest-openapi.yaml` (or the annotations and `EntityModel` declarations that feed it). Adding an `info.description`, populating a parameter's `description` / `example` / `enum` / `pattern`, `$ref`-ing a nested schema instead of inlining it, exposing a missing listing path, adding an `x-liferay-*` extension, or aligning a generated schema with what the resource actually accepts. Regenerated via `buildREST`.

- **`[resource-impl]`** — the fix lives in a `*ResourceImpl` Java class: making a required parameter optional with a default, accepting an additional identifier form, returning a 400 on unsupported filter fields, exposing a higher-level helper endpoint that bundles a common multi-step flow.

- **`[mcp-wrapper]`** — the fix lives in `mcp-server` or `mcp-server-rest-impl`: how the MCP layer translates a REST response into a tool result (e.g. 204 handling), how `getTool` collapses or paginates large schemas, or what fields the `ToolSet` / `Tool` DTOs expose.

- **`[external]`** — the fix lives outside Liferay (auto-mode classifiers, harness behaviour, third-party agents). Record the defect but make it clear Liferay cannot resolve it.

When a case has no defects (a clean success), omit the `Defects:` line entirely and write one short line under the metadata stating the happy-path observation worth keeping (e.g. that a friendly key worked, or that pagination mapped cleanly). Do not narrate the steps.

Anti-patterns to avoid in defect bullets:

- "The tool was hard to find." Say *why*: "Tool set `X` has an empty description and a misleading name (`cms-*` implies CMS-wide reach but only accepts asset library scopes)."

- "Got a 400." Say *what about the response was the actual problem*: "The error `Group ID 20127 is not valid for scope 'depot'` did not indicate which scopes the tool set accepts; the user has to infer it from the error."

- "The case is complex." Say *which step* is the friction: "The case completes in three calls, but step 2 (`publish`) is undocumented — nothing in step 1's response mentions it is required."

- "The wrapper should accept 204 as success." Without a `Fix` tag, a reader cannot tell which module owns the change. Always state the surface and, when useful, the file or DTO that would be edited.

Good defect bullets, for reference:

- **schema-confusion** — `getRolesPage` advertises **filter** as an input but ignores `filter: "name eq 'Site Member'"` and returns the full role list. The schema shows a filter slot the server does not honor.
	- **Fix `[openapi]`** — in `headless-admin-user-impl/rest-openapi.yaml`, add `name` to the **Role** resource's `EntityModel` so the filter actually works.
	- **Or `[openapi]`** — remove the **filter** slot from the **Role** collection so the schema stops advertising it.
	- **Also `[resource-impl]`** — have the base resource return a 400 on unknown filter fields instead of silently dropping them, so the spec and server cannot diverge again.

- **dynamic-toolset** — `c-mcpbooks` appears in `getToolSets` as a bare name with no description and no marker that it is a custom-object surface.
	- **Fix `[openapi]`** — when Liferay generates the per-Object-Definition OpenAPI, populate `info.title` with the definition label, `info.description` with the definition ERC and scope, and add an `x-liferay-kind: custom-object` extension.
	- **Also `[mcp-wrapper]`** — extend the `ToolSet` DTO in `mcp-server-rest-api` to surface `x-liferay-kind` as a `kind` field, so agents can filter `getToolSets` by it.

- **mcp-wrapper-bug** — `postRoleUserAccountAssociation` returns HTTP 204 on success, but the MCP layer feeds the empty body straight into `McpSchema.TextContent` and emits `-32603 "text must not be null"`.
	- **Fix `[mcp-wrapper]`** — in `MCPServerServlet._call`, when `responseCode < 300` and the body is null, substitute a `{"status":<code>}` payload before building the `CallToolResult`. See `127bcf1`.

- **discovery-cost** — `getTool` for `postSiteSitePage` returns ~618 KB / 8,605 lines because nested types (**PageDefinition**, **PageElement**, **FragmentInstance**) are inlined at every reference instead of `$ref`-d.
	- **Fix `[openapi]`** — restructure `headless-admin-site-impl/rest-openapi.yaml` so each large nested type is declared once under `components.schemas` and `$ref`-d everywhere it is reused.
	- **Or `[mcp-wrapper]`** — have `getTool` replace deep nested schemas with `{"$ref": "..."}` and let agents fetch nested types on demand via a new `getSchema` endpoint.

## Anti-Patterns

- **Do not** summarise what you did. The report is about what got in the way, not about the happy path. A clean success gets only its metadata plus one happy-path observation; do not narrate the steps.

- **Do not** retry a tool with the same input hoping for a different result. Each retry must change something — different tool set, different scope key, different body shape — and the change is itself a finding.

- **Do not** use the Agent tool to delegate cases. The discoverability cost is the signal; off-loading the work to a sub-agent hides it.

- **Do not** read prior memory entries about Liferay endpoints. The evaluation must reflect cold-start discoverability.

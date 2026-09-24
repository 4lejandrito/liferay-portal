# format-source eval

Instructions for running this eval. The human-facing write-up, with the results
and what they mean, is in `RESULTS.md`.

## Why it exists

A review pass over a real branch can only be compared against other passes:
nothing says how many real problems the branch held, so "found 8 things" has no
denominator. Here the violations are placed deliberately and written down, so
recall and false positives are both measurable and two runs months apart are
comparable.

## Layout

- `review/` holds the ten files under review.
- `merged/` holds the already-merged counterparts they were modelled on. This is
  what the skill's `## Existing Code Wins` section points at.
- `ANSWERS.md` is the scoring sheet. **Never give it to the agent being scored.**
- `RESULTS.md` is the write-up of the runs done so far.

Rounds four, five and six do not use this fixture at all. They go back to the
real branch, where the denominator is the six blocks Georgel rewrote by hand
rather than anything seeded. Their answer key, prompts and per-run findings sit
outside the repository, in
`/media/georgelpop/Data/liferay/Tickets/format-source-eval/`, one directory per
round, with the key in `round-four/ANSWERS.md`.

The first three files carry the original thirteen seeded defects and four traps,
and the seven added on 2026-09-11 carry twenty-two more and four more traps, for
thirty-five and eight over ten files. The first three are still scored as a
subset, because the first batch-size matrix ran when they were the whole fixture.

Everything is `.java.txt` on purpose: nothing here should be compiled, formatted,
or picked up by the source formatter, and the line numbers in the answer key have
to stay put.

## The branches behind it

Nothing here is invented. `review/` and `merged/` are modelled on a real branch
and its already-merged counterparts, and the hard three reproduce the ordering
defect that closed two pull requests. That same branch is what rounds one, four,
five and six were measured on.

Those commits live only on Georgel's fork, `georgel-pop-lr/liferay-portal`, and
none of them sits on a branch any more: the branch was rebased past them and the
old tips became unreachable. Each is pinned by a tag so a `git gc` cannot take
it. Fetch the tags before rerunning anything that cites a real branch.

- `format-source-eval-round-one-head` is `94695e0869000`, the frozen head of
  branch `LPD-104528` Enable Page Templates Admin in Design Libraries
  (https://liferay.atlassian.net/browse/LPD-104528) as it stood on 2026-09-09:
  115 files changed, 4706 added lines, 41 of them effective review files.
  https://github.com/georgel-pop-lr/liferay-portal/commit/94695e0869000
- `format-source-eval-round-one-base` is `3c48ca5688ea6`, the commit that head
  was diffed from.
  https://github.com/georgel-pop-lr/liferay-portal/commit/3c48ca5688ea6
- `format-source-eval-round-one-fixes-permissions` is `a5d27bec23e71`, with
  `b6786366ad49d` as its parent: the two hand fixes for LPD-104558 Grant the
  design library roles the add, update, and delete permissions on layout page
  templates (https://liferay.atlassian.net/browse/LPD-104558), sorting the
  permission entries and then the permission assertions. This is the defect the
  hard three reproduce, and the ground truth the ordering family was adjudicated
  against, so that half is not a model judging a model.
  https://github.com/georgel-pop-lr/liferay-portal/commit/a5d27bec23e71
- `format-source-eval-round-one-fixes-creation-items` is `ed1df74ec319c`, the
  third hand fix, sorting the creation items for LPD-104840 Add the page
  template set design library resource type contributor
  (https://liferay.atlassian.net/browse/LPD-104840).
  https://github.com/georgel-pop-lr/liferay-portal/commit/ed1df74ec319c

Read that state by tag and never by branch name: `LPD-104528` has moved well past
the frozen head, and nothing on `liferay/liferay-portal` carries any of these
commits.

A second real branch, `LPD-104529` Content Page Template usage in page creation
from Design Library (https://liferay.atlassian.net/browse/LPD-104529), is the
planned counterpart at a comparable size: 124 files, 4704 added lines outside
`Language_*`, 54 effective review files. It is not frozen yet, because the story
is still being written. Tag it the same way once it lands.

## Running one pass

Give a fresh agent, with no history, a prompt containing:

1. The path to the skill body (`../SKILL.md`), to be read in full first.
2. The paths of the files under review, in `review/`.
3. The paths of the merged counterparts, in `merged/`.
4. The procedure being tested. For the per-file shape: work one file at a time,
   read the file, read its merged counterpart, apply every applicable rule,
   record findings, then move on.
5. These constraints: report only, no edits, no gradle or ant, and no subagents.

For a batch size other than one, say the groups explicitly (files 1 to 4, then 5
to 8, then 9 and 10) rather than naming a number, so two runs of the same cell
review the same groups.

Ask for exactly this output, and nothing else:

	# Findings: <n>

	- <file name>:<line> | rule <n> | <one sentence: what is wrong and the fix>

Use the bare file name and the line number counted from 1 in that file.

## Scoring a pass

Against `ANSWERS.md`:

- **Recall**, how many of the 35 seeded violations came back, how many of the
  original 13, and separately how many of the hard three, since those are what
  distinguish a careful pass.
- **Traps**, how many of the 8 traps were reported. Zero is the target. A trap is
  code a rule genuinely applies to, written exactly as the merged counterpart
  writes it, so the correct behaviour is silence.
- **Extras**, anything reported that is neither. Judge each on its merits: a run
  may find something real the key missed, and that is a finding about the key.

Match on file and line with a tolerance of two or three lines, because a run may
cite the opening line of a wrapped statement rather than the line naming the
symbol. Rule numbers are not scored strictly; `ANSWERS.md` lists the pairs that
describe the same fix.

Two reporting habits to record rather than penalise silently. A run may report
one defect once per affected variable, and a run may collapse three separate
blocks into a single finding. Both change the count without changing what was
detected, so note which happened.

## Adding to it

A rule that keeps producing false positives in practice earns a trap. A rule
nobody has seen fire is worth seeding once to find out whether it works at all.
Seed one defect per site: two defects on one line means runs report the other one
and the seeded miss looks like a recall failure.

When a fixture file changes, re-derive every line number in `ANSWERS.md` from the
file rather than adjusting the ones that look wrong.

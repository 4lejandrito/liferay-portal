# What we measured, and what came out of it

Written 2026-09-11. This is the human-readable half; `README.md` is the runbook
for actually running a pass.

## Where this started

The `format-source` skill runs an automatic formatter and then applies 47 manual
rules that the formatter cannot see. The question on the table was narrow: should
the skill be pinned to a cheaper model to save money. Answering it turned up
something more useful, so this file records both.

The thing that made it worth measuring is LPD-104558 Grant the design library
roles the add, update, and delete permissions on layout page templates
(https://liferay.atlassian.net/browse/LPD-104558). Two pull requests were
closed over a single defect class: `Layout.class.getName()` sorted after
`LayoutPageTemplateCollection` and `LayoutPageTemplateEntry`, first in a test, and
then, after that one was fixed and the pull resent, in the production contributor
the same change added. It was fixed by hand in three commits. The manual rules
should have caught it, and the skill as it ran then reported nothing.

## Round one: a real branch

A 41-file branch at a frozen head, reviewed by cold agents with an identical
prompt, only the model and the procedure differing. Findings were adjudicated
against the already-merged counterpart of each file, and for the ordering family
against those three fix commits, so that part is not a model judging a model.
The branch is `LPD-104528` Enable Page Templates Admin in Design Libraries
(https://liferay.atlassian.net/browse/LPD-104528); the head, its base and the
three fix commits are all pinned by tag on Georgel's fork, listed in
`README.md` under "The branches behind it", because none of them is reachable
from a branch any more.

| Shape | Model | Runs | Time | Cost | Real defects | False |
| --- | --- | --- | --- | --- | --- | --- |
| Whole diff in one pass | Sonnet | 2 | ~6m | $1.76 | 0 | 0 |
| Whole diff in one pass | Opus | 2 | ~8m | $2.80 | 2 to 6 | 4 to 5 |
| 47 rules split over 6 parallel agents | Sonnet | 1 | 8m 21s | $7.47 | 0 | 5 |
| One file at a time | Sonnet | 2 | 9 to 12m | $2.30 to $2.90 | 1 to 2 | 0 |
| One file at a time | Opus | 5 | 12 to 14m | $6.66 to $8.10 | 6 to 9 | 0, 3, 0, 0, 0 |

Three things came out of it.

Per-file scoping is the change that matters. Every one of the five per-file Opus
runs found the complete `Layout.class.getName()` family, and false positives fell
from four or five a run to none in four of five. No whole-diff pass managed both.

Splitting the rules across parallel agents was tried because it had been
suggested, and it lost on every axis: 2.7x the cost of one pass and nothing
valid, missing ordering defects a single pass catches every time. Splitting by
agent is what makes it expensive; the fixed cost is paid per agent.

Sonnet never found any of the confirmed defects at any scope tried.

## Round two: a fixture with a known answer

Round one has no denominator. It can say a run found eight real problems, not
what fraction of the real problems that is. So the fixture in this directory
places the violations deliberately, and writes down where they are.

The hard three reproduce the LPD-104558 shape: a repeated entry whose first
argument is identical in every row, so ordering is decided by the second, and the
short name sorts before the longer ones that share its prefix. In one of the three
blocks the outlier sits in the middle rather than last, so a run that finds the
defect by noticing the last entry looks wrong picks up two and misses one.

It contains **13 defects planted on purpose**, three of them the hard ones just
described, and **4 traps**. A trap is code a rule technically applies to, written
exactly the way the already-merged file writes it, so the right answer is to
leave it alone and say nothing. All four are false positives that real runs
actually produced on a real branch.

Thirty passes were run against it, five per cell: three batch sizes against two
models, everything else identical.

That gives two scores, running in opposite directions:

- **Defects found**, out of 13. Higher is better; 13 means the run found every
  planted defect.
- **Traps wrongly reported**, out of 4. Lower is better; 0 means the run fell for
  none of them.

A third column counts how many of the five runs in that row were flawless, that
is, found all 13 planted defects with nothing missed. "5 of 5" means the
configuration got everything every single time; "1 of 5" means it did so once and
the other four runs each missed something. It is there because an average hides
consistency: a configuration that scores 13 every time and one that alternates
between 8 and 13 can look similar on paper and behave very differently in use.

Each row below is one configuration run five times. The "per run" column lists
all five results rather than averaging them, because the spread turned out to
matter more than the average.

| Files per pass | Model | Defects found, per run (13 planted) | Runs (of 5) that found all 13 | Traps wrongly reported | Cost per run | Time per run |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Opus | 13, 13, 13, 13, 13 | 5 of 5 | none | $0.83 | 127s |
| 2 | Opus | 13, 13, 13, 13, 13 | 5 of 5 | none | $0.78 | 139s |
| 3 | Opus | 13, 13, 13, 13, 13 | 5 of 5 | none | $0.73 | 112s |
| 1 | Sonnet | 12, 13, 9, 10, 10 | 1 of 5 | none | $0.33 | 264s |
| 2 | Sonnet | 11, 12, 10, 12, 9 | 0 of 5 | none | $0.29 | 258s |
| 3 | Sonnet | 8, 11, 12, 13, 11 | 1 of 5 | none | $0.28 | 248s |

In words: every Opus run found all 13 planted defects, whatever the batch size.
Sonnet found between 8 and 13, changing from run to run on identical input, and
found all 13 in only 2 of its 15 runs. Neither model reported a single trap in
any of the 30 runs.

## Round three: ten files, and where the ceiling actually is

Round two saturated. Opus scored full marks in all fifteen runs, so nothing above
three files could be ranked, and the obvious question was whether four, five or
ten files behave differently.

The fixture grew from three files to ten: 683 lines under review against 639
lines of merged counterparts, **35 seeded defects and 8 traps**. The original
thirteen are still scored on their own, so these runs sit beside round two's
without either number being reconstructed. Thirty more passes, five per cell,
three batch sizes against two models.

| Files per pass | Model | Of 35 planted, per run | Of the original 13 | Traps (8 per run) | Cost per run | Time per run |
| --- | --- | --- | --- | --- | --- | --- |
| 4 | Opus | 35, 35, 35, 34, 34 | 13, 13, 13, 12, 12 | none | $0.92 | 178s |
| 5 | Opus | 35, 35, 35, 35, 35 | 13, 13, 13, 13, 13 | none | $1.11 | 207s |
| 10 | Opus | 35, 35, 35, 35, 35 | 13, 13, 13, 13, 13 | none | $1.01 | 190s |
| 4 | Sonnet | 32, 32, 31, 31, 30 | 11, 11, 11, 9, 9 | 1 in 5 runs | $0.47 | 357s |
| 5 | Sonnet | 31, 29, 28, 28, 28 | 9, 8, 7, 7, 7 | 3 in 5 runs | $0.40 | 343s |
| 10 | Sonnet | 30, 30, 30, 30, 29 | 9, 9, 8, 9, 7 | none | $0.47 | 335s |

Thirty runs, $21.92.

**Opus does not degrade at ten files.** Twenty-eight of its thirty findings sets
are perfect, and the two that are not are the same single miss, the blank line
splitting two parallel assertions, in two runs of the four-file cell. Reviewing
all ten at once was neither worse nor dearer than reviewing them four at a time.
Whatever broke the 41-file pass in round one, it is not reached by ten files of
this size.

**Sonnet degrades, and now the degradation is visible in the batch size.** On the
original thirteen it averaged 11.0 at three files per pass, 10.2 at four, 7.6 at
five and 8.4 at ten. Its ceiling on the full set never reached 35: the best of
its fifteen runs was 32. Two runs also reported traps, where Opus reported none
in any of the thirty runs across both rounds.

## Back to the real branch: the six key blocks

Rounds four, five and six drop the fixture and go back to the 41-file branch
round one used, at the same frozen head. There the denominator is not seeded, it
is the set of blocks Georgel himself rewrote afterwards in three hand-fix
commits, so the ground truth is a person's edit rather than a model's judgement.
There are six:

| Id | Where | The defect |
| --- | --- | --- |
| A1, A2, A3 | `LayoutPageTemplateDepotRolePermissionsContributor` | the `Layout.class.getName()` entry is out of order in each of the three role groups |
| B1 | `DepotRolesPortalInstanceLifecycleListenerTest` | six assertion calls in a mixed sequence, class literals and string literals interleaved |
| C1 | `LayoutPageTemplateDepotRolePermissionsContributorTest` | the test half of A, the same entry out of order among the asserts |
| D1 | `LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor` | two creation items in the wrong order |

A and C are a matched pair, the production contributor and its test. They are the
defect behind the two closed pull requests, and which batch each lands in turns
out to decide the whole result.

The answer key, the prompts and the per-run findings for these three rounds live
outside the repository, in
`/media/georgelpop/Data/liferay/Tickets/format-source-eval/`, one directory per
round.

## Round four: the whole branch in one pass, at full context

Three cold Opus runs over the whole diff at once, the prompt byte-identical to
round one's whole-diff prompt, the body this branch's with only the per-file
scoping removed and the merged-sibling check kept, and the session raised to the
full 1M window first.

Five of the six blocks in two runs, four in the third, D1 in none of the three.
One false positive in each of the first two. $13.78 for the three, 8 to 11
minutes each.

**Round one's "no whole-diff pass found more than one of them" does not hold
under these conditions.** It does not isolate why, because the window and the
body changed together and the 300k-with-this-body cell was never run. What
survives is narrower: per-file scoping buys reliability rather than reach, and
the block the third run dropped is C1.

## Round five: batches of ten over the same branch

Five cold Opus agents in parallel, one per batch of 9, 9, 9, 9 and 7 files, the
shipped body with the batch procedure overriding its one-file-at-a-time line.

**Six of six blocks and zero false positives, the first shape to find all six.**
$10.84, 9m 53s of wall clock.

On its own that reads as a clear win over both earlier shapes, and it is what
first moved the recommendation towards batching. It carried two qualifiers. It
was one run of the shape, not five, so it had no variance estimate. And the split
was favourable: one batch held A and C together, so the pass that had to find
four of the six could see both halves of the matched pair at once.

## Round six: the same batches with the matched pair split apart

Ten cold Opus agents, two splits of five batches, everything else identical to
round five. Split A assigns file *i* to batch *((i-1) mod 5)+1*, which separates
every natural main-and-test pair at once. Split B is round five's exact split
with one swap, moving the contributor test into another batch, so if the score
moves the pair is the only thing that can have moved it.

| Split | Key blocks, of 6 | Missed | Bullets | False |
| --- | --- | --- | --- | --- |
| A, round-robin | 5 | C1 | 8 | 1 |
| B, round five with the pair swapped | 4 | C1, D1 | 11 | 0 |
| Round five, pair together | 6 | none | 10 | 0 |

$28.77 for the ten runs, 8m 13s of wall clock.

**Both splits lost C1**, and in split B the pair being split is the only variable
that changed. Round five's six of six does not survive the separation.

**Round five's D1 was not deterministic either.** Split B's batch 3 is identical
in content to round five's batch 3, since the swap does not touch those files,
and it found D1 once and missed it once on the same input.

| Block | Per file (round one) | Whole diff (round four) | Batches of ten, pair together (round five) | Batches of ten, pair apart (round six) |
| --- | --- | --- | --- | --- |
| C1 | 5 of 5 runs | 2 of 3 runs | 1 of 1 | 0 of 2 |
| D1 | 1 of 5 runs | 0 of 3 runs | 1 of 1 | 1 of 2 |

Per-file scoping is the only shape that has found C1 every time it ran.

## The finding the score does not capture: a right block with a wrong fix

Every pass that has found the A family has named the wrong destination for the
entry. The hand fix puts `Layout.class.getName()` **second**, after the
`LayoutPageTemplateConstants.RESOURCE_NAME` entry. Round five said it "should
lead that group". Round six's split B said it "should be the first of the four
entries". Round six's split A went further and spelled out an order that
explicitly moves `RESOURCE_NAME` out of first place. Three passes for three,
across two rounds, each one different from the order that was actually merged.

It is not confined to that block. Both round-six splits flagged the same
`_setUpDesignLibraryScope` ordering break and gave opposite fixes, one saying it
belongs before `_setUpGroup` and the other after `_setUpStagingGroupHelper`.
Alphabetically the first is right.

Round four's whole-diff runs are the only ones to have phrased the A family
correctly, and they did it by claiming less: that the entry must precede the
other two, without naming a destination. So a pass can locate a defect reliably
and still hand a reviewer a fix that would not survive review, and positional
detail of exactly this kind is what closed the two pull requests. Finding the
block is necessary and it is not sufficient.

## What the numbers say

**Batch size does not change what Opus finds, up to ten files.** It scored full
marks in all fifteen runs at three files and in twenty-eight of thirty across
both rounds, whatever the grouping, and reviewing everything at once was never
dearer or slower than chunking. This does not contradict round one, where
per-file scoping beat a whole-diff pass on 41 files. It moves the point where
chunking starts to pay somewhere above ten files of this size, and says the
chunking is not free insurance below it.

**For Sonnet, batch size does matter.** Its recall on the original thirteen falls
from an average of 11.0 at three files per pass to 7.6 at five and 8.4 at ten. So
the scoping advice is model-dependent: it buys Opus nothing at this size, and
buys Sonnet something at every size.

**Almost nothing reported a trap.** Sixty runs over both rounds: Opus reported
none at all, in 120 opportunities at three files and 120 more at ten. Sonnet
reported four, all in round three. The merged-sibling check works when the merged
file is actually put in front of the reviewer.

**Opus and Sonnet are not interchangeable here.** Opus found all 13 in every one
of its 15 runs. Sonnet found all 13 in 2 of its 15, and in the other 13 runs it
missed between one and five. Its misses are consistent rather
than random: the blank line splitting two parallel assertions, missed in 10 runs
of 15, and the noun-only method name that rule 40 covers, missed in 8. In 6 runs
it reported the three hard blocks as a single combined finding, which detects the
defect but hands a reviewer one line instead of three fixes.

**Sonnet is cheaper and slower.** About a third of the cost, roughly twice the
wall clock, fewer but much longer turns.

**On a real branch, the batch is not the variable. The split is.** The fixture
says batch size does not change what Opus finds up to ten files, and the real
branch does not contradict that. What it adds is that where the boundary falls
matters more than how many files sit inside it: the same ten-file shape scored
six of six with the matched pair together and four or five of six with it apart.
A batch size is easy to write into a skill body. A split that keeps a production
file with its test is not, because nothing knows which files are a pair until
someone reads them.

## What we changed

The skill applies the manual rules one file at a time and requires a check
against the already-merged counterpart before anything is reported. That is the
commit this eval sits beside, and after six rounds it is still what the evidence
supports.

Round five looked for a while like it had overturned the first half. Batching ten
files per pass scored six of six against per-file's five, for less wall clock, and
the recommendation moved to batching on the strength of it. Round six took that
back: the one thing round five's split did that no other shape did was keep the
production contributor and its test in the same batch, and once they are
separated the shape loses C1 in both arrangements tried. Per-file scoping is the
only shape that has found C1 in every run of it, five for five.

The pin to a cheaper model was dropped. On the real branch Sonnet found none of
the confirmed defects, and on the fixture it misses a fifth of the seeded ones
and reports the hard cases less usefully. The saving is real and small; what it
costs is the findings.

## What this does not settle

Opus saturates the fixture. Ten files and 35 violations did not find its ceiling,
so the fixture's batch-size result still only says "not yet", not "never". The
next honest test there is bigger files rather than more of them: 683 lines under
review is a fraction of the 41-file branch, and total size is the more likely
variable behind that failure than file count.

The real-branch rounds have the opposite problem, too few runs rather than too
many. Round four is three runs, round five is one, and round six is two, against
five for per-file in round one. C1 at nought of two is a strong signal and it is
not the same thing as nought of five.

**Nothing here explains why a pass misses C1.** It is the least surprising of the
six blocks, an ordering break in a test file with a merged counterpart to compare
against, and the per-file shape finds it every time. What changed when it was
missed is only which other files shared the pass. That mechanism is unexamined.

The rules exercised on the fixture are 1, 2, 3, 4, 11, 12, 13, 14, 17, 19, 20, 23,
25, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 43 and 45, twenty-seven of the
forty-seven. The remaining twenty are untested there, and a rule nobody has
watched fire is a rule nobody knows works.

One methodological wrinkle in round three: the four-file runs were handed their
prompt inline, while the five- and ten-file runs were handed a path to the
identical prompt file and read it themselves. That is one extra small file read in
twenty of the thirty runs, which is not enough to explain any gap in the table,
but it is a difference and it is recorded here rather than smoothed over.

Two in round six. The worktree round five used no longer exists, so those runs
read the frozen head out of a different checkout, which is why the head is pinned
by tag. And `master` had moved on since round five, so the merged-sibling
adjudication of the non-key findings saw a slightly newer master than round five
did. Neither touches the key-block scoring, which is against the hand-fix commits.

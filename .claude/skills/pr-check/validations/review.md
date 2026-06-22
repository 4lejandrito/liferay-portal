# Review

## Trigger

Always a candidate.

## Match

`.`

## Command

Run `<repo-root>/pr-reviewer/run_local.sh`. The review usually takes 10-20 minutes — longer than a normal command timeout — so run it as a background task and wait for it to exit before reading its output; a command timeout is not a result, so do not report until the script has finished. It prints a one element JSON array — `[{"chance" (0-100), "input_tokens", "output_tokens", "seconds", "violations": [...]}]` — or `[]` when nothing was reviewable.

Build the report from that result:

- When the array is `[]` or its `violations` is empty: `Brian will most likely merge this PR.`

- Otherwise lead with `There is a/an <chance>% chance that Brian will reject this PR.` (the article is "an" before 8, 11, 18, and 80-89 and "a" otherwise), then a metadata line and one `- ` line per entry in `violations`:

	```
	There is a 60% chance that Brian will reject this PR.

	(116k/35k tokens, 580s):
	- <violation>
	```

	The metadata line is `(<input>/<output> tokens, <seconds>s)`, with token counts abbreviated to the nearest `<n>k` (115843 becomes 116k).

The violations gate the run, but the review is automated and is sometimes wrong, so the developer may override a finding they judge a false positive. Report **PASS** when the report is the merge line, or once every violation is fixed or overridden; otherwise **FAIL**.

## Notes

Run **last** so the diff reflects the latest version of the code. This validation is read only and does not autocommit.

## Time Estimate

~10-20 min, depending on diff size.
---

allowed-tools: [Agent, Bash, Glob, Grep, Read]
argument-hint: "[the decision to review]"
description: Run an adversarial review of a decision that rests on judgment, such as what to build next or whether a safeguard is enough. One model proposes, a second model checks each claim against the code, and the review ends in a decision or an experiment that settles it. Use when the user asks for an adversarial review, or to put a plan, design or rule "through the courtroom".
name: adversarial-review

---

# Adversarial Review

Put a decision through four rounds of adversarial review before acting on it. One model proposes, a second model checks every claim against the code and data, the first model answers only the points that are matters of judgment, and the second model converges. The result is a decision, or, when the question is empirical, an experiment with a pass mark stated in advance.

The review pays off on questions that rest on assumptions: which of several builds comes first, whether a safeguard is sufficient, or whether a design holds under the cases it has not met yet. It adds little to a question of fact, which one careful read of the code answers.

## Input

`${ARGUMENTS}` is the decision to review. When it is missing or vague, ask the user for the following:

- **Constraints**: the rules the answer must respect, such as "never push to a forwarded pull request".

- **Decision**: the question, stated so that an answer can be wrong.

- **Evidence**: the files, tickets, pull requests, logs or data the review should read.

## Roles

Use two different models, so that the verifier does not share the proposer's blind spots. When only one model is available, run both roles on it with separate, self contained briefs.

| Round | Role | Played By |
| --- | --- | --- |
| 1 | Posit | Proposer |
| 2 | Verify | Verifier |
| 3 | Counter | Proposer |
| 4 | Converge | Verifier |

Each round is a fresh agent. Agents do not see this conversation, so every brief must carry the question, the constraints, the evidence paths and the output of the previous rounds in full.

## Rounds

1. **Posit.** The proposer answers the decision with a ranked list of proposals. For each proposal, it gives the claim, the evidence for it and the assumptions it depends on. Ask it to name the assumption most likely to be wrong.

1. **Verify.** The verifier checks every claim against the code and data and cites `file:line`, command output or query results. It tags each rebuttal with one of two tags:

	- `FACT-SETTLED`: the code or data decides it. The point is closed and is not argued again.

	- `JUDGMENT`: reasonable people could weigh it differently.

	The verifier also looks for proposals that are already built or that rest on a false premise. This round catches most of the value.

1. **Gate.** When round 2 has no `JUDGMENT` items, or none that the user cares about, stop here and report round 2. Rounds 3 and 4 only pay off when there is something left to weigh.

1. **Counter.** The proposer answers the `JUDGMENT` items only, and the brief lists the `FACT-SETTLED` items as closed. The proposer may concede, refine or hold its position, and it gives its reasons.

1. **Converge.** The verifier weighs the counters and reaches a verdict. Where the disagreement is empirical, the verdict is the experiment that settles it: what to run, on what data, and the pass mark, all stated before anything runs.

## Check the Verdict

Do not pass round 4 along unexamined. Before reporting, check it against round 2:

- **Consistency.** Round 4 must not rely on a claim or a number that round 2 rejected.

- **Numbers.** A pass mark must be statistically meaningful. With zero failures in `n` trials, the 95% upper bound on the failure rate is about `3/n` (the rule of three). Twenty clean samples only bound precision at about 86%, and a 99% bound needs about 300 samples.

- **Scope.** A safeguard must cover the whole failure path that the review found, not only the feature that prompted the review.

- **Writes.** No round may change code, post, push or close anything. The review is read only, and no agent in it can approve an action on behalf of the user.

## Report

Report in this order:

1. **The verdict**, in one or two sentences.

1. **What changed** from the starting position of the user, and why.

1. **The build order or the experiments**, with the rough size of each build and the pass mark of each experiment.

1. **Where you disagree with round 4**, if anywhere, and what you would do instead.

Keep the round transcripts available on request, but do not paste them into the report.

## Worked Example

`testray-triage`, the tool the release team uses to triage backport pull requests, gained a command that builds mechanical fixes (a missing import or a leftover rename) and delivers them to the developer. The starting safeguard was "deliver only when the fix is clear". The user asked the review to test that rule and invited it to replace the rule with a better one.

- **Round 2 found a hole wider than the feature.** Nothing reruns CI when a pull request receives a push, and neither `testray-triage` nor the forward script checked that the builds they read matched the current head of the pull request. Any push after a green run could therefore be forwarded on test results for a commit the pull request no longer had, whether the push came from the fix command or from the developer. One live pull request was in exactly that state. The code and three live pull requests settled these points as `FACT-SETTLED`.

- **Rounds 3 and 4 replaced the rule.** Instead of "is the fix clear?", the rule became "has this head been built, and has this kind of fix proven itself?":

	1. Never push to a pull request that was already triaged. Every fix goes out as a new pull request, which CI runs on.

	1. Forward only the head that was checked. The forward script refuses when the live head differs, even when forced.

	1. Deliver a kind of fix only after a false positive test over 200 or more backports that landed clean.

	1. Report, but never fix, changes to the kernel, to upgrade processes and registrators, and to major versions.

	1. Tell the developer about a fix only after CI has run on it.

	1. After two failed deliveries of a kind of fix, report that kind only.

- **The check caught one error in round 4.** Round 4 gated one experiment on "20 samples at 95%", a threshold that round 2 had already rejected. The report kept that experiment as information and made the 200 backport test the gate.

An earlier review of the same tool stopped a result cache before any code was written. Round 2 showed that the results of a build can still be importing when they are first read, so a cache keyed only on the build ID would have frozen incomplete results.

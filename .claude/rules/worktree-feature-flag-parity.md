---

paths:
  - ".claude/hooks/worktree-create.sh"

---

# Master Feature-Flag Parity On Worktree Create

When a new worktree should inherit the curated `feature.flag.*=true` lines from the main worktree's `portal-ext.properties`, set `LIFERAY_BRING_MASTER_FLAGS=yes` before invoking the hook. The hook then walks the main worktree's `portal-ext.properties` for every `feature.flag.*=true` line and its immediately preceding contiguous comment lines, and appends each block idempotently to the new worktree's `portal-ext.properties`. Flags already present in the target are skipped, along with the comment lines associated only with those skipped flags.

## Why Opt-In

A fresh worktree off `upstream/master` should usually behave like master, but on short-lived experiment worktrees the user sometimes wants to verify behavior with stock flags. Defaulting off keeps the hook predictable; the env var makes the choice explicit.

## Why Not Inside The Hook

A hook invoked by `claude --worktree` runs without an interactive tty, so a `read --prompt` cannot work there. The hook honors only the explicit env var. Interactive UX lives in the consumer.

## Interactive Prompt Shape For Consumers

Any session or skill that wraps the hook should default the user into the parity flow with a tri-state prompt before invoking it. The recommended shape:

```
Master has <N> feature flags enabled. Bring them into the new worktree?
  [Y]es     bring all (default, press Enter)
  [s]elect  choose a subset
  [n]o      skip
```

1. **Yes**: set `LIFERAY_BRING_MASTER_FLAGS=yes` and invoke the hook. The hook copies all flags.
1. **Select**: show a numbered list of flag blocks, accept comma-separated indices (for example `1,3,5-7`), and write only the chosen blocks into the new worktree's `portal-ext.properties` directly. Do not pass the env var in this case.
1. **No**: invoke the hook without the env var. No flags copied.

Empty input means yes; the prompt fires once per worktree-create flow.

## Idempotency Guarantee

The hook checks each flag key against the target `portal-ext.properties` and skips any flag already present. Reruns with the same env var setting are safe and produce no duplicates.

## Scope

This rule covers the env-var-driven path in `worktree-create.sh`. Other portal-ext mutations (offset-derived ports, Liferay home, OSGi console) follow the conventions in the existing `_set_*` functions and do not need consumer guidance.
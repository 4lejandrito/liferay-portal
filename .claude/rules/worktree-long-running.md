---

paths:
  - ".claude/hooks/worktree-create.sh"

---

# Consuming Long-Running Output From The Worktree-Create Hook

The worktree-create hook redirects `ant all` (on the fresh provision path) to `${WORKTREE_DIR}/.worktree-ant-all.log`. The hook itself does not stream the output back through stdout, so any caller driving the hook from a long-lived session reads the log file to observe progress.

## Why

`ant all` produces tens of thousands of `[copy]`, `[exec]`, and `[sync-dir]` lines. Streaming that volume through a session's tool result floods context for no analytical value. The hook captures the output once to disk; consumers tail and grep the log instead.

## Read Pattern

Invoke the hook with `run_in_background` so the hook itself does not block the session. The harness notifies on completion.

While the hook runs, attach a monitor to the log with a `grep` tuned for verdict and progress markers:

```bash
tail -f ${WORKTREE_DIR}/.worktree-ant-all.log | grep --line-buffered --extended-regexp '^BUILD (FAILED|SUCCESSFUL)|^\[(error|exec)\]|Exception'
```

The `--line-buffered` flag is mandatory; without it, pipe buffering delays events by minutes and defeats the purpose.

## Heartbeat

`ant all` is mostly silent between markers, so a heartbeat catches a stalled build before the completion notification arrives. Run alongside the verdict monitor:

```bash
while true
do
	if [ -s ${WORKTREE_DIR}/.worktree-ant-all.log ]
	then
		echo "heartbeat: log_size=$(stat --format=%s ${WORKTREE_DIR}/.worktree-ant-all.log)"
	fi

	sleep 60
done
```

Three heartbeats with the same size indicate the log is no longer growing. Investigate before waiting another full build cycle.

## Failure Diagnostic

When `ant all` fails, the hook exits via `_die` and the message references the log path. The caller's diagnostic is:

```bash
tail --lines=50 ${WORKTREE_DIR}/.worktree-ant-all.log
```

## Scope

This rule covers the `ant all` invocation in `worktree-create.sh`. The `ant setup-sdk` call on the reuse path currently still streams through `>&2`; its output is two orders of magnitude smaller. If it ever grows into a noise problem it gets the same treatment under a separate rule.
---

allowed-tools: [Bash, Edit, Glob, Grep, Read, Write]
argument-hint: "[new | status | list | cleanup]"
description: Manage Git worktrees for parallel Liferay development — create one with isolated ports and database, inspect a worktree's ports, list every worktree's status, or tear a worktree down. Use when the user asks to create, configure, list, status-check, or clean up a worktree.
name: worktree-setup

---

# Worktree Setup

Set up, inspect, or tear down an isolated Liferay worktree with unique ports and a unique database, so multiple branches can run side by side without colliding.

## Input

### Action

One of `new`, `status`, `list`, or `cleanup`, supplied via `${ARGUMENTS}`. Resolve in this order:

1. **User Argument** — when `${ARGUMENTS}` names an action, use it.

1. **Conversation Context** — infer the action from how the user asked.

1. **Working Directory** — when no action is supplied and `${PWD}` is inside a worktree that already has a bundle, default to reapplying the `new` flow's port configuration (steps 4–7 of **Workflow**).

1. **Fallback** — ask the user.

### Worktree Name

Required for `new`, `status`, and `cleanup`. Derived from `${PWD}` when it matches `*/liferay-portal-<name>`. Otherwise asked.

### Port Offset

Required for `new` when multiple worktrees are being configured before any of them starts. Optional otherwise — auto-detected by scanning ports starting at offset `1`. **Offset 0 is reserved for the main worktree; never use it in a secondary worktree.**

## Expected Output

### New Worktree (Action `new`)

A configured, runnable Liferay worktree at `../liferay-portal-<name>` with:

- A Git worktree on a branch named `<name>` (created off `master`, or reused when the branch already exists).

- A bundle directory under the worktree, populated by `ant all` or by full-copy reuse of the main worktree's bundle when its `.githash` matches the worktree's `HEAD`. Never symlink.

- All service ports patched to `default + offset` per the **Port Map** below.

- A MySQL database named per the **Database Naming** rule below.

- Tomcat started in JPDA mode in the background via `<tomcat>/bin/catalina.sh jpda run`.

- A summary printed to the user with all assigned ports, the database name, the Liferay URL, and the Glowroot URL.

### Worktree Status (Action `status`)

A port table for a single worktree, read from `<bundles>/.worktree-port-offset`. No modifications.

### Worktree List (Action `list`)

A summary table covering every Git worktree:

```
Worktree                                 Port     Status     Offset    Database
liferay-portal                           -        NO TOMCAT  (none)    -
liferay-portal-LPD-00000                 8081     RUNNING    1         lportal_lpd_00000
liferay-portal-LPD-12345                 8082     STOPPED    2         lportal_lpd_12345
```

Each row combines `git worktree list --porcelain` with the worktree's `<bundles>/.worktree-port-offset`. Detect running status by matching `-Dcatalina.base` from running Java processes against each worktree's `<tomcat>` directory — port scanning cannot distinguish which Tomcat owns which port.

### Cleaned-Up Worktree (Action `cleanup`)

The worktree, its database, and (optionally) its branch removed. Confirm with the user before any destructive step:

1. Stop Tomcat via `<tomcat>/bin/catalina.sh stop`.

1. Drop the database: `mysql --execute 'DROP DATABASE IF EXISTS <DB_NAME>;' --user root`.

1. Remove the worktree: `git worktree remove <absolute-path>` (resolve the absolute path from `git worktree list --porcelain`).

1. Ask whether to delete the branch; only run `git branch --delete --force <BRANCH>` when the user confirms.

## Port Map

All ports shift by the same offset `N`:

| Service | Default | Config Location |
| --- | --- | --- |
| Tomcat HTTP | 8080 | `<tomcat>/conf/server.xml` |
| Tomcat Shutdown | 8005 | `<tomcat>/conf/server.xml` |
| Tomcat AJP | 8009 | `<tomcat>/conf/server.xml` |
| HTTPS Redirect | 8443 | `<tomcat>/conf/server.xml` |
| JPDA Debug | 8000 | `<tomcat>/bin/setenv.sh` |
| OSGi Console | 11311 | `<tomcat>/webapps/ROOT/WEB-INF/classes/portal-developer.properties` |
| ES Sidecar HTTP | 9201 | `<bundles>/osgi/configs/...ElasticsearchConfiguration.config` |
| ES Transport | 9301 | `<bundles>/osgi/configs/...ElasticsearchConfiguration.config` |
| Glowroot | 4000 | `<bundles>/glowroot/admin.json` |
| Arquillian | 32763 | `<bundles>/osgi/configs/...ArquillianConnector.config` |
| DataGuard | 42763 | `<bundles>/osgi/configs/...DataGuardConnector.config` |

## Database Naming

Derive the database name from the worktree directory name, not the offset:

| Directory | Database |
| --- | --- |
| `liferay-portal-LPD-12345` | `lportal_lpd_12345` |
| `liferay-portal-hotfix` | `lportal_hotfix` |
| `liferay-portal` | `lportal` (main — never touch) |

Rule: strip the `liferay-portal-` prefix, lowercase, replace each nonalphanumeric character with `_`, collapse consecutive `_`, truncate to 56 characters, then prepend `lportal_`.

## Workflow

Only the `new` action has non-trivial control flow. The `status`, `list`, and `cleanup` actions are fully described under **Expected Output**.

### 1. Locate or Create the Worktree

When `${PWD}` matches `*/liferay-portal-<name>`, the worktree already exists — use `${PWD}` as `<WORKTREE_DIR>` and `<name>` as `<DIR_NAME>`.

Otherwise create one off `master`:

```bash
git worktree add -b <BRANCH> ../<DIR_NAME> master
```

When the branch already exists, offer the user two options: **reuse** the existing branch (`git worktree add ../<DIR_NAME> <BRANCH>`) or pick a **new branch name** (suggest `-v2`, `-wt`, or a short descriptor).

### 2. Configure the Bundle Directory

Create `app.server.<username>.properties` in the worktree root:

```properties
app.server.parent.dir=${project.dir}/bundle
```

### 3. Build or Reuse the Bundle

Check whether a bundle already exists by looking for `<tomcat>` inside `<bundles>`.

- **Bundle exists** — skip `ant all` and inform the user the existing bundle will be reused. When the user explicitly asks to rebuild, run `ant all`.

- **No bundle** — try to reuse the main worktree's bundle. Locate the `liferay-portal` worktree (no suffix) via `git worktree list --porcelain`, resolve its `<bundles>`, and read `.githash`. When that hash equals `git rev-parse HEAD` in the current worktree, or when the user explicitly asks to copy or reuse the main bundle, copy it fully (never symlink) to `<WORKTREE_DIR>/bundle` and skip `ant all`. Otherwise run `ant setup-profile-dxp && ant all`.

When `ant all` fails, surface the error and stop — do not continue to port configuration.

### 4. Determine the Offset

Resolve in this order:

1. **User-Specified** — use it (reject 0).

1. **Saved** — read `.worktree-port-offset` from the bundle dir when present.

1. **Auto-Detected** — scan offsets starting at `1`; for each candidate `N`, test that ports `8080+N`, `8005+N`, `8000+N`, `11311+N`, `9201+N`, `9301+N`, and `4000+N` are all free with `nc -z localhost <port>`. The first offset where all are free wins.

Save the chosen offset to `<bundles>/.worktree-port-offset`.

### 5. Patch Configuration Files

All file patches must be **idempotent**. Detect the `sed -i` flavor once before any in-place edit — BSD `sed` (macOS) requires an empty-string argument after `-i`, while GNU `sed` (Linux) does not:

```bash
if [[ "$(uname)" == "Darwin" ]]; then
	SED_INPLACE=(sed -i '')
else
	SED_INPLACE=(sed -i)
fi
```

Use `"${SED_INPLACE[@]}"` for every in-place edit.

| File | Survives Rebuild? | Action |
| --- | --- | --- |
| `<tomcat>/conf/server.xml` | Yes | Read the current `protocol="HTTP/1.1"` Connector port to determine the current offset, then rewrite the Shutdown, HTTP, AJP, HTTPS, and `redirectPort` attributes in a single `sed` invocation. Skip when the target HTTP port is already present. |
| `<tomcat>/bin/setenv.sh` | **No** — wiped on rebuild, always reapply | Replace `JPDA_ADDRESS` with `8000+N`. |
| `<tomcat>/webapps/ROOT/WEB-INF/classes/portal-developer.properties` | **No** — wiped on rebuild, always reapply | Set `module.framework.properties.osgi.console=11311+N`. |
| `<bundles>/osgi/configs/com.liferay.portal.search.elasticsearch<V>.configuration.ElasticsearchConfiguration.config` | **No** — wiped on rebuild, always overwrite | Detect Elasticsearch version (`elasticsearch7` or `elasticsearch8`; default `elasticsearch8` when neither config is present). Write `sidecarHttpPort="9201+N"`, `transportTcpPort="9301+N"`, `networkBindHost="127.0.0.1"`, `networkPublishHost="127.0.0.1"`. |
| `<bundles>/osgi/configs/com.liferay.arquillian.extension.junit.bridge.connector.ArquillianConnector.config` | **No** | Write `port="32763+N"`. |
| `<bundles>/osgi/configs/com.liferay.data.guard.connector.DataGuardConnector.config` | **No** | Write `port="42763+N"`. |
| `<bundles>/glowroot/admin.json` | Yes | Use `jq` to set `.web.port` to `4000+N`. Skip when already correct. |
| `<bundles>/portal-ext.properties` | Yes | Ensure these properties are present (remove any existing values first, then append): `include-and-override=portal-developer.properties`, `portal.instance.inet.socket.address=localhost:8080+N`, `browser.launcher.url=`, `setup.wizard.enabled=false`, `terms.of.use.required=false`, `passwords.default.policy.change.required=false`, `users.reminder.queries.enabled=false`. **Important:** the property is `portal.instance.inet.socket.address` (with `inet`), not `portal.instance.http.socket.address`; remove any old `portal.instance.http.socket.address` lines. |

### 6. Configure the MySQL Database

Derive the database name per **Database Naming**.

Ensure these lines in `portal-ext.properties` (read existing username/password before replacing them when JDBC properties already exist):

```properties
jdbc.default.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.default.url=jdbc:mysql://localhost/<DB_NAME>?characterEncoding=UTF-8&dontTrackOpenResources=true&holdResultsOpenOverStatementClose=true&serverTimezone=GMT&useFastDateParsing=false&useUnicode=true
jdbc.default.username=root
jdbc.default.password=
```

Always attempt to create the database (idempotent):

```bash
mysql --execute 'CREATE DATABASE IF NOT EXISTS <DB_NAME> CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;' --user root
```

When `mysql` is unavailable or fails, print the command for the user to run manually. Show errors — never swallow them with `2>/dev/null`.

### 7. Print the Summary and Start

Print the summary described under **New Worktree** above, then start Tomcat in JPDA mode in the background. `jpda run` is foreground, so background it so the call does not block. Tell the user how to follow the log:

```bash
tail -f <tomcat>/logs/catalina.out
```

## Multiple Worktrees at Once

When configuring N worktrees before any of them starts, use explicit offsets (1, 2, 3, …) — the port scanner only detects running services and would otherwise assign every worktree the same offset.
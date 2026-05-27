# Poshi Local Run

Use this procedure to run a `Poshi` test locally against the current checkout's
Tomcat, including a `claude --worktree` checkout whose Tomcat is **not** on the
default port `8080`. Running a Poshi test against a non-default port without the
overrides below silently resolves data from whichever bundle owns `8080` and
aborts the setup.

## Resolve the Port

`<port>` is the `port` attribute on the `<Connector>` element with
`protocol="HTTP/1.1"` in `<tomcat>/conf/server.xml`, where `<tomcat>` is the
`tomcat-*` directory under `<bundles>` (the highest version when several exist).
Resolve it once and reuse it everywhere below.

```bash
grep -m1 'protocol="HTTP/1.1"' "<tomcat>/conf/server.xml" |
	grep -o 'port="[0-9]*"'
```

When the port is `8080`, the overrides are harmless no-ops, so this procedure is
safe to apply unconditionally.

## The Problem

The Poshi runner JAR (`com.liferay.poshi.runner-*.jar`) ships an internal
`poshi.properties` with `portal.url=http://localhost:8080`. The macros read URLs
from three different properties depending on the call site:

- `JSONCompany.getPortalURL()` reads `instance.url`, falling back to
  `portal.url`. Used for JSON-WS calls in most macros.

- `JSONCompany.getDefaultPortalURL()` reads `default.portal.url`. Used inside
  `getCompanyIdNoSelenium`, which resolves the `companyId` from a virtual host
  lookup.

- The `run-selenium-test` Ant target uses `test.url` for the URL Chrome
  navigates to.

Passing only `-Dtest.url=http://localhost:<port>` makes the browser hit the
right port, but the JSON-WS curl calls in macros still go to `localhost:8080`
because they read `portal.url` and `default.portal.url`. When another worktree
is running on `8080`, Poshi resolves the `companyId` from that other bundle and
then sends it as a path parameter to the target bundle. The mismatched
`companyId` causes `getUserByEmailAddress` to return `NoSuchUserException`, and
the setUp aborts with:

```
java.lang.Exception: TEST_SETUP_ERROR: No results for path: $['userId']
```

## The Solution

Run from the checkout root, overriding all four URL properties plus the
instance flag:

```bash
cd <repo-root>
HOSTNAME=localhost ANT_OPTS="-Xmx2560m" ant \
	-buildfile build-test.xml \
	-Ddefault.portal.url=http://localhost:<port> \
	-Dinstance.url=http://localhost:<port> \
	-Dportal.url=http://localhost:<port> \
	-Dtest.class="<name>" \
	-Dtest.portal.instance=false \
	-Dtest.url=http://localhost:<port> \
	run-selenium-test
```

`<name>` is the Poshi test name exactly as returned by the Testray fetch (for
example `LocalFile.StagingUsecase#AssertAssetPriorityNotBeResetAfterPublication`).

`-D<property>=<value>` injects the value as a JVM system property, and
`PropsUtil.get(...)` inside the macros reads system properties with priority
over the packaged `poshi.properties`.

The `test.portal.instance=false` flag tells Poshi not to create a virtual
instance for this test. The virtual-instance setup performs its own JSON-WS
calls that would also need the URL overrides; disabling it removes that path.

## Verify

When in doubt, grep the Poshi log for the curl invocations Poshi printed:

```bash
grep "Executing commands.*curl" <poshi-log>
```

Every URL should point at the same host and port. Any line that targets
`localhost:8080` while the rest target the worktree port indicates a missing
override.
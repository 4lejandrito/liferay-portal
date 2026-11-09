# 612: Restore Every Thread Local a Test Sets

A test that changes a thread local restores it before the test ends, even when an assertion fails. Run code as another user inside `try (ContextUserReplace contextUserReplace = new ContextUserReplace(user))` rather than calling `PermissionThreadLocal.setPermissionChecker` and `PrincipalThreadLocal.setName` directly, and follow every `ServiceContextThreadLocal.pushServiceContext` with a `try` block whose `finally` calls `ServiceContextThreadLocal.popServiceContext`.

**Rationale:** Integration tests share one thread across the test methods of a class and across classes in the same run. A permission checker, principal, or service context left behind makes every later test run as the wrong user or in the wrong group, so a failure shows up in an unrelated test and depends on the order the tests run in.

A violation is a call in a test that sets or pushes a thread local, such as `PermissionThreadLocal.setPermissionChecker`, `PrincipalThreadLocal.setName`, `CompanyThreadLocal.setCompanyId`, or `ServiceContextThreadLocal.pushServiceContext`, without a matching restore in a `finally` block, a `try` with resources, or an `@After` method. Do not flag a thread local that a test rule or an `@After` method already restores.

**Example:** https://github.com/liferay-content-management/liferay-portal/pull/9525 (commit b2a888aeb) replaced bare permission checker and principal calls with `ContextUserReplace`, and https://github.com/liferay-content-management/liferay-portal/pull/9531 (commit 333e14bdd) popped the service context a test pushed.

```diff
 ServiceContextThreadLocal.pushServiceContext(serviceContext);
 
-_fooLocalService.deleteFoo(fooId);
+try {
+	_fooLocalService.deleteFoo(fooId);
+}
+finally {
+	ServiceContextThreadLocal.popServiceContext();
+}
```
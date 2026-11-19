# 611: Assert the Expected Value, Not an Excluded One

In a test, assert the exact outcome the code should produce rather than one outcome it should not. Write `Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus())`, not `Assert.assertNotEquals(HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus())`.

**Rationale:** `assertNotEquals` against the success value passes on every other outcome, including a server error, a missing resource, or an exception turned into a status code, so a test meant to prove access is denied also passes when the request simply breaks. Naming the expected value keeps the test failing until the code produces exactly that outcome.

A violation is an `assertNotEquals`, or an `assertFalse` over an equality check, that compares a status code, result code, or other value from a small known set against the one value it should not be, when the test can state the value it should be. Do not flag an `assertNotEquals` whose point is that two values differ, such as a regenerated key against the old one, where no single expected value exists.

**Example:** https://github.com/liferay-content-management/liferay-portal/pull/9525 (commit b2a888aeb) replaced the check that a denied request was not `SC_OK` with a check that it was `SC_UNAUTHORIZED`.

```diff
-Assert.assertNotEquals(
-	HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
+Assert.assertEquals(
+	HttpServletResponse.SC_UNAUTHORIZED,
+	mockHttpServletResponse.getStatus());
```
# 610: Set Up the Opposite Before Asserting an Absence

When a test asserts that something is missing or denied, first set up the state in which it would be present, so that only the behavior under test can take it away. Grant Guest `DOWNLOAD` and assert it is granted before asserting that a bulk update removed it; grant a site member `VIEW` on a document type before asserting that the member cannot open its edit page for lack of `UPDATE`.

**Rationale:** An assertion of absence passes whenever the thing was never there. Without the opposite state set up first, a test that checks a permission was removed also passes when the permission was never granted, and a test that checks access is denied for lack of one permission also passes when the user lacks a different one, so the test proves nothing about the code it names.

A violation is an assertion that a permission, entry, value, or access is absent or denied, in a test that never establishes, through setup or an earlier assertion, that it would otherwise be present or allowed for the same subject. A change that removes a grant or a positive assertion from a test is a violation when the test still asserts, anywhere later in the method, that the same thing is absent, so read past the changed lines for that assertion. Do not flag an absence that the test itself creates the opposite of, or an assertion on a freshly created subject whose default is what the test checks.

**Example:** https://github.com/liferay-content-management/liferay-portal/pull/9526 (commit f746d20b4) granted Guest `DOWNLOAD` and asserted it before the test checked that the update removed it.

```diff
 _updatePermissions(
 	HashMapBuilder.put(
+		roleId + "_ACTION_FOO", "true"
+	).put(
 		roleId + "_ACTION_VIEW", "true"
 	).build());
 
+_assertHasPermission(ActionKeys.FOO, true, roleId);
+
 _removePermissions(roleId);
 
 _assertHasPermission(ActionKeys.FOO, false, roleId);
```
# 609: Write One Call Per Test Case Instead of a Loop

In a test, write each case as its own statement rather than looping over a literal array, a literal list, or a small fixed count. Call `_removeViewPermission(..., RoleConstants.GUEST)` and then `_removeViewPermission(..., RoleConstants.SITE_MEMBER)`, not a `for` over `new String[] {RoleConstants.GUEST, RoleConstants.SITE_MEMBER}`, and call `updateArticle` twice rather than inside `for (int i = 0; i < 2; i++)`. When each case needs several lines, writing the block out once per case is fine, and so is a private helper called once per case, as rule 601 describes.

**Rationale:** A failing assertion inside a loop reports the line of the loop body, not the case that failed, so the reader has to work out which iteration broke. Written out, each case has its own line in the stack trace, reads top to bottom without simulating the loop, and can take its own input, such as a keyword at the start of one description and at the end of the next, which a loop over identical iterations never tests.

A violation is a loop in a test whose iterations are separate cases or setup steps over a literal array, a literal list, or a fixed count small enough to write out. Do not flag a loop over values the code under test returns, such as asserting each entry of a returned list, or a loop that creates bulk data whose count is itself what the test checks, such as enough entries to fill two pages. Do not flag cases already written out, whether as repeated blocks or as helper calls.

**Example:** https://github.com/liferay-content-management/liferay-portal/pull/9524 (commit 0657e21a0) unrolled four loops over roles and file names into one call per value.

```diff
-for (String name : new String[] {NAME_A, NAME_B}) {
-	_removeFoo(bar, name);
-}
+_removeFoo(bar, NAME_A);
+_removeFoo(bar, NAME_B);
```
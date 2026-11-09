# 613: Add a Case to the Existing Test of a Method

When a change adds coverage for a method that the test class already tests, add the case to that existing test method instead of adding a new `@Test` method beside it. A short case goes in as its own section introduced by a comment line, and a case that needs its own setup goes in a private `_test<Method><Qualifier>` helper called from the existing method, as rule 605 asks of a generated resource test. Add the change's ticket to the existing method's `@TestInfo` rather than leaving it on a separate method.

**Rationale:** One test method per method under test keeps the class a map of its subject: a reader finds every case of `processAction` in `testProcessAction`, and the setup the cases share is written once. Separate methods added beside it split that coverage by the order changes arrived in rather than by the method, and each repeats the setup of the one before it. Rule 601 leaves the methods a class already has to taste; this rule is about the method a change adds.

A violation is a `@Test` method that a change adds to a class that already has a test method for the same method under test, usually named `test<Method>` plus a qualifier, such as a new `testFooWithBar` beside an existing `testFoo`. Do not flag a new test method for a method the class does not test yet, a new test class, a method the change only renames or moves, or test methods the change does not add.

**Example:** https://github.com/liferay-content-management/liferay-portal/pull/9536 (commit 00b80ca70) folded a new `testProcessActionWithDisplayPageTemplate` into `testProcessAction` and a new `testProcessActionWithModifiedDataDefinitionKey` into a helper that `testProcessAction` calls, and pull 9537 (commits 98334d1dc and 487a1ce19) did the same for two more pairs.

```diff
 @Test
-@TestInfo("LPD-1")
+@TestInfo({"LPD-1", "LPD-2"})
 public void testFoo() throws Exception {
 	_assertFoo(bar);
-}
 
-@Test
-@TestInfo("LPD-2")
-public void testFooWithBaz() throws Exception {
+	// Baz
+
 	_assertFoo(baz);
 }
```
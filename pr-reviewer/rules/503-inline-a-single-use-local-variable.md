# 503: Inline a Single Use Local Variable

A local that is assigned once and then handed, unchanged, to a single call site adds a name without adding meaning; pass the expression directly. The source formatter's `VariableDeclarationAsUsedCheck` already reports the plain case, where the local is assigned from a `get<Name>()` accessor named after it, from a no argument constructor without type arguments, or from a multiline builder chain ending in `build`, `create*`, `map`, or `put`, and is then passed whole as an argument (or into a `set<Name>(local)` call) on the next statement. This rule covers the three shapes that check skips: a value built by an anonymous class, which the check leaves alone whenever the `new` carries a class body; a value computed by an ordinary method call and handed to a builder's chained method as its argument, where neither the call name nor the use site matches the check's accessor and setter patterns; and a local assigned a default and then patched by an `if` (`int count = 1; if (condition) { count = 0; }`) whose only use is a single argument, which becomes an inline ternary at the use site (`Mockito.times(condition ? 0 : 1)`), since the check never looks past a reassignment.

**Rationale:** A name that is used once, on the very next line, forces the reader to hop from the declaration to the use and back to learn that nothing happened in between. Passing the expression at the use site removes the hop, shortens the method, and keeps the value beside the call that consumes it. The formatter takes the mechanical cases; the anonymous class and the builder argument need a reviewer, because the check cannot tell whether the intermediate name was carrying meaning.

A violation is a local assigned from an anonymous class, or from a method call whose only use is a single argument to a chained builder method, with no other reference before or after and no later reassignment; or a local assigned a default and patched by a single `if` with no `else` that sets it to one other value, whose only use is a single argument after that `if`. Do not flag a local the formatter already reports, a local used twice or more, one referenced inside a Mockito stub (the check skips those, and so does this rule), a local whose name documents a value the expression does not (`Foo defaultFoo = createFoo(null)`), an anonymous class with a body long enough that inlining it would bury the call's other arguments, a patched default that is used twice or more, or one whose `if` assigns more than one value or holds other statements, so that the ternary would nest or drop work. This is not the needless ternary that `pr-reviewer/STYLE.md` says to unwrap (`condition ? true : false`): the ternary chooses between two real values. Nor does it touch rule 206, which orders conditional field assignments in a constructor; a local that only feeds one argument has no slot to keep. The plain case of an ordinary call result passed to an ordinary call (`Foo foo = makeFoo(arg); bar.consume(foo);`) is outside both this rule and the check, which reports it only when the call is a `get<Name>()` accessor or the use is a `set<Name>(local)` call; the reviewer judges it under the simplify guidance in `pr-reviewer/STYLE.md`.

**Example:** a `FooSchema fooSchema = computeSchema(args, root);` declared only to feed `.inputSchema(fooSchema)` in a builder chain became `.inputSchema(computeSchema(args, root))`, and a `FooDelegate fooDelegate = new FooDelegate() {};` that existed only to be passed to `method.invoke` became `method.invoke(new FooDelegate() {})`. Commit `d1292ab` (`BlogsEntryCTEventListenerTest`) folded an `int wantedNumberOfInvocations = 1;` that an `if` reset to `0` into `Mockito.times(content.equals(expectedContent) ? 0 : 1)`.

```diff
-FooSchema fooSchema = computeSchema(args, root);
-
 return Bar.builder(
 ).name(
 	"x"
 ).inputSchema(
-	fooSchema
+	computeSchema(args, root)
 ).build();
```

```diff
-FooDelegate fooDelegate = new FooDelegate() {};
-
-method.invoke(fooDelegate);
+method.invoke(new FooDelegate() {});
```

```diff
-int count = 1;
-
-if (foo.equals(bar)) {
-	count = 0;
-}
-
 Mockito.verify(
-	fooService, Mockito.times(count)
+	fooService, Mockito.times(foo.equals(bar) ? 0 : 1)
 ).updateFoo(
 	foo
 );
```
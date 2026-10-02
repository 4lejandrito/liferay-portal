# Answer key

Do not give this file to the agent under evaluation. It is the scoring sheet.

Thirty-five seeded violations and eight traps, spread over the ten files in
`review/`. Every line number is a line of the file it names, counted from 1.

The first three files carry the original thirteen, S1 to S10 plus H1 to H3, and
the four original traps. Score that subset on its own as well as the full set:
the thirty runs of the first batch-size matrix were scored against those thirteen
alone, and the subset is what makes a later run comparable to them.

S1 to S10 are ordinary findings. H1 to H3 are the hard ones, modelled on a real
defect that cost two closed pull requests: a repeated entry whose first argument
is identical in every row, so the ordering has to be judged on the second
argument, and where the short name sorts before the longer names that share its
prefix.

## Files one to three: seeded violations, all ten must be reported

| # | File | Line | Rule | What is wrong | Reference in `merged/` |
| --- | --- | --- | --- | --- | --- |
| S1 | `review/FooSetResourceTest.java.txt` | 23 | 3 | `"gamma"` is asserted before `"beta"` | `BarSetResourceTest.java.txt:21-23` asserts alpha, beta, gamma |
| S2 | `review/FooSetResourceTest.java.txt` | 33 | 23 | A blank line splits two parallel assertions on the same `data` map | - |
| S3 | `review/FooSetResourceTest.java.txt` | 41 | 45 | `setFieldValue` configures the mock before `themeDisplay` and `group` are declared; all declarations come first, configuration after | - |
| S4 | `review/FooSetResourceTest.java.txt` | 59 | 32 | The block mixes forms: a string literal, then three class literals, then another string literal | `BarSetResourceTest.java.txt:28-36` groups class literals first, sorted, then the string literals |
| S5 | `review/FooSetResourceTest.java.txt` | 84 | 14 | `group` and `fooCollection` are declared above the `try` although only its body uses them | `BarSetResourceTest.java.txt:44-45` declares both inside the `try` |
| S6 | `review/FooDisplayContext.java.txt` | 36 | 4 | `remoteLiveGroup` is declared before `localLiveGroup` | `BarDisplayContext.java.txt:36-37` declares them alphabetically |
| S7 | `review/FooDisplayContext.java.txt` | 46 | 12 | `title` is a single-use local handed straight to the next `return` | `BarDisplayContext.java.txt:45-47` returns the call directly |
| S8 | `review/FooDisplayContext.java.txt` | 57 | 40 | `_defaultTitle` is a noun, not a verb; it should be `_getDefaultTitle` | - |
| S9 | `review/FooDisplayContext.java.txt` | 83 | 20 | `300000L` fills a slot already typed `long` | `BarDisplayContext.java.txt:73` writes `300000` |
| S10 | `review/FooDisplayContext.java.txt` | 85 | 32 | `_FOO_PREFIX` sits after `_TIMEOUT`, splitting the sorted `String` group with a `long` | `BarDisplayContext.java.txt:69-73` keeps the two Strings together, the long last |

S5 may be reported as one finding or as two, one per variable. Either counts
once.

## The hard three, all must be reported

`review/FooDepotRolePermissionsContributor.java.txt` lists twelve permission
entries in three role blocks of four. Inside a block the first argument is the
same role constant in every row, so the order is decided by the second argument,
and `Foo` sorts before `FooCollection` and `FooEntry` because it is a prefix of
both. The merged counterpart puts `Bar.class.getName()` first in all three of its
blocks (`merged/BarDepotRolePermissionsContributor.java.txt:28`, `:43`, `:58`).

| # | Line | Rule | What is wrong |
| --- | --- | --- | --- |
| H1 | 36 | 32 | In the `DESIGN_LIBRARY_ADMINISTRATOR` block `Foo.class.getName()` is last; it belongs before `FooCollection` and `FooEntry` |
| H2 | 47 | 32 | In the `DESIGN_LIBRARY_CONTENT_REVIEWER` block `Foo.class.getName()` sits in the middle, between `FooCollection` and `FooEntry` |
| H3 | 66 | 32 | In the `DESIGN_LIBRARY_OWNER` block `Foo.class.getName()` is last, the same as H1 |

Each block is scored on its own. H2 is deliberately not in the same position as
H1 and H3: a run that finds the defect by noticing the last entry looks wrong
picks up two of the three and misses the middle one. Rule 3 or rule 23 instead
of rule 32 still counts, as long as the fix described is moving `Foo` ahead of
the other two in that block.

## Files one to three: traps, none of these may be reported

Each is a case where a rule genuinely applies to the code in isolation, and the
already-merged counterpart writes it exactly the same way, so "Existing Code
Wins" says leave it. A run that reports one of these has not done the sibling
check.

| # | File | Line | Rule that appears to apply | Why it is not a finding |
| --- | --- | --- | --- | --- |
| T1 | `review/FooSetResourceTest.java.txt` | 73 | 20 | `BarSetResourceTest.java.txt:55` passes the identical `0L, 0L` |
| T2 | `review/FooSetResourceTest.java.txt` | 95 | 12 | `BarSetResourceTest.java.txt:65` writes the identical single-use `Group group` |
| T3 | `review/FooDisplayContext.java.txt` | 61 | 2 | `BarDisplayContext.java.txt:49-51` declares the same three parameters in the same order |
| T4 | `review/FooDisplayContext.java.txt` | 72 | 40 | `BarDisplayContext.java.txt:60` carries the same `_newCreationItem` name |

## Files four to ten: seeded violations, all twenty-two must be reported

Written 2026-09-11, when the fixture grew from three files to ten so batch sizes
of four, five and ten could be asked the same question as one, two and three.

| # | File | Line | Rule | What is wrong | Reference in `merged/` |
| --- | --- | --- | --- | --- | --- |
| S11 | `review/FooBreadcrumbEntryContributorImpl.java.txt` | 41 | 2 | `_createFooCollectionBreadcrumbEntry` takes `httpServletRequest` before `fooCollection`; the call site at 37-38 passes them in that same order | `BarBreadcrumbEntryContributorImpl.java.txt:41-43` takes `barCollection` first |
| S12 | `review/FooBreadcrumbEntryContributorImpl.java.txt` | 47 | 17 | `setURL` is called before `setTitle` on the same `breadcrumbEntry` | `BarBreadcrumbEntryContributorImpl.java.txt:47-53` sets title, then URL |
| S13 | `review/FooBreadcrumbEntryContributorImpl.java.txt` | 61 | 12 | `fooCollectionId` is a single-use local handed straight to the next call | `BarBreadcrumbEntryContributorImpl.java.txt:61-62` inlines `ParamUtil.getLong` |
| S14 | `review/FooBreadcrumbEntryContributorImpl.java.txt` | 67 | 38 | `_FOO_ADMIN_PREFIX` sits before `_FOO_ADMIN_LABEL` | `BarBreadcrumbEntryContributorImpl.java.txt:65-67` sorts LABEL before PREFIX |
| S15 | `review/FooCollectionServiceImpl.java.txt` | 24 | 12 | `externalReferenceCode` is a single-use local, and it is declared above the permission check although only the return at 30 uses it | - |
| S16 | `review/FooCollectionServiceImpl.java.txt` | 39 | 23 | No blank line between the `fooCollections` declaration and the `return` that consumes it | `BarCollectionServiceImpl.java.txt:37` has one |
| S17 | `review/FooCollectionServiceImpl.java.txt` | 43 | 40 | `_visible` is an adjective, not a verb; it should be `_isVisible`, call site at 40 | `BarCollectionServiceImpl.java.txt:42` |
| S18 | `review/FooEntryModelListener.java.txt` | 26 | 34 | `onBeforeRemove` overrides the base method but carries no `@Override` | `BarEntryModelListener.java.txt:26` |
| S19 | `review/FooEntryModelListener.java.txt` | 39 | 19 | `_removeFooEntryResources` calls `deleteResource`, so the helper's verb is delete; call site at 29 | `BarEntryModelListener.java.txt:39` |
| S20 | `review/FooEntryModelListener.java.txt` | 45 | 43 | The private helper catches `PortalException` and rethrows it wrapped; its signature can declare the checked type instead | `BarEntryModelListener.java.txt:39-40` declares `throws PortalException` and wraps only at the override |
| S21 | `review/FooCollectionResourceImpl.java.txt` | 29 | 25 | `getFooCollection` returns a `List`, so the name is plural; call site at 25 | `BarCollectionResourceImpl.java.txt:29` |
| S22 | `review/FooCollectionResourceImpl.java.txt` | 35 | 30 | A positive nested condition wraps the loop body; invert it and `continue` | `BarCollectionResourceImpl.java.txt:37-39` |
| S23 | `review/FooCollectionResourceImpl.java.txt` | 42 | 37 | `ListUtil` is imported at line 3 yet written fully qualified | `BarCollectionResourceImpl.java.txt:46` |
| S24 | `review/FooEntryUpgradeProcess.java.txt` | 22 | 42 | No blank line before `resultSet`, which consumes `preparedStatement1` | `BarEntryUpgradeProcess.java.txt:22-23` |
| S25 | `review/FooEntryUpgradeProcess.java.txt` | 40 | 36 | Two consecutive literal `append` calls; combining them also drops the `StringBundler` size at 36 from 4 to 3 | `BarEntryUpgradeProcess.java.txt:41` |
| S26 | `review/FooEntryUpgradeProcess.java.txt` | 46 | 41 | `_FOO_ENTRY_TABLE_NAME` leads with the value instead of the group, scattering it from `_FOO_ENTRY_VERSION_TABLE_NAME` at 48 | `BarEntryUpgradeProcess.java.txt:46` writes `_TABLE_NAME_BAR_ENTRY` |
| S27 | `review/FooCollectionPermissionTest.java.txt` | 25 | 31 | `@BeforeClass` where `@Before` does the job, which also forces `_group` static at 61 | `BarCollectionPermissionTest.java.txt:25` |
| S28 | `review/FooCollectionPermissionTest.java.txt` | 34 | 11 | `assertNotNull` before two assertions that dereference the same value | `BarCollectionPermissionTest.java.txt:34` drops it |
| S29 | `review/FooCollectionPermissionTest.java.txt` | 46 | 13 | A narrative `StringBundler.concat` message on `assertTrue` restates the predicate | `BarCollectionPermissionTest.java.txt:45-47` |
| S30 | `review/FooEntryLocalServiceImpl.java.txt` | 42 | 33 | `currentGroupIds` disagrees with `_getInheritedGroupIds`, the expression assigned to it | `BarEntryLocalServiceImpl.java.txt:42` |
| S31 | `review/FooEntryLocalServiceImpl.java.txt` | 45 | 1 | The chain calls `groupIds`, `queryString`, `entryClassNames` | `BarEntryLocalServiceImpl.java.txt:44-52` calls `entryClassNames`, `groupIds`, `queryString` |
| S32 | `review/FooEntryLocalServiceImpl.java.txt` | 59 | 39 | `_reindexFooEntry` is package private although only this class calls it | `BarEntryLocalServiceImpl.java.txt:59` |

## Files four to ten: traps, none of these may be reported

| # | File | Line | Rule that appears to apply | Why it is not a finding |
| --- | --- | --- | --- | --- |
| T5 | `review/FooBreadcrumbEntryContributorImpl.java.txt` | 50 | 20 | `BarBreadcrumbEntryContributorImpl.java.txt:51` passes the identical `0L, 0L` |
| T6 | `review/FooEntryModelListener.java.txt` | 33 | 12 | `BarEntryModelListener.java.txt:47-50` writes the identical single-use `fooEntryIndexer` |
| T7 | `review/FooCollectionPermissionTest.java.txt` | 35 | 3 | `BarCollectionPermissionTest.java.txt:34-35` asserts name then description in the same order |
| T8 | `review/FooEntryLocalServiceImpl.java.txt` | 27 | 17 | `BarEntryLocalServiceImpl.java.txt:27-31` configures the entity in the same model-field order, and rule 17's own exception says a Service Builder entity's setter block is left as the automatic formatter produces it |

## Scoring

- Recall: seeded violations reported, out of 13, and separately the hard three
  out of 3, since those are the ones that distinguish a careful pass.
- Traps: traps reported, out of 4. Zero is the target.
- Other false positives: anything reported that is neither seeded nor a trap.
  Judge these on their merits before counting them; a run may find something
  real that this key missed, and that is a finding about the key, not the run.
  One already came back on the first scored pass and is correct: a blank line
  splitting the paired `themeDisplay` and `group` declarations at
  `review/FooSetResourceTest.java.txt:46`. Rule numbers are not scored strictly
  either: accept rule 38 or rule 32 for S10, rule 4 or rule 28 for S6, and rule
  23 or rule 32 for S4, since each pair describes the same fix.

Seed one defect per site. The S2 site originally also carried a `data.toString()`
message on the assertion, and both scored passes reported that instead of the
blank line, so the message was removed to leave the blank line as the only thing
wrong there.

## Scoring the original thirteen as a subset

S1 to S10 and H1 to H3 are the thirteen the first matrix was scored on, and T1 to
T4 its four traps. Report recall over them separately from recall over all
thirty-five, so a run on the ten-file fixture can be set beside the runs on the
three-file one without either number being reconstructed afterwards.

One change was made to `review/FooCollectionServiceImpl.java.txt` when it was
keyed: `_visible` returned `true` on a negated draft check, which no rule in the
set describes, so it was rewritten to match the merged counterpart's shape. The
name is the seeded defect there, not the branch.

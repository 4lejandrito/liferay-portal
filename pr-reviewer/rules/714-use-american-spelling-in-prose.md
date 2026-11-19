# 714: Use American Spelling in Prose

Write prose in American English spelling. This covers comments, Javadoc, Markdown, `Language.properties` values, and log, exception, and status messages: `behavior` not `behaviour`, `color` not `colour`, `labeled` not `labelled`, `canceled` not `cancelled`, `neighbor` not `neighbour`, `initialize` not `initialise`, `license` as both noun and verb, and `afterward` and `toward` rather than `afterwards` and `towards`.

**Rationale:** The portal, its APIs, and its English user interface are written in American English, so one spelling throughout keeps search results complete, keeps translators working from one source form, and stops a reader from wondering whether two spellings name two different things.

A violation is a British or Commonwealth spelling in prose added or changed by the pull request. Do not flag text quoted verbatim from another source, such as a third party error message or a cited document, an existing identifier or API name, such as a method, field, property key, or file name that already uses the British form, or a `Language_<locale>.properties` translation other than English.

**Example:** commit `f80c725` changed `behaviour` to `behavior`, `labelled` to `labeled`, `neighbour` to `neighbor`, and `afterwards` to `afterward` in skill Markdown.

```diff
-Diagnose from the behaviour instead, and bounce Tomcat afterwards.
+Diagnose from the behavior instead, and bounce Tomcat afterward.
```
# Playwright Tests

These conventions apply to any work on Playwright tests under `modules/test/playwright` — implementing new tests, modifying existing ones, or fixing broken ones.

## Layout

```
modules/test/playwright
├── fixtures        # shared fixtures (loginTest, dataApiHelpersTest, ...)
├── pages           # shared Page classes (PageEditorPage, ...)
├── utils           # shared utilities (clickAndExpectToBeVisible, waitFor*, ...)
├── helpers         # ApiHelpers
└── tests
    └── <module-group>
        └── <project>        # main, permissions, <feature-scope>, ...
            ├── *.spec.ts
            ├── fixtures      # project-specific fixtures
            ├── pages         # project-specific Page classes
            ├── env
            └── config.ts     # declares { name, testDir, dependencies }
```

A "project" is a test scope tied to an environment. `main` is the default set, and new tests go there by default. A separate project exists only when its tests need a different environment from `main` — different `portal-ext.properties`, a custom startup script, specific feature flags enabled at the bundle level, etc. That setup lives in the project's `env` folder (properties files and startup scripts that run before its tests). If the test runs fine under `main`'s environment, add it to `main`; do not create a new project. If it needs a distinct environment, either join an existing project that already provides it or create a new one with its own `env` and `config.ts`.

## Anatomy of a Spec

Tests compose fixtures with `mergeTests(...)`. They almost never use `test` from `@playwright/test` directly:

```typescript
import {mergeTests} from '@playwright/test';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {myProjectFixture} from './fixtures/myProjectFixture';

const test = mergeTests(loginTest(), dataApiHelpersTest, myProjectFixture);

test('does something', {tag: '@LPD-12345'}, async ({apiHelpers, myPage, page}) => {
	...
});
```

**Always tag the test with the LPD ticket** it covers: `{tag: '@LPD-XXXXX'}`. This is mandatory — it is how tests are linked back to Jira.

## Page Classes

Before writing flow logic in a spec, check if the relevant Page class already exposes a method for it (for example `PageEditorPage` for the page editor, the project's own Page class for project-specific UI). If a method exists, use it.

If it does not:

- **Reusable action** (likely useful in other tests) → add a method to the Page class.
- **One-off, specific to this test** → inline it in the spec.

Page classes own the locators (as public readonly properties), a `goto(...)` that navigates and waits for readiness, and high-level actions that wrap several low-level steps.

## Setup

Avoid UI navigation. Use `page.goto(url)` (or a Page method that wraps it) to land directly on the target screen, and create test data via `apiHelpers` rather than clicking through the UI. UI is reserved for the behavior under test — anything else is wasted time and a flake source.

## Cleanup

Leaving the system as you found it is mandatory.

- Anything created through `apiHelpers` is auto-cleaned by the `dataApiHelpersTest` fixture (it tracks creates and deletes them at the end of the test).
- Anything created manually — directly via UI, raw API call, or any path outside `apiHelpers` — must be deleted explicitly at the end of the test.

### `isolatedSiteTest`

`fixtures/isolatedSiteTest.ts` provisions a fresh site (random name) before the test and deletes it after — every page, asset, or entry scoped to that site is wiped with it. Merge it into the test and scope work to the exposed `site`:

```typescript
const test = mergeTests(loginTest(), isolatedSiteTest, dataApiHelpersTest);

test('does something', {tag: '@LPD-12345'}, async ({site, page}) => {
	...
});
```

**Default to `isolatedSiteTest` unless a dependency forbids it.** A test cannot use it when it relies on pre-seeded content from another site — for example the `cmsSite` or `pageManagementSite` projects, which provision shared fixtures the test reads from. Those tests must run against the site their project provides. Anything else — small UI checks, isolated flows, tests that build their own data — should run on an isolated site.

## Flaky-Proofing

Liferay's JS often loads slower than Playwright fires actions. A naked `click()` after a navigation will frequently miss the handler.

Use these utilities from `modules/test/playwright/utils`:

- `clickAndExpectToBeHidden({trigger, target})` — clicks `trigger`, retries until `target` becomes hidden.
- `clickAndExpectToBeVisible({trigger, target})` — clicks `trigger`, retries until `target` becomes visible.
- `hoverAndExpectToBeVisible(...)` — hover-triggered visibility with retry.
- `waitForAlert(page, message)` — waits for a Liferay alert.
- `waitForPageToBeLoaded`, `waitForSPAToBeLoaded` — wait for full JS readiness.

For arbitrary retry, wrap a block in `expect(async () => { ... }).toPass()` (built into Playwright, not custom). Use it when no canned helper fits.

## Locators

Keep locators simple:

- `getByLabel('Exact label')`
- `getByRole('button', {name: 'Publish'})`
- `getByText('Exact text', {exact: true})`

Pass `exact: true` whenever the text could collide with another element. Avoid long IDs, deep CSS chains, and XPath — they break under markup changes and are hard to maintain.

## Feature Flags

Tests that depend on a feature flag use the `featureFlagsTest` fixture, which enables the flag before the test and disables it after:

```typescript
const test = mergeTests(
	loginTest(),
	featureFlagsTest({'flag-name': {enabled: true}}),
	...
);
```
---

paths:
  - "modules/**/test/**/*.{js,ts,tsx}"

---

# Jest Testing

Conventions for Jest and React Testing Library tests across `*-web` and `*-impl` modules in the portal. Apply them whenever a test file is created or edited under any module.

## File Naming

Place tests in a sibling `test/` directory at the module root, mirroring the subpath under `src/main/resources/META-INF/resources/js/`. A source file at `src/main/resources/META-INF/resources/js/utils/convertRGBtoHex.ts` has its test at `test/utils/convertRGBtoHex.ts`.

The shared Jest config in `getJestConfig.js` picks up every file under `test/` via a `testMatch` pattern, except `test/stories/` and `test/__lib__/` which are excluded so they can hold fixtures, helpers, and Storybook artifacts.

Do not colocate tests next to source files, and do not use `__tests__/` directories.

Tests follow the same TypeScript-first convention as other frontend source. See the corresponding rule in `.claude/rules/frontend.md`.

## Imports

Import testing utilities from these packages:

1. `@testing-library/react` for `render`, `screen`, `cleanup`, `fireEvent`, `waitFor`, `within`, `renderHook`, and `act`.

1. `@testing-library/user-event` for simulated user input.

Do not import from `@testing-library/react-hooks`. React 18 moved `renderHook` and `act` into `@testing-library/react`; the legacy package no longer applies.

Custom matchers from `@testing-library/jest-dom` (`toBeInTheDocument`, `toBeVisible`, ...) are registered automatically by the shared setup; do not import them manually. See the shared-setup section below for the full list of what every test inherits.

## Structure

Wrap every file in a top-level `describe('<Unit>')`. Group related cases in nested `describe` blocks. Phrase each `it('<description>')` as a sentence describing the expected behavior, and mark it `async` when the body awaits user interactions or asynchronous state.

## Lifecycle

1. `@testing-library/react` auto-calls `cleanup` after every test as long as an `afterEach` exists in scope, so an explicit `afterEach(cleanup)` is not required. Tests that already include it are fine; do not add it in new code unless a specific case demands it.

1. Reset call history with `jest.clearAllMocks()` in `beforeEach`. The shared Jest config does not set `clearMocks: true`, so call histories on `Liferay.*` and your own spies persist between tests unless cleared. `fetch` is the exception: the shared `setupAfterEnv.js` already calls `fetch.mockRestore()` in an `afterEach` hook, so you do not need to reset fetch yourself.

1. Stub browser APIs and external modules with `jest.spyOn`, then call `jest.restoreAllMocks()` in `afterEach` so the spy does not leak into other suites. Note that `restoreAllMocks` only undoes `jest.spyOn` setups; for `jest.fn()` mocks, use `clearAllMocks` (history) or `resetAllMocks` (history + implementation) as needed.

## User Interaction

Prefer `userEvent` over `fireEvent`, and `await` every interaction. `userEvent` simulates the full event sequence a real user produces (focus, keydown, input, keyup, change), which catches handler bugs that `fireEvent` skips.

When a test uses fake timers (`jest.useFakeTimers()`), instantiate `userEvent` with `setup()` and pass `advanceTimers` so user interactions can advance pending timers:

```javascript
jest.useFakeTimers();

const user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});

await user.upload(input, file);

act(() => {
	jest.runAllTimers();
});

jest.useRealTimers();
```

Without `advanceTimers`, user-event delays block on the frozen clock and the test hangs or behaves inconsistently.

## Accessibility

Tests for UI components should include accessibility assertions using `checkAccessibility` from `@liferay/layout-js-components-web/test/__lib__/`. It wraps `axe-core` to assert zero violations against WCAG 2.1a, 2.1aa, and 2.2aa rulesets:

```typescript
import checkAccessibility from '@liferay/layout-js-components-web/test/__lib__/checkAccessibility';

it('has no accessibility violations', async () => {
	const {container} = render(<MyComponent {...DEFAULT_PROPS} />);

	await checkAccessibility({bestPractices: true, context: container});
});
```

The `bestPractices: true` flag enables additional checks beyond strict WCAG.

Color contrast is not reliably testable in JSDOM and is disabled by default. Cover that case in Playwright tests if relevant.

## Shared Setup

Every `*-web` module that runs `yarn test` inherits a two-file setup from `modules/frontend-sdk/node-scripts/util/jest/`. The Jest config factory in `getJestConfig.js` wires both files in:

1. `setup.js` runs once per test file before any module code (via `setupFiles`).

1. `setupAfterEnv.js` runs after the Jest test framework is installed (via `setupFilesAfterEnv`).

Tests inherit what these files provide. Do not reinstall or reassign the same globals from a test file.

### What `setup.js` Installs

1. **`global.Liferay`**: the full mocked Liferay surface from `mocks/Liferay.js`; see the surface breakdown below.

1. **`global.fetch`**: the setup calls `require('jest-fetch-mock').enableMocks()` once for the whole portal and assigns the resulting mock to `global.fetch`. Do not call `enableMocks()` again in test files or in a module's Jest config; it is already enabled portal-wide. Drive responses through the `fetch` reference:

	```javascript
	// One-shot JSON response
	fetch.mockResponseOnce(JSON.stringify({id: 1, name: 'Sample'}));

	// Multiple chained responses for sequential calls
	fetch
		.mockResponseOnce(JSON.stringify({page: 1}))
		.mockResponseOnce(JSON.stringify({page: 2}));

	// Network failure
	fetch.mockRejectOnce(new Error('Network down'));

	// Reset between cases
	beforeEach(() => {
		fetch.resetMocks();
	});

	// Assert on outgoing calls
	expect(fetch).toHaveBeenCalledWith(
		'/o/api/foo',
		expect.objectContaining({method: 'POST'})
	);
	```

	Reassigning `global.fetch = jest.fn(...)` conflicts with the shared mock and triggers the throw-on-unmocked guard in `setupAfterEnv.js`.

1. **`global.Headers`**: a polyfill from `mocks/Headers.js` exposing `set(key, value)` and `forEach(callback)`.

1. **`global.themeDisplay`**: alias to `Liferay.ThemeDisplay`.

1. **DOM polyfills**: `global.crypto` (Node `crypto`), `Image.prototype.decode` (resolved Promise), `createRange()` (SVG fragment).

1. **Runtime polyfill**: `regenerator-runtime` for async/await transpiling.

1. **CSS and SCSS imports** are routed to `mocks/empty.css` by `resolver.js` and `transformSass.js` in the same directory. Tests do not need to import or mock stylesheets.

1. **Timezone**: `globalSetup.js` sets `process.env.TZ = 'utc'` for the whole session so date assertions are stable across machines.

### What `setupAfterEnv.js` Installs

1. **`@testing-library/jest-dom` matchers**: `toBeInTheDocument`, `toBeVisible`, `toHaveAttribute`, and the rest are registered globally. Do not import this package in test files.

1. **`global.TextEncoder` and `global.TextDecoder`**: set in a `beforeAll`, sourced from Node's `util` module.

1. **`afterEach` hook for fetch**: calls `global.fetch?.mockRestore?.()` so a fetch mock from one test does not bleed into the next.

1. **`beforeEach` hook for fetch**: re-installs a spy on `global.fetch` that throws on unmocked calls. A test that hits `fetch` without setting up a response via `fetch.mockResponseOnce(...)` fails by default. This is intentional: it forces tests to be explicit about expected network traffic.

### Liferay Surface in `mocks/Liferay.js`

The mock provides a complete `Liferay.*` surface, including events (`on`, `fire`, `detach`, `once`, `after`), `Language`, `ThemeDisplay`, `Util`, `Session`, `PortletKeys`, `PropsValues`, `FeatureFlags`, `Icons`, and more. See `modules/frontend-sdk/node-scripts/util/jest/mocks/Liferay.js` for the full mock surface.

A few defaults are non-obvious and worth knowing:

1. `Liferay.Language.get(key)` returns the key itself when no entry in the stub map exists, so `Liferay.Language.get('foo-bar')` evaluates to `'foo-bar'`.

1. `Liferay.ThemeDisplay.getLanguageId()` returns `'en_US'`; `getBCP47LanguageId()` returns `'en-US'`.

1. `Liferay.Util.openToast()` returns `true`; `Liferay.Util.navigate()` is a no-op.

1. `Liferay.Session.get()` resolves to `{}`.

### Overriding the Shared Mocks

When a single case needs a different return value than the shared default (for example, `Liferay.Util.openToast` returning a value other than `true`), scope the change with `jest.spyOn` plus `mockRestore`:

```javascript
let openToastSpy;

beforeEach(() => {
	openToastSpy = jest.spyOn(Liferay.Util, 'openToast').mockReturnValue(false);
});

afterEach(() => {
	openToastSpy.mockRestore();
});
```

The following patterns shadow the shared mock and leak into every later test in the same worker.

```javascript
// ANTIPATTERN: reassigns the Liferay surface
(global as any).Liferay.Language.get = jest.fn((key) => `T(${key})`);
global.Liferay = {Language: {get: jest.fn()}};

// ANTIPATTERN: conflicts with jest-fetch-mock from setup.js
global.fetch = jest.fn().mockResolvedValue({...});

// ANTIPATTERN: duplicates stubs the shared mock already provides via Liferay
jest.mock('frontend-js-web', () => ({
	sub: jest.fn(),
	fetch: jest.fn(),
	navigate: jest.fn(),
}));
```

### Before Writing a New Mock

Check these locations in order before implementing your own:

1. **`modules/frontend-sdk/node-scripts/util/jest/mocks/`** for portal-wide stubs (`Liferay.js`, `Headers.js`, `empty.css`).

1. **Your module's `test/`** tree for project-local fixtures and helpers (e.g. `frontend-data-set-web/test/utils/loadData.ts`).

1. **`jest-fetch-mock`** for any `fetch`-driven case (use `fetch.mockResponseOnce`, never reassign `global.fetch`).
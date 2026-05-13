---

paths:
  - "modules/**/package.json"
  - "modules/**/src/main/resources/**/*.{js,jsx,scss,ts,tsx}"

---

# Frontend Conventions

Conventions for React, TypeScript, and SCSS sources inside `liferay-portal/`. Apply them whenever a frontend file is created or edited.

## File Naming

1. **TypeScript by default for new code.** Write new components as `.tsx`, and new hooks, services, and utilities as `.ts`. Do not introduce new `.js` or `.jsx` files. When you are rewriting the implementation of an exported function or component, restructuring the file (splitting, merging, moving exports), or changing the public API (signatures, new exports), suggest to the author that migrating `.js`→`.ts` (or `.jsx`→`.tsx`) would be a natural fit for the change. The decision is the author's; do not migrate unilaterally. Skip the suggestion for one-line bug fixes, comment changes, internal renames, or pure additions to an otherwise stable file.

1. **React components:** `PascalCase.tsx`. Name the entry file after the component. Do not put component logic in `index.tsx`. `index.ts` is allowed only as a barrel re-export.

1. **Hooks, services, and utilities:** `camelCase.ts` (`useSelectedItem.ts`, `commentService.ts`).

1. **Stylesheets:** `PascalCase.scss` matching the owning component (`MyPanel.tsx` pairs with `MyPanel.scss`).

## Component Folder Layout

A simple component lives as a single `ComponentName.tsx` file under `components/`. Promote it to its own `components/ComponentName/` folder when it gains private collaborators (subcomponents, reducer, types, utils, styles).

Inside a component folder:

1. `ComponentName.tsx`

1. `ComponentName.scss`

1. `SubComponent.tsx` for any subcomponent private to the parent.

1. `reducer.ts` for the state reducer.

1. `types.ts` for shared type definitions.

1. `utils.ts` for pure helpers and validation.

Promote a private subcomponent to its own folder only when a second consumer appears.

## Shared Code

At the JS root, place shared code in conventional role folders (`services/`, `utils/`, `components/`, ...) alongside the feature folders. The role folder name already conveys "shared"; the contents do not need to live under an extra `common/` directory.

Modules like `site-cms-site-initializer` carry a `common/` wrapper when they host multiple sub-projects whose role folders need to sit side by side. For a typical `*-web` module, skip the wrapper.

## State

1. Default to local `useState` as long as the state is not shared across decoupled components and does not accumulate many transitions.

1. Promote to Context + `useReducer` when two or more components that do not share a direct parent need to read or mutate the same state, or when combining several fields makes `setState` calls repetitive and error-prone. The reducer lives in `reducer.ts` and actions are typed as a discriminated union on `type`.

1. Validation lives in `utils.ts` as pure functions. The reducer dispatches the result; it does not validate inline.

## Types

### Where Types Live

Before declaring a new type, check whether it already exists:

1. **Ambient Liferay globals.** The portal-wide `Liferay` namespace is stitched together by `modules/global.d.ts`, which pulls in several `liferay.d.ts` files via triple-slash references. Authoritative core: `modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/liferay.d.ts` (~700 lines, owns `Liferay.Language`, `Liferay.ThemeDisplay`, `Liferay.Util`, `Liferay.Service`, `Liferay.Session`, `Liferay.Portlet`, `Liferay.PortletKeys`, `Liferay.Address`, `Liferay.Browser`, `Liferay.Loader`, `Liferay.Portal`). Other contributors augment the namespace for their domain:

	1. `frontend-js-bootstrap-support-web/.../liferay.d.ts` for `Liferay.CollapseProvider`, `Liferay.DropdownProvider`, `Liferay.ModalProvider`, `Liferay.PopoverProvider`, `Liferay.TooltipProvider`.

	1. `frontend-icons-web/.../liferay.d.ts` for `Liferay.Icons`.

	1. `oauth2-provider-web/.../liferay.d.ts` for `Liferay.OAuth2`, `Liferay.OAuth2Client`, `Liferay.authToken`.

	1. `modules/global.d.ts` itself for `Liferay.Event`, `Liferay.EventHandler`, `Liferay.FeatureFlags`, `Liferay.SPA`, `Liferay.IZIndex`.

	These types are available globally without imports.

1. **Utility types from `frontend-js-web`** exported from `modules/apps/frontend-js/frontend-js-web/src/main/resources/META-INF/resources/main/index.d.ts`. Provides `Region`, `EventEmitter`, `Disposable`, `PortletInit`, `dateUtils`. Import from `frontend-js-web`.

1. **REST API contracts** from `*-rest-client-js` packages (e.g. `import {ObjectDefinition} from '@liferay/object-admin-rest-client-js'`). Generated from OpenAPI; live under `/src/models/`.

1. **Cross-module shared types** from `@liferay/*` packages (`@liferay/frontend-js-react-web`, `@liferay/object-js-components-web`). Check the package `index.ts` for the export surface.

1. **Module-local types** in `types.ts` or `types.d.ts` files alongside the source they describe.

### Declaration Conventions

1. Use `type` for domain models and discriminated unions; use `interface` for component props and stable contracts that may need extension. This matches the dominant pattern across modern modules.

1. Model values with mutually exclusive variants (reducer actions, UI machine states, heterogeneous tree items, filters with operators) as discriminated unions over a literal `type` field. This enables type narrowing in `switch`/`if` and surfaces missing cases at compile time.

1. For string enum-like sets, prefer `as const` on a constant object and derive the type with `keyof typeof`:

	```typescript
	export const ASSET_STATUS = {
		APPROVED: 'approved',
		DENIED: 'denied',
		PENDING: 'pending',
	} as const;

	export type AssetStatus = (typeof ASSET_STATUS)[keyof typeof ASSET_STATUS];
	```

## Services and Data Fetching

1. Do not call `fetch` from components. Wrap every HTTP call behind a typed service function in `services/` at the JS root.

1. Route services through a single `ApiHelper` module that wraps `fetch` from `frontend-js-web`, sets default headers (`Accept-Language`, `X-Accept-All-Languages` where multilingual responses are expected), and centralizes auth handling (a 401 forces a page reload).

1. Return a discriminated union from every service call. The shape is `{data: T, error: null} | {data: null, error: string, status?: string}`. Callers branch on `error` instead of using `try`/`catch`.

1. One service module per domain (`VocabularyService.ts`, `StructureService.ts`, `getExportPreview.ts`). The module exports async functions typed by domain shape; it does not expose `fetch` or `ApiHelper` directly.

## Forms

1. Follow the form pattern already established in the module. For a new module with no forms yet, prefer a custom `useForm` hook for simple single-submit forms; reach for Formik when the form is multi-step or needs `touched`/`dirty`/`isSubmitting` lifecycle. Do not mix Formik and custom `useForm` styles inside the same module.

## Hooks

1. Reuse the hooks already shipped by Liferay's shared modules before writing your own. `frontend-js-react-web` exports generic React utilities (`useEventListener`, `useInterval`, `useIsMounted`, `usePrevious`, `useStateSafe`, `useTimeout`, ...). `frontend-js-clay-web` exports UI-oriented hooks (`useControlledState`, `useDebounce`, `useFocusManagement`, `useHover`, `useId`, `useInteractOutside`, `useMobileDevice`, ...).

## Translations

1. **UI string keys must be literal.** Use `Liferay.Language.get('key')` with a literal string. Variables, template literals, and string concatenation are forbidden because the build extracts keys into per-locale `.properties` files by grepping for literals; non-literal keys are silently skipped and never reach the runtime translated. Source Formatter's `LanguageKeysCheck` validates that every literal key exists in the properties file. See `rules/language.md` for how to add the underlying keys.

1. **Use `sub` for interpolation.** `sub(template, ...values)` from `frontend-js-web` inserts variables into a translated string through explicit placeholders: `sub(Liferay.Language.get('x-items'), count)`. Prefer this over JavaScript template literals (`` `${Liferay.Language.get('items')}: ${count}` ``); the placeholders in the underlying `.properties` key are what give translators the freedom to reorder them in languages with different word order.

1. **Backend multilingual data goes through a helper.** A value returned by the backend with translations per language (`{en_US: 'Categories', es_ES: 'Categorías'}`) is `Liferay.Language.LocalizedValue<T>`. Resolve the displayed value through a shared helper such as `getLocalizableLabel` (canonical implementation in `object-js-components-web/.../utils/string.ts`) that falls back across preferred, user, and default language. Do not read the language map directly (`value[languageId]`).

## Building UI

1. Build UI with `@clayui/*` components rather than rolling custom primitives. Clay packages and CSS foundation live at `modules/apps/frontend-js/frontend-js-clay-web/clay/`; see `clay/AGENTS.md` for deeper guidance.

1. Before opening a PR that adds or changes a user-facing interface, run `/accessibility` to audit against WCAG 2.1 / 2.2 AA.

## Styles

1. Prefer utility classes over custom CSS. A component-scoped `.scss` file is justified when utilities cannot express the rule (pseudo-elements, animations, complex grids), when the component owns a multi-element visual identity that benefits from named class hooks, or when behavior depends on state selectors (`:hover`, `:focus-within`, `[aria-expanded]`) beyond what utilities cover.

1. Two utility families coexist. **Bootstrap 4** (`mb-4`, `p-3`, `d-flex`, `text-secondary`) ships with `!important`. **Clay `c-*`** (`c-mb-4`, `c-gap-*`, `c-focus-trap`) omits `!important` by design, so a component rule like `.my-thing { margin-bottom: 1rem; }` overrides `c-mb-4` but not `mb-4`.

1. Prefer the family that leaves the property overridable:

	1. **Spacing utilities** (`mb-*`, `mt-*`, `p-*`, `mx-*`, `my-*`, ...) exist in both families. Use the `c-*` form (`c-mb-4`) so component CSS can override without `!important` battles.

	1. **Utilities only Bootstrap 4 provides** (`d-flex`, `text-center`, `text-secondary`, `font-weight-bold`, ...): use them as-is, there is no `c-*` equivalent.

	1. **Utilities only Clay provides** (`c-gap*`, `c-focus-inset`, `c-focus-trap`): use the `c-*` form.

## Dependencies

1. Declare every npm dependency in a module's `package.json` under `dependencies`, including test-only packages. Do not use `devDependencies`.

1. The narrow exceptions are generated REST client modules (`*-rest-client-js`), where TypeScript build tooling may stay under `devDependencies`, and `frontend-js-clay-web`, where Storybook addons may stay under `devDependencies`.

## Reference Component

A minimal idiomatic component pulling together the patterns above:

```tsx
import {ClayButton} from '@clayui/button';
import {sub} from 'frontend-js-web';
import React, {useReducer} from 'react';

import {getItems} from '../../services/itemService';
import {initialState, reducer} from './reducer';
import './ItemList.scss';

export function ItemList() {
	const [state, dispatch] = useReducer(reducer, initialState);

	const handleLoad = async () => {
		dispatch({type: 'load-start'});

		const {data, error} = await getItems();

		if (error) {
			dispatch({type: 'load-error', message: error});

			return;
		}

		dispatch({type: 'load-success', items: data});
	};

	return (
		<div className="c-mb-4">
			<ClayButton onClick={handleLoad}>
				{Liferay.Language.get('load-items')}
			</ClayButton>

			{state.status === 'loaded' && (
				<p className="text-secondary">
					{sub(
						Liferay.Language.get('x-items-loaded'),
						state.items.length
					)}
				</p>
			)}
		</div>
	);
}
```

Combines: Clay component import, `Liferay.Language.get` with a literal key, `sub` for interpolation, `useReducer` with discriminated-union actions, a service call returning `{data, error}`, the Clay `c-mb-4` spacing utility, and the Bootstrap `text-secondary` text utility.
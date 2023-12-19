/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test as base, mergeTests} from '@playwright/test';
import {test as apiHelpersTest} from './apiHelpers.fixture';
import {test as featureFlagsTest} from './featureFlags.fixture';

export const test = mergeTests(
	base,
	apiHelpersTest,
	featureFlagsTest
);

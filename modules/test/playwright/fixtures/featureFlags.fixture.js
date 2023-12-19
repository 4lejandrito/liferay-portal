/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

exports.test = test.extend({
	_withFeatureFlags: async ({_apiHelpers}, use) => {
		await use(async (featureFlags, code) => {
			for (const featureFlag of featureFlags) {
				await _apiHelpers.featureFlag.updateFeatureFlag(
					featureFlag,
					'true'
				);
			}
			await code();
			for (const featureFlag of featureFlags) {
				await _apiHelpers.featureFlag.updateFeatureFlag(
					featureFlag,
					'false'
				);
			}
		});
	},
});

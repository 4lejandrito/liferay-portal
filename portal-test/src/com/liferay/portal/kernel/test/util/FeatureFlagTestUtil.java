/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thiago Buarque
 * @author Carlos Correa
 */
public class FeatureFlagTestUtil {

	public static void invokeFeatureFlagListeners(
		long companyId, boolean enabled, String key) {

		try (ServiceTrackerList<FeatureFlagListener> featureFlagListeners =
				ServiceTrackerListFactory.open(
					SystemBundleUtil.getBundleContext(),
					FeatureFlagListener.class,
					StringBundler.concat(
						"(|(feature.flag.key=",
						SystemBundleUtil.escapeFilterValue(key),
						")(feature.flag.key=",
						SystemBundleUtil.escapeFilterValue(StringPool.STAR),
						"))"))) {

			for (FeatureFlagListener featureFlagListener :
					featureFlagListeners) {

				featureFlagListener.onValue(companyId, key, enabled);
			}
		}
	}

	public static SafeCloseable setFeatureFlagsWithSafeCloseable(
		boolean enabled, String... keys) {

		Map<String, String> previousValues = new HashMap<>();

		for (String key : keys) {
			String featureFlagKey = FeatureFlagConstants.getKey(key);

			String previousValue = PropsUtil.get(featureFlagKey);

			previousValues.put(key, previousValue);

			PropsUtil.set(featureFlagKey, String.valueOf(enabled));

			if (GetterUtil.getBoolean(previousValue) == enabled) {
				continue;
			}

			_invokeWildcardFeatureFlagListeners(enabled, key);
		}

		return () -> {
			for (Map.Entry<String, String> entry : previousValues.entrySet()) {
				PropsUtil.set(
					FeatureFlagConstants.getKey(entry.getKey()),
					entry.getValue());

				boolean previousEnabled = GetterUtil.getBoolean(
					entry.getValue());

				if (previousEnabled == enabled) {
					continue;
				}

				_invokeWildcardFeatureFlagListeners(
					previousEnabled, entry.getKey());
			}
		};
	}

	private static void _invokeWildcardFeatureFlagListeners(
		boolean enabled, String featureFlagKey) {

		try (ServiceTrackerList<FeatureFlagListener> featureFlagListeners =
				ServiceTrackerListFactory.open(
					SystemBundleUtil.getBundleContext(),
					FeatureFlagListener.class,
					StringBundler.concat(
						"(feature.flag.key=",
						SystemBundleUtil.escapeFilterValue(StringPool.STAR),
						")"))) {

			if (featureFlagListeners.isEmpty()) {
				return;
			}

			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> {
					for (FeatureFlagListener featureFlagListener :
							featureFlagListeners) {

						featureFlagListener.onValue(
							companyId, featureFlagKey, enabled);
					}
				});

			for (FeatureFlagListener featureFlagListener :
					featureFlagListeners) {

				featureFlagListener.onValue(
					CompanyConstants.SYSTEM, featureFlagKey, enabled);
			}
		}
	}

}
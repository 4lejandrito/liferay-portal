/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.feature.flag;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.feature.flag.FeatureFlag;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class FeatureFlagUtil {

	public static String getFeatureFlagKey(
		Class<?> resourceClass, Method method) {

		for (Class<?> currentClass = resourceClass; currentClass != null;
			 currentClass = currentClass.getSuperclass()) {

			Method currentMethod = _getDeclaredMethod(currentClass, method);

			if (currentMethod == null) {
				continue;
			}

			FeatureFlag featureFlag = currentMethod.getAnnotation(
				FeatureFlag.class);

			if (featureFlag != null) {
				return featureFlag.value();
			}
		}

		for (Class<?> currentClass = resourceClass; currentClass != null;
			 currentClass = currentClass.getSuperclass()) {

			FeatureFlag featureFlag = currentClass.getAnnotation(
				FeatureFlag.class);

			if (featureFlag != null) {
				return featureFlag.value();
			}
		}

		return null;
	}

	public static boolean isEnabled(long companyId, String featureFlagKey) {
		try {
			return FeatureFlagManagerUtil.isEnabled(companyId, featureFlagKey);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to resolve feature flag \"", featureFlagKey,
						"\" for company ", companyId),
					illegalStateException);
			}

			return false;
		}
	}

	private static Method _getDeclaredMethod(Class<?> clazz, Method method) {
		for (Method declaredMethod : clazz.getDeclaredMethods()) {
			if (Objects.equals(declaredMethod.getName(), method.getName()) &&
				Arrays.equals(
					declaredMethod.getParameterTypes(),
					method.getParameterTypes())) {

				return declaredMethod;
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagUtil.class);

}
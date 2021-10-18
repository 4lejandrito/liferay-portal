/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.release.feature.flag.web.internal.upgrade;

import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.release.feature.flag.ReleaseFeatureFlag;
import com.liferay.release.feature.flag.web.internal.upgrade.util.UpgradeReleaseFeatureFlag;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(immediate = true, service = UpgradeStepRegistrator.class)
public class ReleaseFeatureFlagUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.0", "1.0.0",
			new UpgradeReleaseFeatureFlag(
				ReleaseFeatureFlag.DISABLE_BLOGS_SUBTITLE));

		registry.register(
			"1.0.0", "1.0.1",
			new UpgradeReleaseFeatureFlag(
				ReleaseFeatureFlag.REMOVE_SEGMENTATION));
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.dto.v1_0.FeatureFlagMethodTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.FeatureFlagMethodTestEntityResource;
import com.liferay.portal.vulcan.feature.flag.FeatureFlag;
import com.liferay.portal.vulcan.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/feature-flag-method-test-entity.properties",
	scope = ServiceScope.PROTOTYPE,
	service = FeatureFlagMethodTestEntityResource.class
)
public class FeatureFlagMethodTestEntityResourceImpl
	extends BaseFeatureFlagMethodTestEntityResourceImpl {

	@FeatureFlag("FAKE-123")
	@Operation(
		extensions = @Extension(name = "feature-flag", properties = @ExtensionProperty(name = "key", value = "FAKE-123"))
	)
	@Override
	public Page<FeatureFlagMethodTestEntity>
			getFeatureFlagMethodTestEntitiesPage()
		throws Exception {

		return super.getFeatureFlagMethodTestEntitiesPage();
	}

}
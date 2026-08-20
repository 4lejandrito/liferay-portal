/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.tools.rest.builder.test.dto.v1_0.FeatureFlagClassTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.FeatureFlagClassTestEntityResource;
import com.liferay.portal.vulcan.feature.flag.FeatureFlag;
import com.liferay.portal.vulcan.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/feature-flag-class-test-entity.properties",
	scope = ServiceScope.PROTOTYPE,
	service = FeatureFlagClassTestEntityResource.class
)
@FeatureFlag("FAKE-456")
public class FeatureFlagClassTestEntityResourceImpl
	extends BaseFeatureFlagClassTestEntityResourceImpl {

	@Operation(
		extensions = @Extension(name = "feature-flag", properties = @ExtensionProperty(name = "key", value = "FAKE-456"))
	)
	@Override
	public Page<FeatureFlagClassTestEntity>
			getFeatureFlagClassTestEntitiesPage()
		throws Exception {

		return super.getFeatureFlagClassTestEntitiesPage();
	}

	@Operation(
		extensions = @Extension(name = "feature-flag", properties = @ExtensionProperty(name = "key", value = "FAKE-456"))
	)
	@Override
	public Response postFeatureFlagClassTestEntitiesPageExportBatch(
			String callbackURL, String contentType, String fieldNames)
		throws Exception {

		return super.postFeatureFlagClassTestEntitiesPageExportBatch(
			callbackURL, contentType, fieldNames);
	}

}
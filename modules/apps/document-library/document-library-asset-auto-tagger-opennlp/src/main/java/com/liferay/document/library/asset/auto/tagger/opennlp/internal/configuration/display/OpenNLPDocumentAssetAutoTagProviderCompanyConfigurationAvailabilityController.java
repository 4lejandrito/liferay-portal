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

package com.liferay.document.library.asset.auto.tagger.opennlp.internal.configuration.display;

import com.liferay.asset.auto.tagger.configuration.display.AssetAutoTagProviderConfigurationAvailabilityController;
import com.liferay.configuration.admin.display.ConfigurationAvailabilityController;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = "configuration.pid=com.liferay.document.library.asset.auto.tagger.opennlp.internal.configuration.OpenNLPDocumentAssetAutoTagProviderCompanyConfiguration",
	service = ConfigurationAvailabilityController.class
)
public class
	OpenNLPDocumentAssetAutoTagProviderCompanyConfigurationAvailabilityController
		implements ConfigurationAvailabilityController {

	@Override
	public boolean isVisible(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		return _assetAutoTagProviderConfigurationAvailabilityController.
			isVisible(scope, scopePK);
	}

	@Reference
	private AssetAutoTagProviderConfigurationAvailabilityController
		_assetAutoTagProviderConfigurationAvailabilityController;

}
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.portlet.action;

import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.constants.GCloudNaturalLanguageAssetAutoTagProviderConstants;
import com.liferay.document.library.asset.auto.tagger.google.cloud.natural.language.internal.constants.PortalSettingsGCloudNaturalLanguageAssetAutoTagProviderConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.settings.portlet.action.PortalSettingsFormContributor;
import com.liferay.portal.settings.portlet.action.PortalSettingsParameterUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.util.Optional;

/**
 * @author Alicia García
 */
@Component(immediate = true, service = PortalSettingsFormContributor.class)
public class
GCloudNaturalLanguageAssetAutoTagProviderPortalSettingsFormContributor
	implements PortalSettingsFormContributor {

	@Override
	public Optional<String> getDeleteMVCActionCommandNameOptional() {
		return Optional.empty();
	}

	@Override
	public String getParameterNamespace() {
		return PortalSettingsGCloudNaturalLanguageAssetAutoTagProviderConstants.
			FORM_PARAMETER_NAMESPACE;
	}

	@Override
	public Optional<String> getSaveMVCActionCommandNameOptional() {
		return Optional.of(
			"/portal_settings/document_library_asset_auto_tagger_google_" +
			"cloud_natural_language");
	}

	@Override
	public String getSettingsId() {
		return GCloudNaturalLanguageAssetAutoTagProviderConstants.SERVICE_NAME;
	}

	@Override
	public void validateForm(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		boolean classificationEndpointEnabled = GetterUtil.getBoolean(
			PortalSettingsParameterUtil.getBoolean(
				actionRequest, this, "classificationEndpointEnabled"));

		boolean entityEndpointEnabled = GetterUtil.getBoolean(
			PortalSettingsParameterUtil.getBoolean(
				actionRequest, this, "entityEndpointEnabled"));

		String apiKey = GetterUtil.getString(
			PortalSettingsParameterUtil.getString(
				actionRequest, this, "apiKey"));

		if ((classificationEndpointEnabled || entityEndpointEnabled) &&
			Validator.isNull(apiKey)) {
			SessionErrors.add(actionRequest, "endpointEnabledWithoutAPIKey");
		}

	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}
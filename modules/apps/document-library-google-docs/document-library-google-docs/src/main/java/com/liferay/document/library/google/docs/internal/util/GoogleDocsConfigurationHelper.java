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

package com.liferay.document.library.google.docs.internal.util;

import com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PrefsPropsUtil;

import javax.portlet.PortletPreferences;

/**
 * @author Iván Zaera
 */
public class GoogleDocsConfigurationHelper {

	public GoogleDocsConfigurationHelper(long companyId)
		throws ConfigurationException {

		_dlGoogleDriveCompanyConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				DLGoogleDriveCompanyConfiguration.class, companyId);

		_portletPreferences = PrefsPropsUtil.getPreferences(companyId);
	}

	public String getGoogleAppsAPIKey() {
		return GetterUtil.getString(
			_dlGoogleDriveCompanyConfiguration.clientSecret(),
			_portletPreferences.getValue("googleAppsAPIKey", ""));
	}

	public String getGoogleClientId() {
		return GetterUtil.getString(
			_dlGoogleDriveCompanyConfiguration.clientId(),
			_portletPreferences.getValue("googleClientId", ""));
	}

	private final DLGoogleDriveCompanyConfiguration
		_dlGoogleDriveCompanyConfiguration;
	private final PortletPreferences _portletPreferences;

}
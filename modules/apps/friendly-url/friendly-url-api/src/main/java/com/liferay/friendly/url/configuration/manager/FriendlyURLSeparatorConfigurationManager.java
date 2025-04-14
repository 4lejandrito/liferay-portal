/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.configuration.manager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

/**
 * @author Mikel Lorza
 */
public interface FriendlyURLSeparatorConfigurationManager {

	public default String getFriendlyURLSeparatorsJSON(long companyId)
		throws ConfigurationException {

		JSONObject friendlyURLSeparatorsJSONObject = null;

		try {
			friendlyURLSeparatorsJSONObject =
				getFriendlyURLSeparatorsJSONObject(companyId);
		}
		catch (PortalException portalException) {
			throw new ConfigurationException(portalException);
		}

		return friendlyURLSeparatorsJSONObject.toString();
	}

	public JSONObject getFriendlyURLSeparatorsJSONObject(long companyId)
		throws PortalException;

	public void updateFriendlyURLSeparatorCompanyConfiguration(
			long companyId, String friendlyURLSeparatorsJSON)
		throws ConfigurationException;

}
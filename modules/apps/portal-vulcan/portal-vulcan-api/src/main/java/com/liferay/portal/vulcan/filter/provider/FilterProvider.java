/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.filter.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.Locale;

/**
 * @author Carlos Correa
 */
public interface FilterProvider {

	public Filter getFilter(
			EntityModel entityModel, String filterString, Locale locale)
		throws PortalException;

}
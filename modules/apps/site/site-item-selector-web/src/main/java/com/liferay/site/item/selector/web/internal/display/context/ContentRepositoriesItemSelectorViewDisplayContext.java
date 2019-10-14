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

package com.liferay.site.item.selector.web.internal.display.context;

import com.liferay.site.item.selector.criterion.SiteItemSelectorCriterion;
import com.liferay.site.util.GroupSearchProvider;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class ContentRepositoriesItemSelectorViewDisplayContext
	extends MySitesItemSelectorViewDisplayContext {

	public ContentRepositoriesItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		SiteItemSelectorCriterion siteItemSelectorCriterion,
		String itemSelectedEventName, PortletURL portletURL,
		GroupSearchProvider groupSearchProvider) {

		super(
			httpServletRequest, siteItemSelectorCriterion,
			itemSelectedEventName, portletURL, groupSearchProvider);
	}

	@Override
	public boolean isShowChildSitesLink() {
		return false;
	}

}
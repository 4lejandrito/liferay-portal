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

package com.liferay.redirect.web.internal.servlet.filter;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PortalInstances;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.web.internal.configuration.RedirectConfiguration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = {
		"servlet-context-name=", "servlet-filter-name=Redirect Filter",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class RedirectFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled() {
		return _redirectConfiguration.isEnabled();
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		RedirectEntry redirectEntry = _getRedirectEntry(httpServletRequest);

		if (redirectEntry != null) {
			if (redirectEntry.isTemporary()) {
				httpServletResponse.sendRedirect(
					redirectEntry.getDestinationURL());
			}
			else {
				httpServletResponse.setStatus(
					HttpServletResponse.SC_MOVED_PERMANENTLY);
				httpServletResponse.setHeader(
					"Location", redirectEntry.getDestinationURL());
			}
		}
		else {
			super.processFilter(
				httpServletRequest, httpServletResponse, filterChain);
		}
	}

	private String _getPathInfo(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		int pos = requestURI.indexOf(Portal.JSESSIONID);

		String pathFriendlyURLPublic = _portal.getPathFriendlyURLPublic();
		String pathProxy = _portal.getPathProxy();

		int pathInfoOffset =
			pathFriendlyURLPublic.length() - pathProxy.length();

		if (pos == -1) {
			return requestURI.substring(pathInfoOffset);
		}

		return requestURI.substring(pathInfoOffset, pos);
	}

	private RedirectEntry _getRedirectEntry(
		HttpServletRequest httpServletRequest) {

		URLInfo urlInfo = _parseURL(httpServletRequest);

		if (urlInfo != null) {
			Group group = urlInfo.getGroup();

			return _redirectEntryLocalService.fetchRedirectEntry(
				group.getGroupId(), urlInfo.getSubpath());
		}

		return null;
	}

	private URLInfo _parseURL(HttpServletRequest httpServletRequest) {
		String path = _getPathInfo(httpServletRequest);

		if (path.length() <= 1) {
			return null;
		}

		String friendlyURL = path;
		String subpath = path;

		int pos = path.indexOf(CharPool.SLASH, 1);

		if (pos != -1) {
			friendlyURL = path.substring(0, pos);
			subpath = path.substring(pos + 1);
		}

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			PortalInstances.getCompanyId(httpServletRequest), friendlyURL);

		if (group == null) {
			return null;
		}

		return new URLInfo(group, subpath);
	}

	private static final Log _log = LogFactoryUtil.getLog(RedirectFilter.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RedirectConfiguration _redirectConfiguration;

	@Reference
	private RedirectEntryLocalService _redirectEntryLocalService;

	private class URLInfo {

		public URLInfo(Group group, String subpath) {
			_group = group;
			_subpath = subpath;
		}

		public Group getGroup() {
			return _group;
		}

		public String getSubpath() {
			return _subpath;
		}

		private final Group _group;
		private final String _subpath;

	}

}
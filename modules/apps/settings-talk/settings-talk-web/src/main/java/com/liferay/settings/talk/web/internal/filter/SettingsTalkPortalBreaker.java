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

package com.liferay.settings.talk.web.internal.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.settings.talk.web.internal.configuration.SettingsTalkConfiguration;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.settings.talk.web.internal.configuration.SettingsTalkConfiguration",
	immediate = true,
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Settings Talk Breaker Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class SettingsTalkPortalBreaker extends BasePortalFilter {

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws ServletException {

		throw new ServletException("Portal Broken");
	}

	@Override
	public boolean isFilterEnabled() {
		return _settingsTalkConfiguration.breakPortal();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_settingsTalkConfiguration = ConfigurableUtil.createConfigurable(
			SettingsTalkConfiguration.class, properties);
	}

	private volatile SettingsTalkConfiguration _settingsTalkConfiguration;

}
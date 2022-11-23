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

package com.liferay.portal.vulcan.internal.template.servlet;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.util.WebKeys;

import java.security.Principal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Alejandro Tardín
 */
public class RESTClientSyntheticHttpRequest extends HttpServletRequestWrapper {

	public RESTClientSyntheticHttpRequest(Locale locale, User user) {
		super(_getHttpServletRequest());

		_locale = locale;
		_user = user;
	}

	@Override
	public Object getAttribute(String name) {
		if (Objects.equals(name, WebKeys.USER)) {
			return _user;
		}

		if (Objects.equals(name, WebKeys.USER_ID)) {
			return _user.getUserId();
		}

		return super.getAttribute(name);
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public Enumeration getLocales() {
		return Collections.enumeration(Arrays.asList(_locale));
	}

	@Override
	public String getRemoteUser() {
		return String.valueOf(_user.getUserId());
	}

	@Override
	public Principal getUserPrincipal() {
		return new ProtectedPrincipal(getRemoteUser());
	}

	private static HttpServletRequest _getHttpServletRequest() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		Stack<ServiceContext> serviceContexts = new Stack<>();

		while (httpServletRequest == null) {
			serviceContext = ServiceContextThreadLocal.popServiceContext();

			serviceContexts.push(serviceContext);

			httpServletRequest = serviceContext.getRequest();
		}

		while (!serviceContexts.isEmpty()) {
			ServiceContextThreadLocal.pushServiceContext(serviceContexts.pop());
		}

		if (httpServletRequest == null) {
			throw new NullPointerException("HttpServletRequest is null");
		}

		return httpServletRequest;
	}

	private final Locale _locale;
	private final User _user;

}
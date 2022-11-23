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

package com.liferay.portal.vulcan.internal.template.rest.client;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.internal.template.servlet.RESTClientSyntheticHttpRequest;
import com.liferay.portal.vulcan.template.rest.client.RESTClient;
import com.liferay.portal.vulcan.template.rest.client.RESTClientFactory;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = RESTClientFactory.class)
public class RESTClientFactoryImpl implements RESTClientFactory {

	public RESTClient getRestClient(HttpServletRequest httpServletRequest) {
		return new RESTClientImpl(httpServletRequest);
	}

	public RESTClient getRestClient(Locale locale, User user) {
		return new RESTClientImpl(
			new RESTClientSyntheticHttpRequest(locale, user));
	}

}
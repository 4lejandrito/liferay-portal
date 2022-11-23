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

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.internal.template.servlet.RESTClientHttpRequest;
import com.liferay.portal.vulcan.template.rest.client.RESTClient;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class RESTClientImpl implements RESTClient {

	public RESTClientImpl(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public Object get(String path) throws Exception {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServletContext servletContext = _getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(Portal.PATH_MODULE + path);

		requestDispatcher.forward(
			new RESTClientHttpRequest(_httpServletRequest),
			new PipingServletResponse(
				new DummyHttpServletResponse(), unsyncStringWriter));

		return JSONFactoryUtil.looseDeserialize(unsyncStringWriter.toString());
	}

	private ServletContext _getServletContext() {
		if (_servletContext == null) {
			_servletContext = ServletContextPool.get(StringPool.BLANK);
		}

		return _servletContext;
	}

	private final HttpServletRequest _httpServletRequest;
	private ServletContext _servletContext;

}
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

package com.liferay.headless.builder.internal.generator.application;

import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Luis Miguel Barcos
 */
public class Operation {

	public Operation(
		String method, String path, Schema response, String scope) {

		_method = _setMethod(method);
		_path = path;
		_response = response;
		_scope = _setScope(scope);
	}

	public Http.Method getMethod() {
		return _method;
	}

	public String getPath() {
		return _path;
	}

	public Schema getResponse() {
		return _response;
	}

	public Scope getScope() {
		return _scope;
	}

	public enum Scope {

		COMPANY, SITE

	}

	private Http.Method _setMethod(String method) {
		if (StringUtil.toUpperCase(
				method
			).equals(
				"GET"
			)) {

			return Http.Method.GET;
		}

		throw new UnsupportedOperationException("Method not supported");
	}

	private Scope _setScope(String scope) {
		String lowerCase = StringUtil.toLowerCase(scope);

		if (lowerCase.equals("company")) {
			return Scope.COMPANY;
		}
		else if (lowerCase.equals("site")) {
			return Scope.SITE;
		}

		throw new UnsupportedOperationException("Scope not supported");
	}

	private final Http.Method _method;
	private final String _path;
	private final Schema _response;
	private final Scope _scope;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alejandro Tardín
 */
public class MCPServerHttpServletRequestWrapper
	extends PersistentHttpServletRequestWrapper {

	public MCPServerHttpServletRequestWrapper(
		HttpServletRequest httpServletRequest, String method, String pathInfo,
		String contentType, byte[] body) {

		super(httpServletRequest);

		_method = method;
		_pathInfo = pathInfo;
		_contentType = contentType;
		_body = (body != null) ? body : _EMPTY_BODY;

		int index = pathInfo.indexOf('?');

		_queryString = (index > -1) ? pathInfo.substring(index + 1) : null;

		_parameterMap = HttpComponentsUtil.getParameterMap(_queryString);

		HashMapBuilder.HashMapWrapper<String, String> overrideHeadersBuilder =
			HashMapBuilder.put(
				HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);

		if (contentType != null) {
			overrideHeadersBuilder.put(HttpHeaders.CONTENT_TYPE, contentType);
			overrideHeadersBuilder.put(
				HttpHeaders.CONTENT_LENGTH, String.valueOf(_body.length));
		}

		_overrideHeaders = overrideHeadersBuilder.build();
	}

	@Override
	public Object getAttribute(String name) {
		if (_attributes.containsKey(name)) {
			return _attributes.get(name);
		}

		if (name.startsWith("jakarta.servlet.include.") ||
			_TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER.equals(name)) {

			return null;
		}

		return super.getAttribute(name);
	}

	@Override
	public int getContentLength() {
		if (_contentType == null) {
			return 0;
		}

		return _body.length;
	}

	@Override
	public long getContentLengthLong() {
		return getContentLength();
	}

	@Override
	public String getContentType() {
		return _contentType;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.FORWARD;
	}

	@Override
	public String getHeader(String name) {
		String override = _findOverride(name);

		if (override != null) {
			return override;
		}

		return super.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Set<String> names = new LinkedHashSet<>(
			Collections.list(super.getHeaderNames()));

		names.addAll(_overrideHeaders.keySet());

		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		String override = _findOverride(name);

		if (override != null) {
			return Collections.enumeration(Collections.singletonList(override));
		}

		return super.getHeaders(name);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new MCPServletInputStream(new ByteArrayInputStream(_body));
	}

	@Override
	public String getMethod() {
		return _method;
	}

	@Override
	public String getParameter(String name) {
		String[] values = _parameterMap.get(name);

		return ((values != null) && (values.length > 0)) ? values[0] : null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(_parameterMap.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return _parameterMap.get(name);
	}

	@Override
	public String getPathInfo() {
		return _pathInfo;
	}

	@Override
	public String getQueryString() {
		return _queryString;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(
			new InputStreamReader(
				new ByteArrayInputStream(_body), StandardCharsets.UTF_8));
	}

	@Override
	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		_attributes.put(name, object);
	}

	private String _findOverride(String name) {
		for (Map.Entry<String, String> entry : _overrideHeaders.entrySet()) {
			if (entry.getKey(
				).equalsIgnoreCase(
					name
				)) {

				return entry.getValue();
			}
		}

		return null;
	}

	private static final byte[] _EMPTY_BODY = new byte[0];

	private static final String _TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER =
		"com.liferay.portal.vulcan.internal.constants.VulcanConstants" +
			"#TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER";

	private final Map<String, Object> _attributes = new HashMap<>();
	private final byte[] _body;
	private final String _contentType;
	private final String _method;
	private final Map<String, String> _overrideHeaders;
	private final Map<String, String[]> _parameterMap;
	private final String _pathInfo;
	private final String _queryString;

	private static class MCPServletInputStream extends ServletInputStream {

		public MCPServletInputStream(
			ByteArrayInputStream byteArrayInputStream) {

			_byteArrayInputStream = byteArrayInputStream;
		}

		@Override
		public boolean isFinished() {
			if (_byteArrayInputStream.available() == 0) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public int read() {
			return _byteArrayInputStream.read();
		}

		@Override
		public int read(byte[] bytes, int offset, int length) {
			return _byteArrayInputStream.read(bytes, offset, length);
		}

		@Override
		public void setReadListener(ReadListener readListener) {
			throw new UnsupportedOperationException();
		}

		private final ByteArrayInputStream _byteArrayInputStream;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.validation;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.portal.vulcan.graphql.validation.GraphQLRequestContext;
import com.liferay.portal.vulcan.internal.configuration.util.ConfigurationUtil;

import java.lang.reflect.Method;

import java.util.Objects;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Carlos Correa
 */
public class ServletDataRequestContext implements GraphQLRequestContext {

	public ServletDataRequestContext(
		long companyId, ConfigurationAdmin configurationAdmin, Method method,
		boolean mutation, ServletData servletData) {

		_companyId = companyId;
		_configurationAdmin = configurationAdmin;
		_servletData = servletData;

		_namespace = _getNamespace(configurationAdmin, servletData);
		_resourceClass = _getResourceClass(method, mutation, servletData);
		_resourceMethod = _getResourceMethod(method, mutation, servletData);
	}

	@Override
	public String getApplicationName() {
		return _servletData.getApplicationName();
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getNamespace() {
		return _namespace;
	}

	@Override
	public Class<?> getResourceClass() {
		return _resourceClass;
	}

	@Override
	public Method getResourceMethod() {
		return _resourceMethod;
	}

	@Override
	public boolean isJaxRsResourceInvocation() {
		return _servletData.isJaxRsResourceInvocation();
	}

	private String _getMethodName(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();

		if (declaringClass == null) {
			return method.getName();
		}

		GraphQLTypeExtension graphQLTypeExtension =
			declaringClass.getAnnotation(GraphQLTypeExtension.class);

		if (graphQLTypeExtension == null) {
			return method.getName();
		}

		Class<?> value = graphQLTypeExtension.value();

		return value.getSimpleName() + "." + method.getName();
	}

	private String _getNamespace(
		ConfigurationAdmin configurationAdmin, ServletData servletData) {

		String graphQLNamespace = ConfigurationUtil.getGraphQLNamespace(
			configurationAdmin, servletData);

		if (graphQLNamespace == null) {
			return null;
		}

		return StringUtil.upperCaseFirstLetter(graphQLNamespace);
	}

	private Class<?> _getResourceClass(
		Method method, boolean mutation, ServletData servletData) {

		if (servletData == null) {
			return null;
		}

		ObjectValuePair<Class<?>, String> resourceMethodObjectValuePair =
			servletData.getResourceMethodObjectValuePair(
				_getMethodName(method), mutation);

		if (resourceMethodObjectValuePair == null) {
			return null;
		}

		return resourceMethodObjectValuePair.getKey();
	}

	private Method _getResourceMethod(
		Method method, boolean mutation, ServletData servletData) {

		if (servletData == null) {
			return null;
		}

		ObjectValuePair<Class<?>, String> resourceMethodObjectValuePair =
			servletData.getResourceMethodObjectValuePair(
				_getMethodName(method), mutation);

		if (resourceMethodObjectValuePair == null) {
			return null;
		}

		Class<?> resourceClass = resourceMethodObjectValuePair.getKey();

		for (Method resourceMethod : resourceClass.getMethods()) {
			if (Objects.equals(
					resourceMethod.getName(),
					resourceMethodObjectValuePair.getValue())) {

				return resourceMethod;
			}
		}

		return null;
	}

	private final long _companyId;
	private final ConfigurationAdmin _configurationAdmin;
	private final String _namespace;
	private final Class<?> _resourceClass;
	private final Method _resourceMethod;
	private final ServletData _servletData;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.yaml;

import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class YAMLUtilTest {

	@Test
	public void testLoadOpenAPIYAMLWithInfoFeatureFlag() {
		OpenAPIYAML openAPIYAML = YAMLUtil.loadOpenAPIYAML(
			_getYAML("get", "    x-feature-flag: \"LPD-1234\"\n", ""));

		Info info = openAPIYAML.getInfo();

		Assert.assertEquals("LPD-1234", info.getFeatureFlag());
	}

	@Test
	public void testLoadOpenAPIYAMLWithOperationFeatureFlag() {
		for (String httpMethod : _HTTP_METHODS) {
			OpenAPIYAML openAPIYAML = YAMLUtil.loadOpenAPIYAML(
				_getYAML(
					httpMethod, "",
					"            x-feature-flag: \"LPD-5678\"\n"));

			Operation operation = _getOperation(openAPIYAML, httpMethod);

			Assert.assertEquals(
				httpMethod, "LPD-5678", operation.getFeatureFlag());
		}
	}

	@Test
	public void testLoadOpenAPIYAMLWithoutFeatureFlag() {
		for (String httpMethod : _HTTP_METHODS) {
			OpenAPIYAML openAPIYAML = YAMLUtil.loadOpenAPIYAML(
				_getYAML(httpMethod, "", ""));

			Info info = openAPIYAML.getInfo();

			Assert.assertNull(info.getFeatureFlag());

			Operation operation = _getOperation(openAPIYAML, httpMethod);

			Assert.assertNull(httpMethod, operation.getFeatureFlag());
		}
	}

	private Operation _getOperation(
		OpenAPIYAML openAPIYAML, String httpMethod) {

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		PathItem pathItem = pathItems.get("/widgets");

		if (httpMethod.equals("delete")) {
			return pathItem.getDelete();
		}
		else if (httpMethod.equals("get")) {
			return pathItem.getGet();
		}
		else if (httpMethod.equals("head")) {
			return pathItem.getHead();
		}
		else if (httpMethod.equals("options")) {
			return pathItem.getOptions();
		}
		else if (httpMethod.equals("patch")) {
			return pathItem.getPatch();
		}
		else if (httpMethod.equals("post")) {
			return pathItem.getPost();
		}

		return pathItem.getPut();
	}

	private String _getYAML(
		String httpMethod, String infoFeatureFlag,
		String operationFeatureFlag) {

		return String.format(
			"info:\n    title: \"Test\"\n    version: v1.0\n%sopenapi: " +
				"3.0.1\npaths:\n    \"/widgets\":\n        %s:\n            " +
					"operationId: getWidgetsPage\n%s",
			infoFeatureFlag, httpMethod, operationFeatureFlag);
	}

	private static final String[] _HTTP_METHODS = {
		"delete", "get", "head", "options", "patch", "post", "put"
	};

}
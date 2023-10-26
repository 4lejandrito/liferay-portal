/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.application.provider.test;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.application.provider.APIApplicationProvider;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags("LPS-178642")
public class APIApplicationProviderTest extends BaseTestCase {

	@Test
	public void test() throws Exception {
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).put(
				"baseURL", "test"
			).put(
				"externalReferenceCode", _API_APPLICATION_ERC
			).put(
				"openAPIJSON",
				JSONUtil.put(
					"components",
					JSONUtil.put(
						"schemas",
						JSONUtil.put(
							"Schema",
							JSONUtil.put(
								"description", "description"
							).put(
								"properties",
								JSONUtil.put(
									"name",
									JSONUtil.put(
										"description", "description"
									).put(
										"type", "string"
									).put(
										"x-liferay-object-field-external-" +
											"reference-code",
										"APPLICATION_STATUS"
									))
							).put(
								"type", "object"
							).put(
								"x-liferay-object-definition-external-" +
									"reference-code",
								"L_API_APPLICATION"
							)))
				).put(
					"info",
					JSONUtil.put(
						"description", "description"
					).put(
						"title", "title"
					).put(
						"version", "1.0.0"
					)
				).put(
					"openapi", "3.0.0"
				).put(
					"paths",
					JSONUtil.put(
						"/path",
						JSONUtil.put(
							"get",
							JSONUtil.put(
								"description", "description"
							).put(
								"responses",
								JSONUtil.put(
									"200",
									JSONUtil.put(
										"content",
										JSONUtil.put(
											"application/json",
											JSONUtil.put(
												"schema",
												JSONUtil.put(
													"items",
													JSONUtil.put(
														"$ref",
														"#/components/schemas" +
															"/Schema")
												).put(
													"type", "array"
												)))))
							).put(
								"x-liferay-odata-filter",
								"baseURL ne 'testName'"
							).put(
								"x-liferay-odata-sort", "baseURL:asc"
							).put(
								"x-liferay-scope", "company"
							)))
				).toString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		APIApplication apiApplication =
			_apiApplicationProvider.fetchAPIApplication(
				"test", TestPropsValues.getCompanyId());

		Assert.assertEquals("test", apiApplication.getBaseURL());

		List<APIApplication.Schema> schemas = apiApplication.getSchemas();

		Assert.assertEquals(schemas.toString(), 1, schemas.size());

		APIApplication.Schema schema = schemas.get(0);

		Assert.assertEquals("description", schema.getDescription());
		Assert.assertEquals("Schema", schema.getName());

		List<APIApplication.Endpoint> endpoints = apiApplication.getEndpoints();

		Assert.assertEquals(endpoints.toString(), 1, endpoints.size());

		APIApplication.Endpoint endpoint = endpoints.get(0);

		APIApplication.Filter filter = endpoint.getFilter();

		Assert.assertEquals(
			"baseURL ne 'testName'", filter.getODataFilterString());

		APIApplication.Sort sort = endpoint.getSort();

		Assert.assertEquals("baseURL:asc", sort.getODataSortString());

		Assert.assertEquals(Http.Method.GET, endpoint.getMethod());
		Assert.assertEquals("/path", endpoint.getPath());
		Assert.assertNull(endpoint.getRequestSchema());
		Assert.assertEquals(schema, endpoint.getResponseSchema());
		Assert.assertEquals(
			APIApplication.Endpoint.RetrieveType.COLLECTION,
			endpoint.getRetrieveType());
		Assert.assertEquals(
			APIApplication.Endpoint.Scope.COMPANY, endpoint.getScope());

		List<APIApplication.Property> properties = schema.getProperties();

		Assert.assertEquals(properties.toString(), 1, properties.size());

		APIApplication.Property property = properties.get(0);

		Assert.assertEquals("description", property.getDescription());
		Assert.assertEquals("name", property.getName());
		Assert.assertEquals(
			APIApplication.Property.Type.PICKLIST, property.getType());
	}

	private static final String _API_APPLICATION_ERC =
		RandomTestUtil.randomString();

	@Inject
	private APIApplicationProvider _apiApplicationProvider;

}
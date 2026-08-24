/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.openapi.contributor;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class FilterableFieldsOpenAPIContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContributeWhenSchemaNameIsNull() throws Exception {
		Schema<?> schema = new Schema<>();

		OpenAPI openAPI = new OpenAPI();

		openAPI.setComponents(
			new Components() {
				{
					addSchemas("TestEntity", schema);
				}
			});

		Schema<?> filterParameterSchema = new Schema<>();

		openAPI.path(
			"/test-entities",
			new PathItem() {
				{
					setGet(
						new Operation() {
							{
								addParametersItem(
									new Parameter() {
										{
											setName("filter");
											setSchema(filterParameterSchema);
										}
									});
								setTags(
									Collections.singletonList("TestEntity"));
							}
						});
				}
			});

		FilterableFieldsOpenAPIContributor filterableFieldsOpenAPIContributor =
			new FilterableFieldsOpenAPIContributor();

		filterableFieldsOpenAPIContributor.contribute(
			openAPI, new OpenAPIContext());

		Map<String, Object> extensions = filterParameterSchema.getExtensions();

		Assert.assertEquals(
			Collections.emptyMap(), extensions.get("x-filterable"));
	}

}
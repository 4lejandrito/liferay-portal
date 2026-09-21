/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.util.OpenAPITestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class FeatureFlagBatchResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Test
	public void testDerivedBatchEndpointsFeatureFlagDisabled()
		throws Exception {

		Assert.assertEquals(404, _getHttpCode(_EXPORT_BATCH_PATH));

		Assert.assertFalse(OpenAPITestUtil.hasOperation("delete", _BATCH_PATH));
		Assert.assertFalse(
			OpenAPITestUtil.hasOperation("post", _EXPORT_BATCH_PATH));
		Assert.assertTrue(OpenAPITestUtil.hasOperation("post", _BATCH_PATH));
	}

	@FeatureFlag(_FEATURE_FLAG_KEY)
	@Test
	public void testDerivedBatchEndpointsFeatureFlagEnabled() throws Exception {
		Assert.assertEquals(202, _getHttpCode(_EXPORT_BATCH_PATH));

		Assert.assertTrue(OpenAPITestUtil.hasOperation("delete", _BATCH_PATH));
		Assert.assertTrue(OpenAPITestUtil.hasOperation("post", _BATCH_PATH));
		Assert.assertTrue(
			OpenAPITestUtil.hasOperation("post", _EXPORT_BATCH_PATH));
	}

	private int _getHttpCode(String path) throws Exception {
		return HTTPTestUtil.invokeToHttpCode(
			null, _BASE_PATH + path, Http.Method.POST);
	}

	private static final String _BASE_PATH =
		"portal-tools-rest-builder-test/v1.0/";

	private static final String _BATCH_PATH =
		"feature-flag-method-test-entities/batch";

	private static final String _EXPORT_BATCH_PATH =
		"feature-flag-method-test-entities/export-batch";

	private static final String _FEATURE_FLAG_KEY = "METHOD-123";

}
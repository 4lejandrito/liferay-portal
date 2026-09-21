/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.util.OpenAPITestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ExcludedOperationIdsResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.vulcan.internal.configuration." +
				"VulcanConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"excludedOperationIds", "getFeatureFlagMethodTestEntitiesPage"
			).put(
				"graphQLEnabled", true
			).put(
				"path", "/portal-tools-rest-builder-test"
			).put(
				"restEnabled", true
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@FeatureFlag(_FEATURE_FLAG_KEY)
	@Test
	public void testExcludedOperationIdIsLeftOutOfDocument() throws Exception {
		Assert.assertFalse(OpenAPITestUtil.hasOperation("get", _METHOD_PATH));
		Assert.assertTrue(OpenAPITestUtil.hasOperation("post", _METHOD_PATH));
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Test
	public void testExcludedOperationIdIsNotFoundWhenFeatureFlagIsDisabled()
		throws Exception {

		Assert.assertEquals(404, _getHttpCode());
	}

	@FeatureFlag(_FEATURE_FLAG_KEY)
	@Test
	public void testExcludedOperationIdIsRejected() throws Exception {
		Assert.assertEquals(409, _getHttpCode());
	}

	private int _getHttpCode() throws Exception {
		return HTTPTestUtil.invokeToHttpCode(
			null, "portal-tools-rest-builder-test/v1.0/" + _METHOD_PATH,
			Http.Method.GET);
	}

	private static final String _FEATURE_FLAG_KEY = "METHOD-123";

	private static final String _METHOD_PATH =
		"feature-flag-method-test-entities";

	private String _pid;

}
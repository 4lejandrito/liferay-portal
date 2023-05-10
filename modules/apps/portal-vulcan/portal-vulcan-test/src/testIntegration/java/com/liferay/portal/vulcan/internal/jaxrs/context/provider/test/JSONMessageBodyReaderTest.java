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

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class JSONMessageBodyReaderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			JSONMessageBodyReaderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
			).put(
				"liferay.jackson", false
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-vulcan"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testCanNotDeserializeObjectToTextString() throws Exception {
		Assert.assertEquals(
			JSONUtil.put(
				"status", "BAD_REQUEST"
			).put(
				"title", "Unable to map JSON path: text"
			).toString(),
			_getJSONObject(
				JSONUtil.put("text", JSONUtil.put("key", "value")),
				"test-vulcan/test", Http.Method.POST
			).toString());
	}

	@Test
	public void testDeserializeArrayToJSONString() throws Exception {
		Assert.assertEquals(
			JSONUtil.put(
				"json",
				JSONUtil.putAll(
					"item1", "item2"
				).toString()
			).toString(),
			_getJSONObject(
				JSONUtil.put("json", JSONUtil.putAll("item1", "item2")),
				"test-vulcan/test", Http.Method.POST
			).toString());
	}

	@Test
	public void testDeserializeObjectToJSONString() throws Exception {
		Assert.assertEquals(
			JSONUtil.put(
				"json",
				JSONUtil.put(
					"key", "value"
				).toString()
			).toString(),
			_getJSONObject(
				JSONUtil.put("json", JSONUtil.put("key", "value")),
				"test-vulcan/test", Http.Method.POST
			).toString());
	}

	@Test
	public void testDeserializeStringToString() throws Exception {
		Assert.assertEquals(
			JSONUtil.put(
				"json", "content"
			).toString(),
			_getJSONObject(
				JSONUtil.put("json", "content"), "test-vulcan/test",
				Http.Method.POST
			).toString());
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@Consumes("application/json")
		@Path("/test")
		@POST
		@Produces("application/json")
		public TestDTO testClass(TestDTO testDTO) {
			return testDTO;
		}

	}

	public static class TestDTO {

		public String json;
		public String text;

	}

	private JSONObject _getJSONObject(
			JSONObject bodyJSONObject, String endpoint, Http.Method httpMethod)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.addHeader(
			"Authorization",
			"Basic " + Base64.encode("test@liferay.com:test".getBytes()));
		options.setLocation("http://localhost:8080/o/" + endpoint);
		options.setMethod(httpMethod);

		options.setBody(
			bodyJSONObject.toString(), ContentTypes.APPLICATION_JSON,
			StandardCharsets.UTF_8.name());

		return JSONFactoryUtil.createJSONObject(HttpUtil.URLtoString(options));
	}

	private ServiceRegistration<Application> _serviceRegistration;

}
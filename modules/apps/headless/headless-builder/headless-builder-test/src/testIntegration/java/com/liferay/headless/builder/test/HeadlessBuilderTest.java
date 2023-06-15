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

package com.liferay.headless.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.builder.application.HeadlessBuilderApplication;
import com.liferay.headless.builder.application.HeadlessBuilderApplicationFactory;
import com.liferay.headless.builder.application.HeadlessBuilderApplicationManager;
import com.liferay.headless.builder.test.info.item.provider.TestEntryInfoItemFieldValuesProvider;
import com.liferay.headless.builder.test.info.item.provider.TestEntryInfoItemFormProvider;
import com.liferay.headless.builder.test.info.item.provider.TestEntryInfoItemObjectProvider;
import com.liferay.headless.builder.test.model.TestEntry;
import com.liferay.headless.builder.test.object.util.ObjectDefinitionTestUtil;
import com.liferay.headless.builder.test.object.util.ObjectEntryTestUtil;
import com.liferay.headless.builder.test.object.util.ObjectRelationshipTestUtil;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.yaml.YAMLUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class HeadlessBuilderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {

		// Create dummy objects used as information for the REST APIs responses.

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					"Text", "String", true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME, false)));

		_objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition.getObjectDefinitionId(), _OBJECT_FIELD_NAME);

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, _OBJECT_FIELD_VALUE
			).build());

		// Check all the Headless Builder infrastructure exists

		_apiApplicationObjectDefinition = _getObjectDefinition(
			"MSOD_API_APPLICATION");
		_apiEndpointObjectDefinition = _getObjectDefinition(
			"MSOD_API_ENDPOINT");
		_apiFilterObjectDefinition = _getObjectDefinition("MSOD_API_FILTER");
		_apiPropertyObjectDefinition = _getObjectDefinition(
			"MSOD_API_PROPERTY");
		_apiSchemaObjectDefinition = _getObjectDefinition("MSOD_API_SCHEMA");
		_apiSortrObjectDefinition = _getObjectDefinition("MSOD_API_SORT");

		_apiApplicationAPIEndpointsObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiApplicationObjectDefinition.getObjectDefinitionId(),
				"apiApplicationToAPIEndpoints");

		_apiApplicationAPISchemasObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiApplicationObjectDefinition.getObjectDefinitionId(),
				"apiApplicationToAPISchemas");

		_apiEndpointAPIFiltersObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiEndpointObjectDefinition.getObjectDefinitionId(),
				"apiEndpointToAPIFilters");

		_apiEndpointAPISortsObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiEndpointObjectDefinition.getObjectDefinitionId(),
				"apiEndpointToAPISorts");

		_apiPropertiesAPIPropertiesObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiPropertyObjectDefinition.getObjectDefinitionId(),
				"apiPropertiesToAPIProperties");

		_apiSchemasAPIPropertiesObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiSchemaObjectDefinition.getObjectDefinitionId(),
				"apiSchemaToAPIProperties");

		_requestAPISchemaAPIEndpointsObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiSchemaObjectDefinition.getObjectDefinitionId(),
				"requestAPISchemaToAPIEndpoints");

		_responseAPISchemaAPIEndpointsObjectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				_apiSchemaObjectDefinition.getObjectDefinitionId(),
				"responseAPISchemaToAPIEndpoints");

		// Create Headless Builder REST APIs information

		_apiSchemaObjectEntry = _createApiSchemaObjectEntry(
			_objectDefinition.getExternalReferenceCode(), "MySchema");

		_apiApplicationObjectEntry1 = _createApiApplicationObjectEntry(
			"base-url-one", "ApiApplication1");

		_apiEndpointObjectEntry1 = _createApiEndpointObjectEntry(
			"GET", "My endpoint", "/endpoint-one", "company");

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiApplicationObjectEntry1.getObjectEntryId(),
			_apiEndpointObjectEntry1.getObjectEntryId(),
			_apiApplicationAPIEndpointsObjectRelationship,
			TestPropsValues.getUserId());

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiApplicationObjectEntry1.getObjectEntryId(),
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiApplicationAPISchemasObjectRelationship,
			TestPropsValues.getUserId());

		_apiApplicationObjectEntry2 = _createApiApplicationObjectEntry(
			"base-url-two", "ApiApplication2");

		_apiEndpointObjectEntry2 = _createApiEndpointObjectEntry(
			"GET", "My endpoint", "/endpoint-two", "company");

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiApplicationObjectEntry2.getObjectEntryId(),
			_apiEndpointObjectEntry2.getObjectEntryId(),
			_apiApplicationAPIEndpointsObjectRelationship,
			TestPropsValues.getUserId());

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiApplicationObjectEntry2.getObjectEntryId(),
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiApplicationAPISchemasObjectRelationship,
			TestPropsValues.getUserId());

		_apiPropertyObjectEntry = _createApiPropertyObjectEntry(
			"myProperty", _objectField.getExternalReferenceCode());

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiPropertyObjectEntry.getObjectEntryId(),
			_apiSchemasAPIPropertiesObjectRelationship,
			TestPropsValues.getUserId());

		ObjectRelationshipTestUtil.relateObjectEntries(
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiEndpointObjectEntry1.getObjectEntryId(),
			_responseAPISchemaAPIEndpointsObjectRelationship,
			TestPropsValues.getUserId());

		Bundle bundle = FrameworkUtil.getBundle(HeadlessBuilderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_infoItemFieldValuesProviderServiceRegistration =
			bundleContext.registerService(
				InfoItemFieldValuesProvider.class,
				new TestEntryInfoItemFieldValuesProvider(), null);
		_infoItemFormProviderServiceRegistration =
			bundleContext.registerService(
				InfoItemFormProvider.class, new TestEntryInfoItemFormProvider(),
				null);
		_infoItemObjectProviderServiceRegistration =
			bundleContext.registerService(
				InfoItemObjectProvider.class,
				new TestEntryInfoItemObjectProvider(), null);
	}

	@After
	public void tearDown() throws Exception {
		_infoItemFieldValuesProviderServiceRegistration.unregister();
		_infoItemFormProviderServiceRegistration.unregister();
		_infoItemObjectProviderServiceRegistration.unregister();

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiApplicationObjectEntry1.getObjectEntryId(),
			_apiEndpointObjectEntry1.getObjectEntryId(),
			_apiApplicationAPIEndpointsObjectRelationship);

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiApplicationObjectEntry1.getObjectEntryId(),
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiApplicationAPISchemasObjectRelationship);

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiApplicationObjectEntry2.getObjectEntryId(),
			_apiEndpointObjectEntry2.getObjectEntryId(),
			_apiApplicationAPIEndpointsObjectRelationship);

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiApplicationObjectEntry2.getObjectEntryId(),
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiApplicationAPISchemasObjectRelationship);

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiPropertyObjectEntry.getObjectEntryId(),
			_apiSchemasAPIPropertiesObjectRelationship);

		ObjectRelationshipTestUtil.deleteObjectEntriesRelationship(
			_apiSchemaObjectEntry.getObjectEntryId(),
			_apiEndpointObjectEntry1.getObjectEntryId(),
			_responseAPISchemaAPIEndpointsObjectRelationship);

		_objectEntryLocalService.deleteObjectEntry(
			_apiEndpointObjectEntry1.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			_apiEndpointObjectEntry2.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			_apiPropertyObjectEntry.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			_apiSchemaObjectEntry.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			_apiApplicationObjectEntry1.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			_apiApplicationObjectEntry2.getObjectEntryId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());
	}

	@FeatureFlags("LPS-171047")
	@Test
	public void testHeadlessBuilderApplication() throws Exception {
		_withHeadlessBuilderApplication(
			TestPropsValues.getCompanyId(),
			() -> {
				JSONObject jsonObject = _invoke(
					"headless-builder/v1.0/test-entries/" +
						_testEntry.getTestEntryId(),
					Http.Method.GET);

				JSONAssert.assertEquals(
					JSONUtil.put(
						"date", _formatDate(_testEntry.getDateField())
					).put(
						"number", (int)_testEntry.getLongField()
					).toString(),
					jsonObject.toString(), true);
			});
	}

	@FeatureFlags("LPS-171047")
	@Test
	public void testHeadlessBuilderApplicationOnADifferentCompany()
		throws Exception {

		_withHeadlessBuilderApplication(
			0,
			() -> {
				JSONObject jsonObject = _invoke(
					"headless-builder/v1.0/test-entries/" +
						_testEntry.getTestEntryId(),
					Http.Method.GET);

				JSONAssert.assertEquals(
					JSONUtil.put(
						"status", "NOT_FOUND"
					).put(
						"title", "The operation could not be found."
					).toString(),
					jsonObject.toString(), true);
			});
	}

	@Test
	public void testHeadlessBuilderApplicationReturnExpectedSchema()
		throws Exception {

		CountDownLatch addedCountLatch = new CountDownLatch(1);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceTracker<?, ?> serviceTracker =
			new ServiceTracker<Application, Application>(
				bundleContext, Application.class, null) {

				@Override
				public Application addingService(
					ServiceReference<Application> serviceReference) {

					String property = (String)serviceReference.getProperty(
						"osgi.jaxrs.application.base");

					if (property.contains(_API_APPLICATION_BASE_URL_VALUE)) {
						addedCountLatch.countDown();

						return super.addingService(serviceReference);
					}

					return null;
				}

			};

		try {
			serviceTracker.open();

			_headlessBuilderApplicationManager.publishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());

			addedCountLatch.await(1, TimeUnit.MINUTES);

			String apiApplication1baseURL =
				(String)_getObjectEntryPropertyValue(
					_apiApplicationObjectEntry1, _API_APPLICATION_BASE_URL);
			String apiEndpoint1Path = (String)_getObjectEntryPropertyValue(
				_apiEndpointObjectEntry1, _API_ENDPOINT_PATH);

			HttpURLConnection httpURLConnection = _createHttpURLConnection(
				apiApplication1baseURL + apiEndpoint1Path, Http.Method.GET);

			httpURLConnection.connect();

			// TODO Assert schema

			Assert.assertEquals(200, httpURLConnection.getResponseCode());
		}
		finally {
			serviceTracker.close();
			_headlessBuilderApplicationManager.unpublishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());
		}
	}

	@Test
	public void testHeadlessBuilderApplicationWithoutFeatureFlag()
		throws Exception {

		HttpURLConnection httpURLConnection = _createHttpURLConnection(
			"headless-builder/v1.0/test-entries/" + _testEntry.getTestEntryId(),
			Http.Method.GET);

		httpURLConnection.connect();

		Assert.assertEquals(404, httpURLConnection.getResponseCode());
	}

	@FeatureFlags("LPS-171047")
	@Test
	public void testMissingHeadlessBuilderApplication() throws Exception {
		JSONObject jsonObject = _invoke(
			"headless-builder/v1.0/test-entries/1234", Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title", "The operation could not be found."
			).toString(),
			jsonObject.toString(), true);
	}

	@Test
	public void testPublishApiApplication() throws Exception {
		CountDownLatch addedCountLatch = new CountDownLatch(1);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceTracker<?, ?> serviceTracker =
			new ServiceTracker<Application, Application>(
				bundleContext, Application.class, null) {

				@Override
				public Application addingService(
					ServiceReference<Application> serviceReference) {

					String property = (String)serviceReference.getProperty(
						"osgi.jaxrs.application.base");

					if (property.contains(_API_APPLICATION_BASE_URL_VALUE)) {
						addedCountLatch.countDown();

						return super.addingService(serviceReference);
					}

					return null;
				}

			};

		try {
			serviceTracker.open();

			_headlessBuilderApplicationManager.publishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());

			addedCountLatch.await(1, TimeUnit.MINUTES);

			String apiApplication1baseURL =
				(String)_getObjectEntryPropertyValue(
					_apiApplicationObjectEntry1, _API_APPLICATION_BASE_URL);
			String apiEndpoint1Path = (String)_getObjectEntryPropertyValue(
				_apiEndpointObjectEntry1, _API_ENDPOINT_PATH);

			HttpURLConnection httpURLConnection = _createHttpURLConnection(
				apiApplication1baseURL + apiEndpoint1Path, Http.Method.GET);

			httpURLConnection.connect();

			Assert.assertEquals(200, httpURLConnection.getResponseCode());
		}
		finally {
			serviceTracker.close();
			_headlessBuilderApplicationManager.unpublishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());
		}
	}

	@Test
	public void testPublishMultipleApiApplications() throws Exception {
		CountDownLatch addedCountLatch = new CountDownLatch(2);
		CountDownLatch removedCountLatch = new CountDownLatch(1);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceTracker<?, ?> serviceTracker =
			new ServiceTracker<Application, Application>(
				bundleContext, Application.class, null) {

				@Override
				public Application addingService(
					ServiceReference<Application> serviceReference) {

					Object property = serviceReference.getProperty(
						"liferay.headless.builder.application");

					if ((property != null) && (Boolean)property) {
						addedCountLatch.countDown();

						return super.addingService(serviceReference);
					}

					return null;
				}

				@Override
				public void removedService(
					ServiceReference<Application> serviceReference,
					Application service) {

					removedCountLatch.countDown();
					super.removedService(serviceReference, service);
				}

			};

		try {
			serviceTracker.open();

			_headlessBuilderApplicationManager.publishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());

			_headlessBuilderApplicationManager.publishApplication(
				_apiApplicationObjectEntry2.getExternalReferenceCode());

			addedCountLatch.await(1, TimeUnit.MINUTES);

			String apiApplication1baseURL =
				(String)_getObjectEntryPropertyValue(
					_apiApplicationObjectEntry1, _API_APPLICATION_BASE_URL);
			String apiEndpoint1Path = (String)_getObjectEntryPropertyValue(
				_apiEndpointObjectEntry1, _API_ENDPOINT_PATH);

			HttpURLConnection httpURLConnection = _createHttpURLConnection(
				apiApplication1baseURL + apiEndpoint1Path, Http.Method.GET);

			httpURLConnection.connect();

			Assert.assertEquals(200, httpURLConnection.getResponseCode());

			String apiApplication2baseURL =
				(String)_getObjectEntryPropertyValue(
					_apiApplicationObjectEntry2, _API_APPLICATION_BASE_URL);
			String apiEndpoint2Path = (String)_getObjectEntryPropertyValue(
				_apiEndpointObjectEntry2, _API_ENDPOINT_PATH);

			httpURLConnection = _createHttpURLConnection(
				apiApplication2baseURL + apiEndpoint2Path, Http.Method.GET);

			httpURLConnection.connect();

			Assert.assertEquals(200, httpURLConnection.getResponseCode());

			_headlessBuilderApplicationManager.unpublishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());

			removedCountLatch.await(1, TimeUnit.MINUTES);

			httpURLConnection = _createHttpURLConnection(
				apiApplication1baseURL + apiEndpoint1Path, Http.Method.GET);

			httpURLConnection.connect();

			Assert.assertEquals(404, httpURLConnection.getResponseCode());

			httpURLConnection = _createHttpURLConnection(
				apiApplication2baseURL + apiEndpoint2Path, Http.Method.GET);

			httpURLConnection.connect();

			Assert.assertEquals(200, httpURLConnection.getResponseCode());
		}
		finally {
			serviceTracker.close();
			_headlessBuilderApplicationManager.unpublishApplication(
				_apiApplicationObjectEntry1.getExternalReferenceCode());
			_headlessBuilderApplicationManager.unpublishApplication(
				_apiApplicationObjectEntry2.getExternalReferenceCode());
		}
	}

	private ObjectEntry _createApiApplicationObjectEntry(
			String baseURL, String title)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_apiApplicationObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_API_APPLICATION_BASE_URL, baseURL
			).put(
				_API_APPLICATION_TITLE, title
			).build());
	}

	private ObjectEntry _createApiEndpointObjectEntry(
			String httpMethod, String name, String path, String scope)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_apiEndpointObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_API_ENDPOINT_HTTP_METHOD, httpMethod
			).put(
				_API_ENDPOINT_NAME, name
			).put(
				_API_ENDPOINT_PATH, path
			).put(
				_API_ENDPOINT_SCOPE, scope
			).build());
	}

	private ObjectEntry _createApiPropertyObjectEntry(
			String name, String objectFieldERC)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_apiPropertyObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_API_PROPERTY_NAME, name
			).put(
				_API_PROPERTY_OBJECT_FIELD_ERC, objectFieldERC
			).build());
	}

	private ObjectEntry _createApiSchemaObjectEntry(
			String mainObjectDefinitionERC, String name)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_apiSchemaObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_API_SCHEMA_MAIN_OBJECT_DEFINITION_ERC, mainObjectDefinitionERC
			).put(
				_API_SCHEMA_NAME, name
			).build());
	}

	private HttpURLConnection _createHttpURLConnection(
			String endpoint, Http.Method method)
		throws Exception {

		URL url = new URL("http://localhost:8080/o/" + endpoint);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");

		httpURLConnection.setRequestProperty(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

		String encodedUserNameAndPassword = Base64.encode(
			"test@liferay.com:test".getBytes(StandardCharsets.UTF_8));

		httpURLConnection.setRequestProperty(
			"Authorization", "Basic " + encodedUserNameAndPassword);

		httpURLConnection.setRequestMethod(method.toString());

		return httpURLConnection;
	}

	private String _formatDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		return simpleDateFormat.format(date);
	}

	private ObjectDefinition _getObjectDefinition(String objectDefinitionERC)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionERC, TestPropsValues.getCompanyId());

		if (objectDefinition == null) {
			throw new NoSuchObjectDefinitionException();
		}

		return objectDefinition;
	}

	private Serializable _getObjectEntryPropertyValue(
		ObjectEntry objectEntry, String propertyName) {

		Map<String, Serializable> values = objectEntry.getValues();

		return values.get(propertyName);
	}

	private JSONObject _invoke(String endpoint, Http.Method method)
		throws Exception {

		HttpURLConnection httpURLConnection = _createHttpURLConnection(
			endpoint, method);

		httpURLConnection.connect();

		try {
			return JSONFactoryUtil.createJSONObject(
				StringUtil.read(httpURLConnection.getInputStream()));
		}
		catch (FileNotFoundException fileNotFoundException) {
			return JSONFactoryUtil.createJSONObject(
				StringUtil.read(httpURLConnection.getErrorStream()));
		}
	}

	private OpenAPIYAML _readOpenAPIYAML(String yamlFile) throws Exception {
		try (InputStream inputStream = getClass().getResourceAsStream(
				yamlFile)) {

			return YAMLUtil.loadOpenAPIYAML(StringUtil.read(inputStream));
		}
	}

	private void _withHeadlessBuilderApplication(
			long companyId, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		HeadlessBuilderApplication headlessBuilderApplication =
			_headlessBuilderApplicationFactory.getHeadlessBuilderApplication(
				companyId, _readOpenAPIYAML("/rest-openapi.yaml"));

		HeadlessBuilderApplication.Handle handle =
			headlessBuilderApplication.deploy();

		try {
			unsafeRunnable.run();
		}
		finally {
			handle.undeploy();
		}
	}

	private static final String _API_APPLICATION_BASE_URL = "baseURL";

	private static final String _API_APPLICATION_BASE_URL_VALUE = "base-url";

	private static final String _API_APPLICATION_TITLE = "title";

	private static final String _API_ENDPOINT_HTTP_METHOD = "hTTPMethod";

	private static final String _API_ENDPOINT_NAME = "name";

	private static final String _API_ENDPOINT_PATH = "path";

	private static final String _API_ENDPOINT_SCOPE = "scope";

	private static final String _API_PROPERTY_NAME = "name";

	private static final String _API_PROPERTY_OBJECT_FIELD_ERC =
		"objectFieldERC";

	private static final String _API_SCHEMA_MAIN_OBJECT_DEFINITION_ERC =
		"mainObjectDefinitionERC";

	private static final String _API_SCHEMA_NAME = "name";

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE =
		RandomTestUtil.randomString();

	private ObjectRelationship _apiApplicationAPIEndpointsObjectRelationship;
	private ObjectRelationship _apiApplicationAPISchemasObjectRelationship;
	private ObjectDefinition _apiApplicationObjectDefinition;
	private ObjectEntry _apiApplicationObjectEntry1;
	private ObjectEntry _apiApplicationObjectEntry2;
	private ObjectRelationship _apiEndpointAPIFiltersObjectRelationship;
	private ObjectRelationship _apiEndpointAPISortsObjectRelationship;
	private ObjectDefinition _apiEndpointObjectDefinition;
	private ObjectEntry _apiEndpointObjectEntry1;
	private ObjectEntry _apiEndpointObjectEntry2;
	private ObjectDefinition _apiFilterObjectDefinition;
	private ObjectRelationship _apiPropertiesAPIPropertiesObjectRelationship;
	private ObjectDefinition _apiPropertyObjectDefinition;
	private ObjectEntry _apiPropertyObjectEntry;
	private ObjectDefinition _apiSchemaObjectDefinition;
	private ObjectEntry _apiSchemaObjectEntry;
	private ObjectRelationship _apiSchemasAPIPropertiesObjectRelationship;
	private ObjectDefinition _apiSortrObjectDefinition;

	@Inject
	private HeadlessBuilderApplicationFactory
		_headlessBuilderApplicationFactory;

	@Inject
	private HeadlessBuilderApplicationManager
		_headlessBuilderApplicationManager;

	private ServiceRegistration<?>
		_infoItemFieldValuesProviderServiceRegistration;
	private ServiceRegistration<?> _infoItemFormProviderServiceRegistration;
	private ServiceRegistration<?> _infoItemObjectProviderServiceRegistration;
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectRelationship _requestAPISchemaAPIEndpointsObjectRelationship;
	private ObjectRelationship _responseAPISchemaAPIEndpointsObjectRelationship;
	private final TestEntry _testEntry = TestEntry.INSTANCE;

}
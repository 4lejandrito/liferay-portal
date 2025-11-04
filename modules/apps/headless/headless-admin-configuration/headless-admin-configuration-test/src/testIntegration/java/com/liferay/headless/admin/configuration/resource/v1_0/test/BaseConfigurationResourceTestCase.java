/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.configuration.client.dto.v1_0.Configuration;
import com.liferay.headless.admin.configuration.client.http.HttpInvoker;
import com.liferay.headless.admin.configuration.client.pagination.Page;
import com.liferay.headless.admin.configuration.client.resource.v1_0.ConfigurationResource;
import com.liferay.headless.admin.configuration.client.serdes.v1_0.ConfigurationSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public abstract class BaseConfigurationResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_configurationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		configurationResource = ConfigurationResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Configuration configuration1 = randomConfiguration();

		String json = objectMapper.writeValueAsString(configuration1);

		Configuration configuration2 = ConfigurationSerDes.toDTO(json);

		Assert.assertTrue(equals(configuration1, configuration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Configuration configuration = randomConfiguration();

		String json1 = objectMapper.writeValueAsString(configuration);
		String json2 = ConfigurationSerDes.toJSON(configuration);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		Configuration configuration = randomConfiguration();

		configuration.setExternalReferenceCode(regex);

		String json = ConfigurationSerDes.toJSON(configuration);

		Assert.assertFalse(json.contains(regex));

		configuration = ConfigurationSerDes.toDTO(json);

		Assert.assertEquals(regex, configuration.getExternalReferenceCode());
	}

	@Test
	public void testGetPortalInstanceConfiguration() throws Exception {
		Configuration postConfiguration =
			testGetPortalInstanceConfiguration_addConfiguration();

		Configuration getConfiguration =
			configurationResource.getPortalInstanceConfiguration(
				postConfiguration.getExternalReferenceCode());

		assertEquals(postConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration
			testGetPortalInstanceConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	@Test
	public void testGetPortalInstanceConfigurationsPage() throws Exception {
		Page<Configuration> page =
			configurationResource.getPortalInstanceConfigurationsPage();

		long totalCount = page.getTotalCount();

		Configuration configuration1 =
			testGetPortalInstanceConfigurationsPage_addConfiguration(
				randomConfiguration());

		Configuration configuration2 =
			testGetPortalInstanceConfigurationsPage_addConfiguration(
				randomConfiguration());

		page = configurationResource.getPortalInstanceConfigurationsPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(configuration1, (List<Configuration>)page.getItems());
		assertContains(configuration2, (List<Configuration>)page.getItems());
		assertValid(
			page, testGetPortalInstanceConfigurationsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetPortalInstanceConfigurationsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Configuration
			testGetPortalInstanceConfigurationsPage_addConfiguration(
				Configuration configuration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteConfiguration() throws Exception {
		Configuration postConfiguration =
			testGetSiteConfiguration_addConfiguration();

		Configuration getConfiguration =
			configurationResource.getSiteConfiguration(
				testGetSiteConfiguration_getSiteExternalReferenceCode(),
				postConfiguration.getExternalReferenceCode(), null);

		assertEquals(postConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration testGetSiteConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	protected String testGetSiteConfiguration_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteConfigurationsPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteConfigurationsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteConfigurationsPage_getIrrelevantSiteExternalReferenceCode();

		Page<Configuration> page =
			configurationResource.getSiteConfigurationsPage(
				siteExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			Configuration irrelevantConfiguration =
				testGetSiteConfigurationsPage_addConfiguration(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantConfiguration());

			page = configurationResource.getSiteConfigurationsPage(
				irrelevantSiteExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantConfiguration, (List<Configuration>)page.getItems());
			assertValid(
				page,
				testGetSiteConfigurationsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		Configuration configuration1 =
			testGetSiteConfigurationsPage_addConfiguration(
				siteExternalReferenceCode, randomConfiguration());

		Configuration configuration2 =
			testGetSiteConfigurationsPage_addConfiguration(
				siteExternalReferenceCode, randomConfiguration());

		page = configurationResource.getSiteConfigurationsPage(
			siteExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(configuration1, (List<Configuration>)page.getItems());
		assertContains(configuration2, (List<Configuration>)page.getItems());
		assertValid(
			page,
			testGetSiteConfigurationsPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteConfigurationsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-configuration/v1.0/sites/{siteExternalReferenceCode}/configurations/batch".
				replace(
					"{siteExternalReferenceCode}",
					String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected Configuration testGetSiteConfigurationsPage_addConfiguration(
			String siteExternalReferenceCode, Configuration configuration)
		throws Exception {

		return configurationResource.postSiteConfiguration(
			siteExternalReferenceCode, configuration);
	}

	protected String
			testGetSiteConfigurationsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteConfigurationsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSystemConfiguration() throws Exception {
		Configuration postConfiguration =
			testGetSystemConfiguration_addConfiguration();

		Configuration getConfiguration =
			configurationResource.getSystemConfiguration(
				postConfiguration.getExternalReferenceCode());

		assertEquals(postConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration testGetSystemConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	@Test
	public void testGetSystemConfigurationsPage() throws Exception {
		Page<Configuration> page =
			configurationResource.getSystemConfigurationsPage();

		long totalCount = page.getTotalCount();

		Configuration configuration1 =
			testGetSystemConfigurationsPage_addConfiguration(
				randomConfiguration());

		Configuration configuration2 =
			testGetSystemConfigurationsPage_addConfiguration(
				randomConfiguration());

		page = configurationResource.getSystemConfigurationsPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(configuration1, (List<Configuration>)page.getItems());
		assertContains(configuration2, (List<Configuration>)page.getItems());
		assertValid(page, testGetSystemConfigurationsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetSystemConfigurationsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Configuration testGetSystemConfigurationsPage_addConfiguration(
			Configuration configuration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPortalInstanceConfiguration() throws Exception {
		Configuration randomConfiguration = randomConfiguration();

		Configuration postConfiguration =
			testPostPortalInstanceConfiguration_addConfiguration(
				randomConfiguration);

		assertEquals(randomConfiguration, postConfiguration);
		assertValid(postConfiguration);
	}

	protected Configuration
			testPostPortalInstanceConfiguration_addConfiguration(
				Configuration configuration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteConfiguration() throws Exception {
		Configuration randomConfiguration = randomConfiguration();

		Configuration postConfiguration =
			testPostSiteConfiguration_addConfiguration(randomConfiguration);

		assertEquals(randomConfiguration, postConfiguration);
		assertValid(postConfiguration);
	}

	protected Configuration testPostSiteConfiguration_addConfiguration(
			Configuration configuration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSystemConfiguration() throws Exception {
		Configuration randomConfiguration = randomConfiguration();

		Configuration postConfiguration =
			testPostSystemConfiguration_addConfiguration(randomConfiguration);

		assertEquals(randomConfiguration, postConfiguration);
		assertValid(postConfiguration);
	}

	protected Configuration testPostSystemConfiguration_addConfiguration(
			Configuration configuration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutPortalInstanceConfiguration() throws Exception {
		Configuration postConfiguration =
			testPutPortalInstanceConfiguration_addConfiguration();

		Configuration randomConfiguration = randomConfiguration();

		Configuration putConfiguration =
			configurationResource.putPortalInstanceConfiguration(
				postConfiguration.getExternalReferenceCode(),
				randomConfiguration);

		assertEquals(randomConfiguration, putConfiguration);
		assertValid(putConfiguration);

		Configuration getConfiguration =
			configurationResource.getPortalInstanceConfiguration(
				putConfiguration.getExternalReferenceCode());

		assertEquals(randomConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration
			testPutPortalInstanceConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	@Test
	public void testPutSiteConfiguration() throws Exception {
		Configuration postConfiguration =
			testPutSiteConfiguration_addConfiguration();

		Configuration randomConfiguration = randomConfiguration();

		Configuration putConfiguration =
			configurationResource.putSiteConfiguration(
				testPutSiteConfiguration_getSiteExternalReferenceCode(),
				postConfiguration.getExternalReferenceCode(),
				randomConfiguration);

		assertEquals(randomConfiguration, putConfiguration);
		assertValid(putConfiguration);

		Configuration getConfiguration =
			configurationResource.getSiteConfiguration(
				testPutSiteConfiguration_getSiteExternalReferenceCode(),
				putConfiguration.getExternalReferenceCode(), null);

		assertEquals(randomConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration testPutSiteConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	protected String testPutSiteConfiguration_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testPutSystemConfiguration() throws Exception {
		Configuration postConfiguration =
			testPutSystemConfiguration_addConfiguration();

		Configuration randomConfiguration = randomConfiguration();

		Configuration putConfiguration =
			configurationResource.putSystemConfiguration(
				postConfiguration.getExternalReferenceCode(),
				randomConfiguration);

		assertEquals(randomConfiguration, putConfiguration);
		assertValid(putConfiguration);

		Configuration getConfiguration =
			configurationResource.getSystemConfiguration(
				putConfiguration.getExternalReferenceCode());

		assertEquals(randomConfiguration, getConfiguration);
		assertValid(getConfiguration);
	}

	protected Configuration testPutSystemConfiguration_addConfiguration()
		throws Exception {

		return configurationResource.postSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomConfiguration());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		Configuration configuration, List<Configuration> configurations) {

		boolean contains = false;

		for (Configuration item : configurations) {
			if (equals(configuration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			configurations + " does not contain " + configuration, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Configuration configuration1, Configuration configuration2) {

		Assert.assertTrue(
			configuration1 + " does not equal " + configuration2,
			equals(configuration1, configuration2));
	}

	protected void assertEquals(
		List<Configuration> configurations1,
		List<Configuration> configurations2) {

		Assert.assertEquals(configurations1.size(), configurations2.size());

		for (int i = 0; i < configurations1.size(); i++) {
			Configuration configuration1 = configurations1.get(i);
			Configuration configuration2 = configurations2.get(i);

			assertEquals(configuration1, configuration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Configuration> configurations1,
		List<Configuration> configurations2) {

		Assert.assertEquals(configurations1.size(), configurations2.size());

		for (Configuration configuration1 : configurations1) {
			boolean contains = false;

			for (Configuration configuration2 : configurations2) {
				if (equals(configuration1, configuration2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				configurations2 + " does not contain " + configuration1,
				contains);
		}
	}

	protected void assertValid(Configuration configuration) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (configuration.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (configuration.getProperties() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<Configuration> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Configuration> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Configuration> configurations = page.getItems();

		int size = configurations.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.configuration.dto.v1_0.
						Configuration.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		Configuration configuration1, Configuration configuration2) {

		if (configuration1 == configuration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						configuration1.getExternalReferenceCode(),
						configuration2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (!equals(
						(Map)configuration1.getProperties(),
						(Map)configuration2.getProperties())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_configurationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_configurationResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator, Configuration configuration) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = configuration.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("properties")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected Configuration randomConfiguration() throws Exception {
		return new Configuration() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Configuration randomIrrelevantConfiguration() throws Exception {
		Configuration randomIrrelevantConfiguration = randomConfiguration();

		return randomIrrelevantConfiguration;
	}

	protected Configuration randomPatchConfiguration() throws Exception {
		return randomConfiguration();
	}

	protected ConfigurationResource configurationResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseConfigurationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.configuration.resource.v1_0.
			ConfigurationResource _configurationResource;

}
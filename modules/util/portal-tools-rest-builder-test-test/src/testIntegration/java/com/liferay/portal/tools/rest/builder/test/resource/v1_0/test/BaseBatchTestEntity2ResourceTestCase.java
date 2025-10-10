/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.BatchTestEntity2Resource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.BatchTestEntity2SerDes;
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
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public abstract class BaseBatchTestEntity2ResourceTestCase {

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

		_batchTestEntity2Resource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		batchTestEntity2Resource = BatchTestEntity2Resource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
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

		BatchTestEntity2 batchTestEntity21 = randomBatchTestEntity2();

		String json = objectMapper.writeValueAsString(batchTestEntity21);

		BatchTestEntity2 batchTestEntity22 = BatchTestEntity2SerDes.toDTO(json);

		Assert.assertTrue(equals(batchTestEntity21, batchTestEntity22));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		BatchTestEntity2 batchTestEntity2 = randomBatchTestEntity2();

		String json1 = objectMapper.writeValueAsString(batchTestEntity2);
		String json2 = BatchTestEntity2SerDes.toJSON(batchTestEntity2);

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

		BatchTestEntity2 batchTestEntity2 = randomBatchTestEntity2();

		batchTestEntity2.setExternalReferenceCode(regex);
		batchTestEntity2.setName(regex);

		String json = BatchTestEntity2SerDes.toJSON(batchTestEntity2);

		Assert.assertFalse(json.contains(regex));

		batchTestEntity2 = BatchTestEntity2SerDes.toDTO(json);

		Assert.assertEquals(regex, batchTestEntity2.getExternalReferenceCode());
		Assert.assertEquals(regex, batchTestEntity2.getName());
	}

	@Test
	public void testDeleteBatchTestEntity2ByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BatchTestEntity2 batchTestEntity2 =
			testDeleteBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		assertHttpResponseStatusCode(
			204,
			batchTestEntity2Resource.
				deleteBatchTestEntity2ByExternalReferenceCodeHttpResponse(
					batchTestEntity2.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			batchTestEntity2Resource.
				getBatchTestEntity2ByExternalReferenceCodeHttpResponse(
					batchTestEntity2.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			batchTestEntity2Resource.
				getBatchTestEntity2ByExternalReferenceCodeHttpResponse("-"));
	}

	protected BatchTestEntity2
			testDeleteBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteBatchTestEntity2ByExternalReferenceCode()
		throws Exception {

		// No namespace

		BatchTestEntity2 batchTestEntity21 =
			testGraphQLDeleteBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteBatchTestEntity2ByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										batchTestEntity21.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteBatchTestEntity2ByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"batchTestEntity2ByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									batchTestEntity21.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace test_v1_0

		BatchTestEntity2 batchTestEntity22 =
			testGraphQLDeleteBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"deleteBatchTestEntity2ByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											batchTestEntity22.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/test_v1_0",
				"Object/deleteBatchTestEntity2ByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"test_v1_0",
					new GraphQLField(
						"batchTestEntity2ByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										batchTestEntity22.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BatchTestEntity2
			testGraphQLDeleteBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2()
		throws Exception {

		return testGraphQLBatchTestEntity2_addBatchTestEntity2();
	}

	@Test
	public void testGetBatchTestEntities2Page() throws Exception {
		Page<BatchTestEntity2> page =
			batchTestEntity2Resource.getBatchTestEntities2Page();

		long totalCount = page.getTotalCount();

		BatchTestEntity2 batchTestEntity21 =
			testGetBatchTestEntities2Page_addBatchTestEntity2(
				randomBatchTestEntity2());

		BatchTestEntity2 batchTestEntity22 =
			testGetBatchTestEntities2Page_addBatchTestEntity2(
				randomBatchTestEntity2());

		page = batchTestEntity2Resource.getBatchTestEntities2Page();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			batchTestEntity21, (List<BatchTestEntity2>)page.getItems());
		assertContains(
			batchTestEntity22, (List<BatchTestEntity2>)page.getItems());
		assertValid(page, testGetBatchTestEntities2Page_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetBatchTestEntities2Page_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected BatchTestEntity2
			testGetBatchTestEntities2Page_addBatchTestEntity2(
				BatchTestEntity2 batchTestEntity2)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetBatchTestEntities2Page() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"batchTestEntities2",
			new HashMap<String, Object>() {
				{
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject batchTestEntities2JSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/batchTestEntities2");

		long totalCount = batchTestEntities2JSONObject.getLong("totalCount");

		BatchTestEntity2 batchTestEntity21 =
			testGraphQLBatchTestEntity2_addBatchTestEntity2(
				randomBatchTestEntity2());

		BatchTestEntity2 batchTestEntity22 =
			testGraphQLBatchTestEntity2_addBatchTestEntity2(
				randomBatchTestEntity2());

		batchTestEntities2JSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/batchTestEntities2");

		Assert.assertEquals(
			totalCount + 2, batchTestEntities2JSONObject.getLong("totalCount"));

		assertContains(
			batchTestEntity21,
			Arrays.asList(
				BatchTestEntity2SerDes.toDTOs(
					batchTestEntities2JSONObject.getString("items"))));
		assertContains(
			batchTestEntity22,
			Arrays.asList(
				BatchTestEntity2SerDes.toDTOs(
					batchTestEntities2JSONObject.getString("items"))));

		// Using the namespace test_v1_0

		batchTestEntities2JSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(new GraphQLField("test_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/test_v1_0",
			"JSONObject/batchTestEntities2");

		Assert.assertEquals(
			totalCount + 2, batchTestEntities2JSONObject.getLong("totalCount"));

		assertContains(
			batchTestEntity21,
			Arrays.asList(
				BatchTestEntity2SerDes.toDTOs(
					batchTestEntities2JSONObject.getString("items"))));
		assertContains(
			batchTestEntity22,
			Arrays.asList(
				BatchTestEntity2SerDes.toDTOs(
					batchTestEntities2JSONObject.getString("items"))));
	}

	@Test
	public void testGetBatchTestEntity2ByExternalReferenceCode()
		throws Exception {

		BatchTestEntity2 postBatchTestEntity2 =
			testGetBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		BatchTestEntity2 getBatchTestEntity2 =
			batchTestEntity2Resource.getBatchTestEntity2ByExternalReferenceCode(
				postBatchTestEntity2.getExternalReferenceCode());

		assertEquals(postBatchTestEntity2, getBatchTestEntity2);
		assertValid(getBatchTestEntity2);
	}

	protected BatchTestEntity2
			testGetBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetBatchTestEntity2ByExternalReferenceCode()
		throws Exception {

		BatchTestEntity2 batchTestEntity2 =
			testGraphQLGetBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		// No namespace

		Assert.assertTrue(
			equals(
				batchTestEntity2,
				BatchTestEntity2SerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"batchTestEntity2ByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												batchTestEntity2.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/batchTestEntity2ByExternalReferenceCode"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				batchTestEntity2,
				BatchTestEntity2SerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"batchTestEntity2ByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													batchTestEntity2.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/batchTestEntity2ByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetBatchTestEntity2ByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"batchTestEntity2ByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace test_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"batchTestEntity2ByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected BatchTestEntity2
			testGraphQLGetBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2()
		throws Exception {

		return testGraphQLBatchTestEntity2_addBatchTestEntity2();
	}

	@Test
	public void testPostBatchTestEntity2() throws Exception {
		BatchTestEntity2 randomBatchTestEntity2 = randomBatchTestEntity2();

		BatchTestEntity2 postBatchTestEntity2 =
			testPostBatchTestEntity2_addBatchTestEntity2(
				randomBatchTestEntity2);

		assertEquals(randomBatchTestEntity2, postBatchTestEntity2);
		assertValid(postBatchTestEntity2);
	}

	protected BatchTestEntity2 testPostBatchTestEntity2_addBatchTestEntity2(
			BatchTestEntity2 batchTestEntity2)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostBatchTestEntity2() throws Exception {
		BatchTestEntity2 randomBatchTestEntity2 = randomBatchTestEntity2();

		BatchTestEntity2 batchTestEntity2 =
			testGraphQLBatchTestEntity2_addBatchTestEntity2(
				randomBatchTestEntity2);

		Assert.assertTrue(equals(randomBatchTestEntity2, batchTestEntity2));
	}

	@Test
	public void testPutBatchTestEntity2ByExternalReferenceCode()
		throws Exception {

		BatchTestEntity2 postBatchTestEntity2 =
			testPutBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2();

		BatchTestEntity2 randomBatchTestEntity2 = randomBatchTestEntity2();

		BatchTestEntity2 putBatchTestEntity2 =
			batchTestEntity2Resource.putBatchTestEntity2ByExternalReferenceCode(
				postBatchTestEntity2.getExternalReferenceCode(),
				randomBatchTestEntity2);

		assertEquals(randomBatchTestEntity2, putBatchTestEntity2);
		assertValid(putBatchTestEntity2);

		BatchTestEntity2 getBatchTestEntity2 =
			batchTestEntity2Resource.getBatchTestEntity2ByExternalReferenceCode(
				putBatchTestEntity2.getExternalReferenceCode());

		assertEquals(randomBatchTestEntity2, getBatchTestEntity2);
		assertValid(getBatchTestEntity2);

		BatchTestEntity2 newBatchTestEntity2 =
			testPutBatchTestEntity2ByExternalReferenceCode_createBatchTestEntity2();

		putBatchTestEntity2 =
			batchTestEntity2Resource.putBatchTestEntity2ByExternalReferenceCode(
				newBatchTestEntity2.getExternalReferenceCode(),
				newBatchTestEntity2);

		assertEquals(newBatchTestEntity2, putBatchTestEntity2);
		assertValid(putBatchTestEntity2);

		getBatchTestEntity2 =
			batchTestEntity2Resource.getBatchTestEntity2ByExternalReferenceCode(
				putBatchTestEntity2.getExternalReferenceCode());

		assertEquals(newBatchTestEntity2, getBatchTestEntity2);

		Assert.assertEquals(
			newBatchTestEntity2.getExternalReferenceCode(),
			putBatchTestEntity2.getExternalReferenceCode());
	}

	protected BatchTestEntity2
			testPutBatchTestEntity2ByExternalReferenceCode_addBatchTestEntity2()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected BatchTestEntity2
			testPutBatchTestEntity2ByExternalReferenceCode_createBatchTestEntity2()
		throws Exception {

		return randomBatchTestEntity2();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		BatchTestEntity2 batchTestEntity21 =
			testBatchEngineDeleteImportTask_addBatchTestEntity2();

		testBatchEngineDeleteImportTask_deleteBatchTestEntity2(
			200, batchTestEntity21.getExternalReferenceCode());
	}

	protected BatchTestEntity2
			testBatchEngineDeleteImportTask_addBatchTestEntity2()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void testBatchEngineDeleteImportTask_deleteBatchTestEntity2(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity2",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected BatchTestEntity2 testGraphQLBatchTestEntity2_addBatchTestEntity2()
		throws Exception {

		return testGraphQLBatchTestEntity2_addBatchTestEntity2(
			randomBatchTestEntity2());
	}

	protected BatchTestEntity2 testGraphQLBatchTestEntity2_addBatchTestEntity2(
			BatchTestEntity2 batchTestEntity2)
		throws Exception {

		JSONDeserializer<BatchTestEntity2> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(BatchTestEntity2.class)) {

			if (getGraphQLValue(field.get(batchTestEntity2)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(batchTestEntity2)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createBatchTestEntity2",
						new HashMap<String, Object>() {
							{
								put("batchTestEntity2", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createBatchTestEntity2"),
			BatchTestEntity2.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		BatchTestEntity2 batchTestEntity2,
		List<BatchTestEntity2> batchTestEntity2s) {

		boolean contains = false;

		for (BatchTestEntity2 item : batchTestEntity2s) {
			if (equals(batchTestEntity2, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			batchTestEntity2s + " does not contain " + batchTestEntity2,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		BatchTestEntity2 batchTestEntity21,
		BatchTestEntity2 batchTestEntity22) {

		Assert.assertTrue(
			batchTestEntity21 + " does not equal " + batchTestEntity22,
			equals(batchTestEntity21, batchTestEntity22));
	}

	protected void assertEquals(
		List<BatchTestEntity2> batchTestEntity2s1,
		List<BatchTestEntity2> batchTestEntity2s2) {

		Assert.assertEquals(
			batchTestEntity2s1.size(), batchTestEntity2s2.size());

		for (int i = 0; i < batchTestEntity2s1.size(); i++) {
			BatchTestEntity2 batchTestEntity21 = batchTestEntity2s1.get(i);
			BatchTestEntity2 batchTestEntity22 = batchTestEntity2s2.get(i);

			assertEquals(batchTestEntity21, batchTestEntity22);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<BatchTestEntity2> batchTestEntity2s1,
		List<BatchTestEntity2> batchTestEntity2s2) {

		Assert.assertEquals(
			batchTestEntity2s1.size(), batchTestEntity2s2.size());

		for (BatchTestEntity2 batchTestEntity21 : batchTestEntity2s1) {
			boolean contains = false;

			for (BatchTestEntity2 batchTestEntity22 : batchTestEntity2s2) {
				if (equals(batchTestEntity21, batchTestEntity22)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				batchTestEntity2s2 + " does not contain " + batchTestEntity21,
				contains);
		}
	}

	protected void assertValid(BatchTestEntity2 batchTestEntity2)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (batchTestEntity2.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (batchTestEntity2.getName() == null) {
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

	protected void assertValid(Page<BatchTestEntity2> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<BatchTestEntity2> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<BatchTestEntity2> batchTestEntity2s =
			page.getItems();

		int size = batchTestEntity2s.size();

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
					com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						BatchTestEntity2.class)) {

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
		BatchTestEntity2 batchTestEntity21,
		BatchTestEntity2 batchTestEntity22) {

		if (batchTestEntity21 == batchTestEntity22) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						batchTestEntity21.getExternalReferenceCode(),
						batchTestEntity22.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						batchTestEntity21.getName(),
						batchTestEntity22.getName())) {

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

		if (!(_batchTestEntity2Resource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_batchTestEntity2Resource;

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
		EntityField entityField, String operator,
		BatchTestEntity2 batchTestEntity2) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = batchTestEntity2.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = batchTestEntity2.getName();

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

	protected BatchTestEntity2 randomBatchTestEntity2() throws Exception {
		return new BatchTestEntity2() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected BatchTestEntity2 randomIrrelevantBatchTestEntity2()
		throws Exception {

		BatchTestEntity2 randomIrrelevantBatchTestEntity2 =
			randomBatchTestEntity2();

		return randomIrrelevantBatchTestEntity2;
	}

	protected BatchTestEntity2 randomPatchBatchTestEntity2() throws Exception {
		return randomBatchTestEntity2();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected BatchTestEntity2Resource batchTestEntity2Resource;
	protected ImportTaskResource importTaskResource;
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
		LogFactoryUtil.getLog(BaseBatchTestEntity2ResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.tools.rest.builder.test.resource.v1_0.
		BatchTestEntity2Resource _batchTestEntity2Resource;

}
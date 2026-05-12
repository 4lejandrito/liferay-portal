/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_rootJSONObject = JSONFactoryUtil.createJSONObject(
			StringUtil.read(
				OpenAPIUtilTest.class.getResourceAsStream(
					"dependencies/openapi.json")));
	}

	@Test
	public void testBuildTool() throws Exception {
		_testBuildTool(
			"This is the summary", "get_c_test.json", "getItemsPage");
		_testBuildTool(
			"This is the summary. This is the description",
			"get_test_v1.0_items.json", "getItems");
		_testBuildTool(
			"POST /v1.0/items", "post_test_v1.0_items.json", "postItem");
		_testBuildTool(
			"This is the description", "get_test_v1.0_items_itemId.json",
			"getItem");
		_testBuildTool(
			"PATCH /v1.0/items/{itemId}", "patch_test_v1.0_items_itemId.json",
			"patchItem");
		_testBuildTool(
			"PUT /v1.0/items/{itemId}", "put_test_v1.0_items_itemId.json",
			"putItem");
		_testBuildTool(
			"POST /v1.0/uploads", "post_test_v1.0_uploads.json", "uploadFile");
		_testBuildTool(
			"POST /v1.0/binaries", "post_test_v1.0_binaries.json",
			"postBinary");
	}

	@Test
	public void testBuildTools() throws Exception {
		List<ToolSummary> toolSummaries = OpenAPIUtil.buildTools(
			_rootJSONObject);

		Map<String, String> byName = new HashMap<>();

		for (ToolSummary toolSummary : toolSummaries) {
			byName.put(toolSummary.getName(), toolSummary.getDescription());
		}

		Assert.assertEquals(toolSummaries.toString(), 8, toolSummaries.size());

		Assert.assertEquals("This is the description", byName.get("getItem"));
		Assert.assertEquals(
			"This is the summary. This is the description",
			byName.get("getItems"));
		Assert.assertEquals("This is the summary", byName.get("getItemsPage"));
		Assert.assertEquals(
			"PATCH /v1.0/items/{itemId}", byName.get("patchItem"));
		Assert.assertEquals("POST /v1.0/items", byName.get("postItem"));
	}

	@Test
	public void testBuildToolThrowsWhenToolMissing() {
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"OpenAPI document has no tool with name \"missing\"",
			() -> OpenAPIUtil.buildTool(true, _rootJSONObject, "missing"));
	}

	@Test
	public void testBuildToolWhenInjectVulcanParametersIsFalse()
		throws Exception {

		Tool toolDetail = OpenAPIUtil.buildTool(
			false, _rootJSONObject, "getItems");

		JSONAssert.assertEquals(
			"{\"properties\":{},\"required\":[],\"type\":\"object\"}",
			new ObjectMapper(
			).writeValueAsString(
				toolDetail.getInputSchema()
			),
			true);
	}

	@Test
	public void testFindTool() throws Exception {
		OpenAPIUtil.Operation operation = OpenAPIUtil.findTool(
			_rootJSONObject, "uploadFile");

		Assert.assertEquals("post", operation.method);
		Assert.assertEquals("/v1.0/uploads", operation.path);
		Assert.assertEquals(
			"uploadFile",
			operation.operationJSONObject.getString("operationId"));
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithEmptyArguments()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments(), null, null, "get",
			"http://localhost/v1.0/items?restrictFields=actions", "getItems");
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithMultipleQueryParameters()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments(
				"active", "true", "fields", "name", "itemId", "123", "page",
				"1"),
			null, null, "get",
			"http://localhost/v1.0/items/123?active=true&page=1&fields=name" +
				"&restrictFields=actions",
			"getItem");
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithPathParameter()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("itemId", "123"), null, null, "get",
			"http://localhost/v1.0/items/123?restrictFields=actions",
			"getItem");
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithRestrictFieldsAlreadyHasActions()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("restrictFields", "name,actions"), null, null, "get",
			"http://localhost/v1.0/items?restrictFields=name%2Cactions",
			"getItems");
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithUserRestrictFields()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("restrictFields", "name"), null, null, "get",
			"http://localhost/v1.0/items?restrictFields=name%2Cactions",
			"getItems");
	}

	@Test
	public void testGetHttpCallArgumentsWhenGetWithValueNeedingURLEncoding()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("fields", "name eq 'John Doe'", "itemId", "123"), null,
			null, "get",
			"http://localhost/v1.0/items/123?fields=name+eq+%27John+Doe%27" +
				"&restrictFields=actions",
			"getItem");
	}

	@Test
	public void testGetHttpCallArgumentsWhenPatchWithBodyAsJSONObject()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("body", JSONUtil.put("name", "Test"), "itemId", "123"),
			"{\"name\":\"Test\"}", "application/json", "patch",
			"http://localhost/v1.0/items/123", "patchItem");
	}

	@Test
	public void testGetHttpCallArgumentsWhenPostDoesNotInjectRestrictFields()
		throws Exception {

		_assertGetHttpCallArguments(
			_arguments("body", JSONUtil.put("name", "Test")),
			"{\"name\":\"Test\"}", "application/json", "post",
			"http://localhost/v1.0/items", "postItem");
	}

	@Test
	public void testGetHttpCallArgumentsWhenUploadFileBuildsBinaryAndStringParts()
		throws Exception {

		String base64 = Base64.getEncoder(
		).encodeToString(
			"hello".getBytes()
		);

		OpenAPIUtil.HttpCallArguments httpCallArguments =
			OpenAPIUtil.getHttpCallArguments(
				_arguments(
					"data",
					JSONUtil.put(
						"contentType", "text/plain"
					).put(
						"data", base64
					).put(
						"filename", "hello.txt"
					),
					"name", "Hello"),
				"http://localhost",
				OpenAPIUtil.findTool(_rootJSONObject, "postBinary"),
				_rootJSONObject);

		Assert.assertNull(httpCallArguments.getBody());
		Assert.assertEquals(
			"multipart/form-data", httpCallArguments.getContentType());
		Assert.assertEquals("post", httpCallArguments.getMethod());
		Assert.assertEquals(
			"http://localhost/v1.0/binaries", httpCallArguments.getURL());

		List<Http.FilePart> fileParts = httpCallArguments.getFileParts();

		Assert.assertEquals(fileParts.toString(), 1, fileParts.size());

		Http.FilePart filePart = fileParts.get(0);

		Assert.assertEquals("text/plain", filePart.getContentType());
		Assert.assertEquals("hello.txt", filePart.getFileName());
		Assert.assertEquals("data", filePart.getName());
		Assert.assertArrayEquals("hello".getBytes(), filePart.getValue());

		Map<String, String> parts = httpCallArguments.getParts();

		Assert.assertEquals(parts.toString(), 1, parts.size());
		Assert.assertEquals("Hello", parts.get("name"));
	}

	@Test
	public void testGetHttpCallArgumentsWhenUploadFileBuildsStringPartsOnly()
		throws Exception {

		OpenAPIUtil.HttpCallArguments httpCallArguments =
			OpenAPIUtil.getHttpCallArguments(
				_arguments("boolean", true, "integer", 42, "string", "hello"),
				"http://localhost",
				OpenAPIUtil.findTool(_rootJSONObject, "uploadFile"),
				_rootJSONObject);

		Assert.assertNull(httpCallArguments.getBody());
		Assert.assertEquals(
			"multipart/form-data", httpCallArguments.getContentType());
		Assert.assertNull(httpCallArguments.getFileParts());
		Assert.assertEquals("post", httpCallArguments.getMethod());
		Assert.assertEquals(
			"http://localhost/v1.0/uploads", httpCallArguments.getURL());

		Map<String, String> parts = httpCallArguments.getParts();

		Assert.assertEquals(parts.toString(), 3, parts.size());
		Assert.assertEquals("true", parts.get("boolean"));
		Assert.assertEquals("42", parts.get("integer"));
		Assert.assertEquals("hello", parts.get("string"));
	}

	@Test
	public void testIsMultipartRequest() throws Exception {
		Assert.assertFalse(
			OpenAPIUtil.isMultipartRequest(_operation("getItemsPage")));
		Assert.assertFalse(
			OpenAPIUtil.isMultipartRequest(_operation("postItem")));
		Assert.assertTrue(
			OpenAPIUtil.isMultipartRequest(_operation("uploadFile")));
	}

	private JSONObject _arguments(Object... keyValuePairs) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (int i = 0; i < keyValuePairs.length; i += 2) {
			jsonObject.put((String)keyValuePairs[i], keyValuePairs[i + 1]);
		}

		return jsonObject;
	}

	private void _assertGetHttpCallArguments(
			JSONObject argumentsJSONObject, String expectedBody,
			String expectedContentType, String expectedMethod,
			String expectedURL, String tool)
		throws Exception {

		OpenAPIUtil.HttpCallArguments httpCallArguments =
			OpenAPIUtil.getHttpCallArguments(
				argumentsJSONObject, "http://localhost",
				OpenAPIUtil.findTool(_rootJSONObject, tool), _rootJSONObject);

		Assert.assertEquals(expectedBody, httpCallArguments.getBody());
		Assert.assertEquals(
			expectedContentType, httpCallArguments.getContentType());
		Assert.assertNull(httpCallArguments.getFileParts());
		Assert.assertEquals(expectedMethod, httpCallArguments.getMethod());
		Assert.assertNull(httpCallArguments.getParts());
		Assert.assertEquals(expectedURL, httpCallArguments.getURL());
	}

	private JSONObject _operation(String tool) {
		return OpenAPIUtil.findTool(_rootJSONObject, tool).operationJSONObject;
	}

	private String _read(String fileName) throws Exception {
		return StringUtil.read(
			getClass().getResourceAsStream("dependencies/" + fileName));
	}

	private void _testBuildTool(
			String expectedDescription, String expectedSchemaFileName,
			String tool)
		throws Exception {

		Tool toolDetail = OpenAPIUtil.buildTool(true, _rootJSONObject, tool);

		Assert.assertEquals(tool, toolDetail.getName());
		Assert.assertEquals(expectedDescription, toolDetail.getDescription());

		JSONAssert.assertEquals(
			_read(expectedSchemaFileName),
			new ObjectMapper(
			).writeValueAsString(
				toolDetail.getInputSchema()
			),
			true);
	}

	private static JSONObject _rootJSONObject;

}
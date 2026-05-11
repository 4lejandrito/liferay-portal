/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mcp.server.rest.client.http.HttpInvoker;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class ToolSetResourceTest extends BaseToolSetResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGetTool() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetToolSets() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetToolSummaries() throws Exception {
	}

	@Override
	@Test
	public void testInvokeTool() throws Exception {
		byte[] bytes = "Hello, MCP multipart upload!".getBytes(
			StandardCharsets.UTF_8);

		String fileName =
			"mcp-upload-" + RandomTestUtil.randomString() + ".txt";

		HttpInvoker.HttpResponse httpResponse =
			toolSetResource.invokeToolHttpResponse(
				"headless-delivery-v1.0", "postSiteDocument",
				JSONUtil.put(
					"file",
					JSONUtil.put(
						"contentType", "text/plain"
					).put(
						"data",
						Base64.getEncoder(
						).encodeToString(
							bytes
						)
					).put(
						"filename", fileName
					)
				).put(
					"siteId", testGroup.getGroupId()
				).toString());

		Assert.assertEquals(
			httpResponse.getContent(), 200, httpResponse.getStatusCode());

		JSONObject documentJSONObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		Assert.assertEquals(fileName, documentJSONObject.getString("title"));
		Assert.assertEquals(
			bytes.length, documentJSONObject.getInt("sizeInBytes"));
		Assert.assertTrue(documentJSONObject.getLong("id") > 0);
	}

}

// LIFERAY-REST-BUILDER-HASH:62830971
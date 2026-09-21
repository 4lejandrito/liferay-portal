/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;

/**
 * @author Daniel Raposo
 */
public class OpenAPITestUtil {

	public static boolean hasOperation(String httpMethod, String path)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "portal-tools-rest-builder-test/v1.0/openapi.json",
			Http.Method.GET);

		JSONObject pathsJSONObject = jsonObject.getJSONObject("paths");

		String versionedPath = "/v1.0/" + path;

		if (!pathsJSONObject.has(versionedPath)) {
			return false;
		}

		JSONObject pathJSONObject = pathsJSONObject.getJSONObject(
			versionedPath);

		return pathJSONObject.has(httpMethod);
	}

}
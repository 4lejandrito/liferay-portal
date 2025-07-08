/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=MCP Servlet Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class MCPServletFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (httpServletRequest.getRequestURI(
			).endsWith(
				"/.well-known/oauth-authorization-server"
			)) {

			httpServletResponse.setHeader("Content-Type", "application/json");
			httpServletResponse.setHeader("Cache-Control", "no-cache");

			httpServletResponse.getWriter(
			).write(
				JSONUtil.put(
					"authorization_endpoint",
					"http://localhost:8080/o/oauth2/authorize"
				).put(
					"code_challenge_methods_supported",
					JSONUtil.putAll("plain", "S256")
				).put(
					"grant_types_supported",
					JSONUtil.putAll("authorization_code", "refresh_token")
				).put(
					"issuer", "http://localhost:8080"
				).put(
					"registration_endpoint",
					"http://localhost:8080/o/oauth2/register"
				).put(
					"response_modes_supported", JSONUtil.putAll("query")
				).put(
					"response_types_supported", JSONUtil.putAll("code")
				).put(
					"revocation_endpoint",
					"http://localhost:8080/o/oauth2/token"
				).put(
					"token_endpoint",
					"http://localhost:8080/o/oauth2/token?client_id=id-63495d1f-9d1a-a931-52e3-4f5cb7694e&client_secret=secret-6d3cc48e-b564-e8da-2df7-5b187cc62c5b"
				).put(
					"token_endpoint_auth_methods_supported",
					JSONUtil.putAll(
						"client_secret_basic", "client_secret_post", "none")
				).toString()
			);

			return;
		}

		if (httpServletRequest.getRequestURI(
			).endsWith(
				"/o/oauth2/register"
			)) {

			httpServletResponse.setHeader("Content-Type", "application/json");
			httpServletResponse.setHeader("Cache-Control", "no-cache");

			httpServletResponse.getWriter(
			).write(
				JSONUtil.put(
					"client_id", "id-63495d1f-9d1a-a931-52e3-4f5cb7694e"
				).put(
					"client_secret",
					"secret-6d3cc48e-b564-e8da-2df7-5b187cc62c5b"
				).put(
					"client_secret_expires_at", 0
				).toString()
			);

			return;
		}

		processFilter(
			MCPServletFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

}
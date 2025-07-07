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

		if (!httpServletRequest.getRequestURI(
			).contains(
				".well-known"
			)) {

			processFilter(
				MCPServletFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		httpServletResponse.setHeader("Content-Type", "application/json");

		httpServletResponse.getWriter(
		).write(
			JSONUtil.put(
				"authorization_endpoint",
				"https://supreme-goblin-steady.ngrok-free.app/o/oauth2/authorize?response_type=code&client_id=id-5595fec0-58ae-e1c9-b53b-b49a891e377a"
			).put(
				"code_challenge_methods_supported",
				JSONUtil.putAll("plain", "S256")
			).put(
				"grant_types_supported",
				JSONUtil.putAll("authorization_code", "refresh_token")
			).put(
				"issuer", "https://supreme-goblin-steady.ngrok-free.app"
			).put(
				"registration_endpoint",
				"https://supreme-goblin-steady.ngrok-free.app/o/oauth2/register"
			).put(
				"response_modes_supported", JSONUtil.putAll("query")
			).put(
				"response_types_supported", JSONUtil.putAll("code")
			).put(
				"revocation_endpoint",
				"https://supreme-goblin-steady.ngrok-free.app/o/oauth2/token"
			).put(
				"token_endpoint",
				"https://supreme-goblin-steady.ngrok-free.app/o/oauth2/token?client_id=id-5595fec0-58ae-e1c9-b53b-b49a891e377a&client_secret=secret-daf1231e-e967-2978-d524-646cb3c2883"
			).put(
				"token_endpoint_auth_methods_supported",
				JSONUtil.putAll(
					"client_secret_basic", "client_secret_post", "none")
			).toString()
		);
	}

}
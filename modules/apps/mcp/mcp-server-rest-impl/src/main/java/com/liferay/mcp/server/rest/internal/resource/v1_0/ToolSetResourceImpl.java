/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.resource.v1_0;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSet;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.mcp.server.rest.internal.util.OpenAPIUtil;
import com.liferay.mcp.server.rest.resource.v1_0.ToolSetResource;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.ApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceMethodInfoDTO;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/tool-set.properties",
	scope = ServiceScope.PROTOTYPE, service = ToolSetResource.class
)
public class ToolSetResourceImpl extends BaseToolSetResourceImpl {

	@Override
	public Tool getTool(String toolSetName, String toolName) throws Exception {
		return OpenAPIUtil.buildTool(
			!Objects.equals(toolSetName, _TOOL_SET_NAME),
			_openAPI(_resolveToolSet(toolSetName)), toolName);
	}

	@Override
	public Page<ToolSet> getToolSets() throws Exception {
		Map<String, ToolSetInfo> toolSetInfos = _readToolSetInfos();

		List<ToolSet> toolSetSummaries = new ArrayList<>();

		for (Map.Entry<String, ToolSetInfo> entry :
				_listToolSets().entrySet()) {

			ToolSet toolSetSummary = new ToolSet();

			toolSetSummary.setName(entry::getKey);

			ToolSetInfo toolSetInfo = entry.getValue();

			toolSetSummary.setDescription(
				() -> _describe(
					toolSetInfos.get(toolSetInfo._applicationBase)));

			toolSetSummaries.add(toolSetSummary);
		}

		return Page.of(toolSetSummaries);
	}

	@Override
	public Page<ToolSummary> getToolSummaries(String toolSetName)
		throws Exception {

		return Page.of(
			OpenAPIUtil.buildTools(_openAPI(_resolveToolSet(toolSetName))));
	}

	@Override
	public Response invokeToolObject(
			String toolSetName, String toolName, Object object)
		throws Exception {

		if (Objects.equals(toolSetName, _TOOL_SET_NAME)) {
			JSONObject argumentsJSONObject = _parseArguments(object);

			if (Objects.equals(toolName, "getTool")) {
				return _ok(
					getTool(
						argumentsJSONObject.getString("toolSetName"),
						argumentsJSONObject.getString("toolName")));
			}

			if (Objects.equals(toolName, "getToolSets")) {
				return _ok(getToolSets());
			}

			if (Objects.equals(toolName, "getToolSummaries")) {
				return _ok(
					getToolSummaries(
						argumentsJSONObject.getString("toolSetName")));
			}

			if (Objects.equals(toolName, "invokeTool")) {
				return invokeToolObject(
					argumentsJSONObject.getString("toolSetName"),
					argumentsJSONObject.getString("toolName"),
					argumentsJSONObject.opt("body"));
			}
		}

		ToolSetInfo toolSetInfo = _resolveToolSet(toolSetName);

		JSONObject rootJSONObject = _openAPI(toolSetInfo);

		OpenAPIUtil.Operation operation = OpenAPIUtil.findTool(
			rootJSONObject, toolName);

		return _invoke(
			_parseArguments(object), operation, rootJSONObject, toolSetInfo);
	}

	@Activate
	protected void activate() {
		_bundleContext = FrameworkUtil.getBundle(
			ToolSetResourceImpl.class
		).getBundleContext();
	}

	private String _describe(ToolSetInfo toolSetInfo) {
		if ((toolSetInfo == null) ||
			Validator.isNull(toolSetInfo._description)) {

			return null;
		}

		return toolSetInfo._description;
	}

	private String _findOpenAPIPath(ApplicationDTO applicationDTO) {
		for (ResourceDTO resourceDTO : applicationDTO.resourceDTOs) {
			String openAPIPath = _findOpenAPIPath(resourceDTO.resourceMethods);

			if (openAPIPath != null) {
				return openAPIPath;
			}
		}

		return _findOpenAPIPath(applicationDTO.resourceMethods);
	}

	private String _findOpenAPIPath(
		ResourceMethodInfoDTO[] resourceMethodInfoDTOs) {

		if (resourceMethodInfoDTOs == null) {
			return null;
		}

		for (ResourceMethodInfoDTO resourceMethodInfoDTO :
				resourceMethodInfoDTOs) {

			String path = resourceMethodInfoDTO.path;

			if ((path != null) && path.contains("/openapi")) {
				return StringUtil.replace(path, "{type:json|yaml}", "json");
			}
		}

		return null;
	}

	private Map<String, String> _forwardedHeaders() {
		Map<String, String> headers = new HashMap<>();

		for (String name : _AUTH_HEADER_NAMES) {
			String value = contextHttpServletRequest.getHeader(name);

			if (Validator.isNotNull(value)) {
				headers.put(name, value);
			}
		}

		return headers;
	}

	private String _get(String url) throws Exception {
		Http.Options options = new Http.Options();

		options.setTimeout(60000);

		Map<String, String> headers = _forwardedHeaders();

		if (!headers.isEmpty()) {
			options.setHeaders(headers);
		}

		options.setLocation(url);

		String content = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if (responseCode >= 300) {
			throw new Exception(
				StringBundler.concat(
					"HTTP ", responseCode, " for ", url, ": ", content));
		}

		return content;
	}

	private Response _invoke(
			JSONObject argumentsJSONObject, OpenAPIUtil.Operation operation,
			JSONObject rootJSONObject, ToolSetInfo toolSetInfo)
		throws Exception {

		String baseURL = contextUriInfo.getBaseUri(
		).resolve(
			Portal.PATH_MODULE + toolSetInfo._applicationBase
		).toString();

		OpenAPIUtil.HttpCallArguments httpCallArguments =
			OpenAPIUtil.getHttpCallArguments(
				argumentsJSONObject, baseURL, operation, rootJSONObject);

		Map<String, String> headers = _forwardedHeaders();

		Http.Options options = new Http.Options();

		if (httpCallArguments.getBody() != null) {
			options.setBody(
				httpCallArguments.getBody(), httpCallArguments.getContentType(),
				StringPool.UTF8);

			if (httpCallArguments.getContentType() != null) {
				headers.put("Content-Type", httpCallArguments.getContentType());
			}
		}
		else if ((httpCallArguments.getFileParts() != null) ||
				 (httpCallArguments.getParts() != null)) {

			String boundary = UUID.randomUUID(
			).toString();

			headers.put(
				"Content-Type", "multipart/form-data; boundary=" + boundary);
		}

		if (httpCallArguments.getFileParts() != null) {
			options.setFileParts(httpCallArguments.getFileParts());
		}

		options.setHeaders(headers);
		options.setLocation(httpCallArguments.getURL());
		options.setMethod(
			Http.Method.valueOf(
				StringUtil.toUpperCase(httpCallArguments.getMethod())));

		if (httpCallArguments.getParts() != null) {
			options.setParts(httpCallArguments.getParts());
		}

		options.setTimeout(60000);

		String content = _stripPageActions(_http.URLtoString(options));

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if (responseCode < 300) {
			return Response.ok(
				content, ContentTypes.TEXT_PLAIN_UTF8
			).build();
		}

		return Response.status(
			responseCode
		).entity(
			StringBundler.concat(
				"Status code: ", responseCode, ", Content:\n", content)
		).type(
			ContentTypes.TEXT_PLAIN_UTF8
		).build();
	}

	private Map<String, ToolSetInfo> _listToolSets() {
		Map<String, ToolSetInfo> toolSets = new TreeMap<>();

		RuntimeDTO runtimeDTO = _jaxrsServiceRuntime.getRuntimeDTO();

		for (ApplicationDTO applicationDTO : runtimeDTO.applicationDTOs) {
			String base = applicationDTO.base;

			if (Validator.isNull(base)) {
				continue;
			}

			if (!base.startsWith(StringPool.SLASH)) {
				base = StringPool.SLASH + base;
			}

			String openAPIPath = _findOpenAPIPath(applicationDTO);

			if (openAPIPath == null) {
				continue;
			}

			String name = StringUtil.replace(
				base.substring(1) + _versionPath(openAPIPath), CharPool.SLASH,
				CharPool.DASH);

			toolSets.put(name, new ToolSetInfo(base, openAPIPath));
		}

		return toolSets;
	}

	private Response _ok(Object value) {
		return Response.ok(
			_stripPageActions(_jsonFactory.looseSerializeDeep(value)),
			ContentTypes.TEXT_PLAIN_UTF8
		).build();
	}

	private JSONObject _openAPI(ToolSetInfo toolSetInfo) throws Exception {
		String url = _openAPIURL(toolSetInfo);

		JSONObject cachedJSONObject = _openAPICache.get(url);

		if (cachedJSONObject != null) {
			return cachedJSONObject;
		}

		JSONObject rootJSONObject = _jsonFactory.createJSONObject(_get(url));

		_openAPICache.put(url, rootJSONObject);

		return rootJSONObject;
	}

	private String _openAPIURL(ToolSetInfo toolSetInfo) {
		String serverURL =
			_portal.getPortalURL(contextHttpServletRequest) +
				_portal.getPathContext() + Portal.PATH_MODULE;

		return serverURL + toolSetInfo._applicationBase +
			toolSetInfo._openAPIPath;
	}

	private JSONObject _parseArguments(Object object) throws Exception {
		if (object == null) {
			return _jsonFactory.createJSONObject();
		}

		if (object instanceof Map) {
			return _jsonFactory.createJSONObject((Map<String, ?>)object);
		}

		return _jsonFactory.createJSONObject(object.toString());
	}

	private ToolSetInfo _readToolSetInfo(Object service) {
		if (service == null) {
			return null;
		}

		OpenAPIDefinition openAPIDefinition = service.getClass(
		).getAnnotation(
			OpenAPIDefinition.class
		);

		if (openAPIDefinition == null) {
			return null;
		}

		Info info = openAPIDefinition.info();

		return new ToolSetInfo(
			null, null, _stripClientJARMessage(info.description()));
	}

	private Map<String, ToolSetInfo> _readToolSetInfos() {
		Map<String, ToolSetInfo> toolSetInfos = new HashMap<>();

		ServiceReference<?>[] serviceReferences;

		try {
			serviceReferences = _bundleContext.getAllServiceReferences(
				null, "(openapi.resource=true)");
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			if (_log.isWarnEnabled()) {
				_log.warn(invalidSyntaxException);
			}

			return toolSetInfos;
		}

		if (serviceReferences == null) {
			return toolSetInfos;
		}

		for (ServiceReference<?> serviceReference : serviceReferences) {
			Object pathProperty = serviceReference.getProperty(
				"openapi.resource.path");

			if (!(pathProperty instanceof String)) {
				continue;
			}

			Object service = _bundleContext.getService(serviceReference);

			try {
				ToolSetInfo toolSetInfo = _readToolSetInfo(service);

				if (toolSetInfo != null) {
					toolSetInfos.putIfAbsent((String)pathProperty, toolSetInfo);
				}
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		return toolSetInfos;
	}

	private ToolSetInfo _resolveToolSet(String name) {
		ToolSetInfo toolSetInfo = _listToolSets().get(name);

		if (toolSetInfo == null) {
			throw new IllegalArgumentException("Unknown tool-set: " + name);
		}

		return toolSetInfo;
	}

	private String _stripClientJARMessage(String description) {
		if (description == null) {
			return null;
		}

		int index = description.indexOf(_CLIENT_JAR_MESSAGE_PREFIX);

		if (index < 0) {
			return description;
		}

		String stripped = description.substring(0, index);

		while (stripped.endsWith(StringPool.SPACE) ||
			   stripped.endsWith(StringPool.PERIOD)) {

			stripped = stripped.substring(0, stripped.length() - 1);
		}

		return stripped;
	}

	private String _stripPageActions(String content) {
		if (Validator.isNull(content) || (content.charAt(0) != '{') ||
			!content.contains("\"actions\"")) {

			return content;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(content);

			if (!jsonObject.has("actions")) {
				return content;
			}

			jsonObject.remove("actions");

			return jsonObject.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return content;
		}
	}

	private String _versionPath(String openAPIPath) {
		int index = openAPIPath.lastIndexOf("/openapi");

		if (index <= 0) {
			return StringPool.BLANK;
		}

		return openAPIPath.substring(0, index);
	}

	private static final String[] _AUTH_HEADER_NAMES = {
		"Authorization", "Cookie", "x-csrf-token"
	};

	private static final String _CLIENT_JAR_MESSAGE_PREFIX =
		"A Java client JAR is available for use with the group ID";

	private static final String _TOOL_SET_NAME = "mcp-server-v1.0";

	private static final Log _log = LogFactoryUtil.getLog(
		ToolSetResourceImpl.class);

	private static final Map<String, JSONObject> _openAPICache =
		new ConcurrentHashMap<>();

	private BundleContext _bundleContext;

	@Reference
	private Http _http;

	@Reference
	private JaxrsServiceRuntime _jaxrsServiceRuntime;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	private static class ToolSetInfo {

		private ToolSetInfo(String applicationBase, String openAPIPath) {
			this(applicationBase, openAPIPath, null);
		}

		private ToolSetInfo(
			String applicationBase, String openAPIPath, String description) {

			_applicationBase = applicationBase;
			_openAPIPath = openAPIPath;
			_description = description;
		}

		private final String _applicationBase;
		private final String _description;
		private final String _openAPIPath;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtil {

	public static Tool buildTool(
		boolean injectVulcanParameters, JSONObject rootJSONObject,
		String tool) {

		Operation operation = findTool(rootJSONObject, tool);

		Tool toolDTO = new Tool();

		toolDTO.setDescription(
			() -> _describeTool(
				operation.method, operation.operationJSONObject,
				operation.path));
		toolDTO.setInputSchema(
			() -> _buildInputSchema(
				injectVulcanParameters, operation.method,
				operation.operationJSONObject,
				operation.pathParametersJSONArray, rootJSONObject));
		toolDTO.setName(() -> tool);

		return toolDTO;
	}

	public static List<ToolSummary> buildTools(JSONObject rootJSONObject) {
		JSONObject pathsJSONObject = rootJSONObject.getJSONObject("paths");

		if (pathsJSONObject == null) {
			throw new IllegalArgumentException(
				"OpenAPI document has no \"paths\" object");
		}

		List<ToolSummary> toolSummaries = new ArrayList<>();

		for (String path : pathsJSONObject.keySet()) {
			JSONObject pathItemJSONObject = pathsJSONObject.getJSONObject(path);

			for (String method : _METHODS) {
				JSONObject operationJSONObject =
					pathItemJSONObject.getJSONObject(method);

				if (operationJSONObject == null) {
					continue;
				}

				ToolSummary toolSummary = new ToolSummary();

				toolSummary.setDescription(
					() -> _describeTool(method, operationJSONObject, path));
				toolSummary.setName(
					() -> operationJSONObject.getString("operationId"));

				toolSummaries.add(toolSummary);
			}
		}

		return toolSummaries;
	}

	public static Operation findTool(JSONObject rootJSONObject, String tool) {
		JSONObject pathsJSONObject = rootJSONObject.getJSONObject("paths");

		if (pathsJSONObject == null) {
			throw new IllegalArgumentException(
				"OpenAPI document has no \"paths\" object");
		}

		for (String path : pathsJSONObject.keySet()) {
			JSONObject pathItemJSONObject = pathsJSONObject.getJSONObject(path);

			for (String method : _METHODS) {
				JSONObject operationJSONObject =
					pathItemJSONObject.getJSONObject(method);

				if ((operationJSONObject == null) ||
					!tool.equals(
						operationJSONObject.getString("operationId"))) {

					continue;
				}

				return new Operation(
					method, operationJSONObject, path,
					pathItemJSONObject.getJSONArray("parameters"));
			}
		}

		throw new IllegalArgumentException(
			"OpenAPI document has no tool with name \"" + tool + "\"");
	}

	public static HttpCallArguments getHttpCallArguments(
		JSONObject argumentsJSONObject, String baseURL, Operation operation,
		JSONObject rootJSONObject) {

		String[] pathRef = {operation.path};
		StringBundler queryStringSB = new StringBundler();
		Set<String> handledKeys = new HashSet<>(
			Arrays.asList(_VULCAN_QUERY_PARAMETERS));

		_applyParameters(
			argumentsJSONObject, handledKeys,
			operation.operationJSONObject.getJSONArray("parameters"), pathRef,
			queryStringSB);
		_applyParameters(
			argumentsJSONObject, handledKeys, operation.pathParametersJSONArray,
			pathRef, queryStringSB);

		for (String name : _VULCAN_QUERY_PARAMETERS) {
			Object value = argumentsJSONObject.opt(name);

			if (Objects.equals(name, "restrictFields") &&
				Objects.equals(operation.method, "get")) {

				value = _withActions(value);
			}

			if (value != null) {
				_appendQueryParameter(name, value, queryStringSB);
			}
		}

		String url = baseURL + pathRef[0];

		if (queryStringSB.length() > 0) {
			url = url + StringPool.QUESTION + queryStringSB.toString();
		}

		String body = null;
		String contentType = null;
		List<Http.FilePart> fileParts = null;
		Map<String, String> parts = null;

		if (isMultipartRequest(operation.operationJSONObject)) {
			contentType = _MULTIPART_CONTENT_TYPE;

			List<Http.FilePart> collectedFileParts = new ArrayList<>();
			Map<String, String> collectedParts = new LinkedHashMap<>();

			_applyMultipartBody(
				argumentsJSONObject, collectedFileParts, collectedParts,
				resolvedBodySchema(
					operation.operationJSONObject, rootJSONObject));

			if (!collectedFileParts.isEmpty()) {
				fileParts = collectedFileParts;
			}

			if (!collectedParts.isEmpty()) {
				parts = collectedParts;
			}
		}
		else if (argumentsJSONObject.has("body")) {
			Object bodyValue = argumentsJSONObject.get("body");

			if (bodyValue instanceof JSONObject) {
				body = bodyValue.toString();
			}
			else if (bodyValue != null) {
				body = String.valueOf(bodyValue);
			}

			if (Validator.isNotNull(body)) {
				contentType = ContentTypes.APPLICATION_JSON;
			}
		}

		return new HttpCallArguments(
			body, contentType, fileParts, operation.method, parts, url);
	}

	public static boolean isMultipartRequest(JSONObject operationJSONObject) {
		JSONObject requestBodyJSONObject = operationJSONObject.getJSONObject(
			"requestBody");

		if (requestBodyJSONObject == null) {
			return false;
		}

		JSONObject contentJSONObject = requestBodyJSONObject.getJSONObject(
			"content");

		if ((contentJSONObject == null) ||
			contentJSONObject.has("application/json")) {

			return false;
		}

		return contentJSONObject.has("multipart/form-data");
	}

	public static JSONObject resolvedBodySchema(
		JSONObject operationJSONObject, JSONObject rootJSONObject) {

		return (JSONObject)_resolveRefs(
			rootJSONObject, _bodySchema(operationJSONObject), new HashSet<>());
	}

	public static class HttpCallArguments {

		public String getBody() {
			return _body;
		}

		public String getContentType() {
			return _contentType;
		}

		public List<Http.FilePart> getFileParts() {
			return _fileParts;
		}

		public String getMethod() {
			return _method;
		}

		public Map<String, String> getParts() {
			return _parts;
		}

		public String getURL() {
			return _url;
		}

		private HttpCallArguments(
			String body, String contentType, List<Http.FilePart> fileParts,
			String method, Map<String, String> parts, String url) {

			_body = body;
			_contentType = contentType;
			_fileParts = fileParts;
			_method = method;
			_parts = parts;
			_url = url;
		}

		private final String _body;
		private final String _contentType;
		private final List<Http.FilePart> _fileParts;
		private final String _method;
		private final Map<String, String> _parts;
		private final String _url;

	}

	public static class Operation {

		public final String method;
		public final JSONObject operationJSONObject;
		public final String path;
		public final JSONArray pathParametersJSONArray;

		private Operation(
			String method, JSONObject operationJSONObject, String path,
			JSONArray pathParametersJSONArray) {

			this.method = method;
			this.operationJSONObject = operationJSONObject;
			this.path = path;
			this.pathParametersJSONArray = pathParametersJSONArray;
		}

	}

	private static void _addMultipartParts(
		JSONObject operationJSONObject, JSONObject propertiesJSONObject,
		JSONArray requiredJSONArray, JSONObject rootJSONObject) {

		JSONObject resolvedSchemaJSONObject = (JSONObject)_resolveRefs(
			rootJSONObject, _bodySchema(operationJSONObject), new HashSet<>());

		JSONObject partPropertiesJSONObject =
			resolvedSchemaJSONObject.getJSONObject("properties");

		if (partPropertiesJSONObject == null) {
			return;
		}

		for (String partName : partPropertiesJSONObject.keySet()) {
			JSONObject partSchemaJSONObject =
				partPropertiesJSONObject.getJSONObject(partName);

			if (_isBinaryPartSchema(partSchemaJSONObject)) {
				propertiesJSONObject.put(
					partName, _binaryPartEnvelope(partSchemaJSONObject));
			}
			else {
				propertiesJSONObject.put(partName, partSchemaJSONObject);
			}
		}

		JSONArray schemaRequiredJSONArray =
			resolvedSchemaJSONObject.getJSONArray("required");

		if (schemaRequiredJSONArray != null) {
			for (int i = 0; i < schemaRequiredJSONArray.length(); i++) {
				requiredJSONArray.put(schemaRequiredJSONArray.getString(i));
			}
		}
	}

	private static void _addParameter(
		JSONObject parameterJSONObject, Set<String> processedParameterNames,
		JSONObject propertiesJSONObject, JSONArray requiredJSONArray) {

		String name = parameterJSONObject.getString("name");

		if (!processedParameterNames.add(name)) {
			return;
		}

		JSONObject schemaJSONObject = parameterJSONObject.getJSONObject(
			"schema");

		JSONObject propertyJSONObject = JSONFactoryUtil.createJSONObject();

		for (String key : schemaJSONObject.keySet()) {
			propertyJSONObject.put(key, schemaJSONObject.get(key));
		}

		if (Objects.equals(name, "fields")) {
			propertyJSONObject.put("description", _FIELDS_DESCRIPTION);
		}
		else if (Objects.equals(name, "nestedFields")) {
			propertyJSONObject.put("description", _NESTED_FIELDS_DESCRIPTION);
		}
		else if (Objects.equals(name, "restrictFields")) {
			propertyJSONObject.put("description", _RESTRICT_FIELDS_DESCRIPTION);
		}
		else if (parameterJSONObject.has("description")) {
			propertyJSONObject.put(
				"description", parameterJSONObject.getString("description"));
		}

		propertiesJSONObject.put(name, propertyJSONObject);

		if (Objects.equals(parameterJSONObject.getString("in"), "path")) {
			requiredJSONArray.put(name);
		}
	}

	private static void _addParameters(
		JSONArray parametersJSONArray, Set<String> processedParameterNames,
		JSONObject propertiesJSONObject, JSONArray requiredJSONArray) {

		if (parametersJSONArray == null) {
			return;
		}

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			_addParameter(
				parametersJSONArray.getJSONObject(i), processedParameterNames,
				propertiesJSONObject, requiredJSONArray);
		}
	}

	private static void _addVulcanFieldSelectionParameters(
		Set<String> processedParameterNames, JSONObject propertiesJSONObject) {

		if (processedParameterNames.add("fields")) {
			propertiesJSONObject.put(
				"fields",
				JSONUtil.put(
					"description", _FIELDS_DESCRIPTION
				).put(
					"type", "string"
				));
		}

		if (processedParameterNames.add("nestedFields")) {
			propertiesJSONObject.put(
				"nestedFields",
				JSONUtil.put(
					"description", _NESTED_FIELDS_DESCRIPTION
				).put(
					"type", "string"
				));
		}

		if (processedParameterNames.add("restrictFields")) {
			propertiesJSONObject.put(
				"restrictFields",
				JSONUtil.put(
					"description", _RESTRICT_FIELDS_DESCRIPTION
				).put(
					"type", "string"
				));
		}
	}

	private static void _appendQueryParameter(
		String name, Object value, StringBundler queryStringSB) {

		if (value == null) {
			return;
		}

		String stringValue = String.valueOf(value);

		if (stringValue.isEmpty()) {
			return;
		}

		if (queryStringSB.length() > 0) {
			queryStringSB.append(CharPool.AMPERSAND);
		}

		queryStringSB.append(URLCodec.encodeURL(name));
		queryStringSB.append(CharPool.EQUAL);
		queryStringSB.append(URLCodec.encodeURL(stringValue));
	}

	private static void _applyMultipartBody(
		JSONObject argumentsJSONObject, List<Http.FilePart> fileParts,
		Map<String, String> parts, JSONObject resolvedSchemaJSONObject) {

		JSONObject propertiesJSONObject =
			resolvedSchemaJSONObject.getJSONObject("properties");

		if (propertiesJSONObject == null) {
			return;
		}

		for (String partName : propertiesJSONObject.keySet()) {
			if (!argumentsJSONObject.has(partName)) {
				continue;
			}

			Object value = argumentsJSONObject.get(partName);

			if (value == null) {
				continue;
			}

			JSONObject partSchemaJSONObject =
				propertiesJSONObject.getJSONObject(partName);

			if (Objects.equals(
					partSchemaJSONObject.getString("format"), "binary")) {

				fileParts.add(_toFilePart(partName, value));
			}
			else {
				parts.put(partName, _toPartString(value));
			}
		}
	}

	private static void _applyParameters(
		JSONObject argumentsJSONObject, Set<String> handledKeys,
		JSONArray parametersJSONArray, String[] pathRef,
		StringBundler queryStringSB) {

		if (parametersJSONArray == null) {
			return;
		}

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			JSONObject parameterJSONObject = parametersJSONArray.getJSONObject(
				i);

			String name = parameterJSONObject.getString("name");

			if (!handledKeys.add(name) || !argumentsJSONObject.has(name)) {
				continue;
			}

			Object value = argumentsJSONObject.get(name);

			if (value == null) {
				continue;
			}

			String stringValue = String.valueOf(value);
			String in = parameterJSONObject.getString("in");

			if (Objects.equals(in, "path")) {
				pathRef[0] = StringUtil.replace(
					pathRef[0], "{" + name + "}",
					URLCodec.encodeURL(stringValue));
			}
			else if (Objects.equals(in, "query")) {
				_appendQueryParameter(name, value, queryStringSB);
			}
		}
	}

	private static JSONObject _binaryPartEnvelope(
		JSONObject partSchemaJSONObject) {

		String description = partSchemaJSONObject.getString("description");

		String envelopeDescription =
			"Provide as an object with `data` (base64-encoded bytes), and " +
				"optional `filename` and `contentType`.";

		if (Validator.isNotNull(description)) {
			envelopeDescription = description + ". " + envelopeDescription;
		}

		return JSONUtil.put(
			"description", envelopeDescription
		).put(
			"properties",
			JSONUtil.put(
				"contentType",
				JSONUtil.put(
					"description", "MIME type of the file content."
				).put(
					"type", "string"
				)
			).put(
				"data",
				JSONUtil.put(
					"description", "Base64-encoded file bytes."
				).put(
					"type", "string"
				)
			).put(
				"filename",
				JSONUtil.put(
					"description", "Original file name."
				).put(
					"type", "string"
				)
			)
		).put(
			"required", JSONUtil.put("data")
		).put(
			"type", "object"
		);
	}

	private static JSONObject _bodySchema(JSONObject operationJSONObject) {
		JSONObject contentJSONObject = operationJSONObject.getJSONObject(
			"requestBody"
		).getJSONObject(
			"content"
		);

		if (contentJSONObject == null) {
			throw new IllegalArgumentException(
				"Request body has no \"content\"");
		}

		JSONObject mediaTypeJSONObject = contentJSONObject.getJSONObject(
			"application/json");

		if (mediaTypeJSONObject == null) {
			for (String mediaType : contentJSONObject.keySet()) {
				mediaTypeJSONObject = contentJSONObject.getJSONObject(
					mediaType);

				if (mediaTypeJSONObject != null) {
					break;
				}
			}
		}

		if (mediaTypeJSONObject == null) {
			throw new IllegalArgumentException("Request body has no content");
		}

		JSONObject schemaJSONObject = mediaTypeJSONObject.getJSONObject(
			"schema");

		if (schemaJSONObject == null) {
			throw new IllegalArgumentException(
				"Request body content has no schema");
		}

		return schemaJSONObject;
	}

	private static Map<String, Object> _buildInputSchema(
		boolean injectVulcanParameters, String method,
		JSONObject operationJSONObject, JSONArray pathParametersJSONArray,
		JSONObject rootJSONObject) {

		JSONObject propertiesJSONObject = JSONFactoryUtil.createJSONObject();
		JSONArray requiredJSONArray = JSONFactoryUtil.createJSONArray();

		if (operationJSONObject.has("requestBody")) {
			if (isMultipartRequest(operationJSONObject)) {
				_addMultipartParts(
					operationJSONObject, propertiesJSONObject,
					requiredJSONArray, rootJSONObject);
			}
			else {
				propertiesJSONObject.put(
					"body",
					_resolveRefs(
						rootJSONObject, _bodySchema(operationJSONObject),
						new HashSet<>()));

				requiredJSONArray.put("body");
			}
		}

		Set<String> processedParameterNames = new HashSet<>();

		_addParameters(
			operationJSONObject.getJSONArray("parameters"),
			processedParameterNames, propertiesJSONObject, requiredJSONArray);
		_addParameters(
			pathParametersJSONArray, processedParameterNames,
			propertiesJSONObject, requiredJSONArray);

		if (injectVulcanParameters && Objects.equals(method, "get")) {
			_addVulcanFieldSelectionParameters(
				processedParameterNames, propertiesJSONObject);
		}

		return _toMap(
			JSONUtil.put(
				"properties", propertiesJSONObject
			).put(
				"required", requiredJSONArray
			).put(
				"type", "object"
			));
	}

	private static String _describeTool(
		String method, JSONObject operationJSONObject, String path) {

		boolean hasDescription = operationJSONObject.has("description");
		boolean hasSummary = operationJSONObject.has("summary");

		if (hasDescription && hasSummary) {
			return operationJSONObject.getString("summary") + ". " +
				operationJSONObject.getString("description");
		}

		if (hasDescription) {
			return operationJSONObject.getString("description");
		}

		if (hasSummary) {
			return operationJSONObject.getString("summary");
		}

		return StringUtil.toUpperCase(method) + StringPool.SPACE + path;
	}

	private static JSONObject _expandDiscriminator(
		JSONObject baseJSONObject, String baseRef, JSONObject rootJSONObject,
		Set<String> visitedRefs) {

		JSONObject componentsJSONObject = rootJSONObject.getJSONObject(
			"components");

		JSONObject schemasJSONObject = (componentsJSONObject != null) ?
			componentsJSONObject.getJSONObject("schemas") : null;

		JSONArray oneOfJSONArray = JSONFactoryUtil.createJSONArray();

		if (schemasJSONObject != null) {
			for (String schemaName : schemasJSONObject.keySet()) {
				JSONObject candidateJSONObject =
					schemasJSONObject.getJSONObject(schemaName);

				JSONArray allOfJSONArray = candidateJSONObject.getJSONArray(
					"allOf");

				if (allOfJSONArray == null) {
					continue;
				}

				boolean extendsBase = false;

				for (int i = 0; i < allOfJSONArray.length(); i++) {
					JSONObject memberJSONObject = allOfJSONArray.getJSONObject(
						i);

					if (memberJSONObject == null) {
						continue;
					}

					if (baseRef.equals(memberJSONObject.getString("$ref"))) {
						extendsBase = true;

						break;
					}
				}

				if (!extendsBase) {
					continue;
				}

				String subtypeRef = "#/components/schemas/" + schemaName;

				if (visitedRefs.contains(subtypeRef)) {
					continue;
				}

				Set<String> subtypeVisitedRefs = new HashSet<>(visitedRefs);

				subtypeVisitedRefs.add(subtypeRef);

				oneOfJSONArray.put(
					_resolveRefs(
						rootJSONObject, candidateJSONObject,
						subtypeVisitedRefs));
			}
		}

		if (oneOfJSONArray.length() == 0) {
			JSONObject resolvedJSONObject = JSONFactoryUtil.createJSONObject();

			for (String key : baseJSONObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				resolvedJSONObject.put(
					key,
					_resolveRefs(
						rootJSONObject, baseJSONObject.get(key), visitedRefs));
			}

			return resolvedJSONObject;
		}

		return JSONUtil.put("oneOf", oneOfJSONArray);
	}

	private static boolean _isBinaryPartSchema(
		JSONObject partSchemaJSONObject) {

		if (partSchemaJSONObject == null) {
			return false;
		}

		return Objects.equals(
			partSchemaJSONObject.getString("format"), "binary");
	}

	private static JSONObject _mergeAllOf(
		JSONObject jsonObject, JSONObject rootJSONObject,
		Set<String> visitedRefs) {

		JSONObject mergedJSONObject = JSONFactoryUtil.createJSONObject();
		JSONObject mergedPropertiesJSONObject =
			JSONFactoryUtil.createJSONObject();
		JSONArray mergedRequiredJSONArray = JSONFactoryUtil.createJSONArray();
		Set<String> mergedRequiredSet = new HashSet<>();

		for (String key : jsonObject.keySet()) {
			if (key.equals("allOf") || _excludedSchemaKeys.contains(key) ||
				key.startsWith("x-")) {

				continue;
			}

			mergedJSONObject.put(
				key,
				_resolveRefs(rootJSONObject, jsonObject.get(key), visitedRefs));
		}

		JSONArray allOfJSONArray = jsonObject.getJSONArray("allOf");

		for (int i = 0; i < allOfJSONArray.length(); i++) {
			Object resolved = _resolveRefs(
				rootJSONObject, allOfJSONArray.getJSONObject(i), visitedRefs);

			if (!(resolved instanceof JSONObject)) {
				continue;
			}

			JSONObject memberJSONObject = (JSONObject)resolved;

			JSONObject memberPropertiesJSONObject =
				memberJSONObject.getJSONObject("properties");

			if (memberPropertiesJSONObject != null) {
				for (String propertyName :
						memberPropertiesJSONObject.keySet()) {

					mergedPropertiesJSONObject.put(
						propertyName,
						memberPropertiesJSONObject.get(propertyName));
				}
			}

			JSONArray memberRequiredJSONArray = memberJSONObject.getJSONArray(
				"required");

			if (memberRequiredJSONArray != null) {
				for (int j = 0; j < memberRequiredJSONArray.length(); j++) {
					String name = memberRequiredJSONArray.getString(j);

					if (mergedRequiredSet.add(name)) {
						mergedRequiredJSONArray.put(name);
					}
				}
			}

			for (String key : memberJSONObject.keySet()) {
				if (mergedJSONObject.has(key) || key.equals("properties") ||
					key.equals("required")) {

					continue;
				}

				mergedJSONObject.put(key, memberJSONObject.get(key));
			}
		}

		if (mergedPropertiesJSONObject.length() > 0) {
			mergedJSONObject.put("properties", mergedPropertiesJSONObject);
		}

		if (mergedRequiredJSONArray.length() > 0) {
			mergedJSONObject.put("required", mergedRequiredJSONArray);
		}

		if (!mergedJSONObject.has("type")) {
			mergedJSONObject.put("type", "object");
		}

		return mergedJSONObject;
	}

	private static JSONObject _resolveRef(
		String ref, JSONObject rootJSONObject) {

		JSONObject currentJSONObject = rootJSONObject;

		for (String part : StringUtil.split(ref.substring(2), CharPool.SLASH)) {
			currentJSONObject = currentJSONObject.getJSONObject(part);
		}

		return currentJSONObject;
	}

	private static Object _resolveRefs(
		JSONObject rootJSONObject, Object value, Set<String> visitedRefs) {

		if (value instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)value;

			if (jsonObject.has("$ref")) {
				String ref = jsonObject.getString("$ref");

				if (visitedRefs.contains(ref)) {
					return JSONUtil.put("type", "object");
				}

				JSONObject referencedJSONObject = _resolveRef(
					ref, rootJSONObject);

				Set<String> currentVisitedRefs = new HashSet<>(visitedRefs);

				currentVisitedRefs.add(ref);

				if (referencedJSONObject.has("discriminator")) {
					return _expandDiscriminator(
						referencedJSONObject, ref, rootJSONObject,
						currentVisitedRefs);
				}

				return _resolveRefs(
					rootJSONObject, referencedJSONObject, currentVisitedRefs);
			}

			if (jsonObject.has("allOf")) {
				return _mergeAllOf(jsonObject, rootJSONObject, visitedRefs);
			}

			JSONObject resolvedJSONObject = JSONFactoryUtil.createJSONObject();

			for (String key : jsonObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				resolvedJSONObject.put(
					key,
					_resolveRefs(
						rootJSONObject, jsonObject.get(key), visitedRefs));
			}

			return resolvedJSONObject;
		}

		if (value instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)value;

			JSONArray resolvedJSONArray = JSONFactoryUtil.createJSONArray();

			for (int i = 0; i < jsonArray.length(); i++) {
				resolvedJSONArray.put(
					_resolveRefs(
						rootJSONObject, jsonArray.get(i), visitedRefs));
			}

			return resolvedJSONArray;
		}

		return value;
	}

	private static Http.FilePart _toFilePart(String name, Object value) {
		String fileName = name;
		String contentType = ContentTypes.APPLICATION_OCTET_STREAM;
		String base64Data;

		JSONObject envelopeJSONObject = null;

		if (value instanceof JSONObject) {
			envelopeJSONObject = (JSONObject)value;
		}
		else if (value instanceof Map) {
			envelopeJSONObject = JSONFactoryUtil.createJSONObject(
				(Map<String, ?>)value);
		}

		if (envelopeJSONObject != null) {
			if (envelopeJSONObject.has("filename")) {
				fileName = envelopeJSONObject.getString("filename");
			}

			if (envelopeJSONObject.has("contentType")) {
				contentType = envelopeJSONObject.getString("contentType");
			}

			base64Data = envelopeJSONObject.getString("data");
		}
		else {
			base64Data = String.valueOf(value);
		}

		if (Validator.isNull(base64Data)) {
			throw new IllegalArgumentException(
				"Multipart part \"" + name + "\" has no data");
		}

		byte[] bytes;

		try {
			bytes = Base64.getDecoder(
			).decode(
				base64Data
			);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException(
				"Multipart part \"" + name + "\" data is not valid base64",
				illegalArgumentException);
		}

		return new Http.FilePart(
			name, fileName, bytes, contentType, StringPool.UTF8);
	}

	private static List<Object> _toList(JSONArray jsonArray) {
		List<Object> list = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			Object value = jsonArray.get(i);

			if (value instanceof JSONObject) {
				list.add(_toMap((JSONObject)value));
			}
			else if (value instanceof JSONArray) {
				list.add(_toList((JSONArray)value));
			}
			else {
				list.add(value);
			}
		}

		return list;
	}

	private static Map<String, Object> _toMap(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);

			if (value instanceof JSONObject) {
				map.put(key, _toMap((JSONObject)value));
			}
			else if (value instanceof JSONArray) {
				map.put(key, _toList((JSONArray)value));
			}
			else {
				map.put(key, value);
			}
		}

		return map;
	}

	private static String _toPartString(Object value) {
		if (value instanceof JSONArray || value instanceof JSONObject) {
			return value.toString();
		}

		if (value instanceof Map) {
			return JSONFactoryUtil.createJSONObject(
				(Map<String, ?>)value
			).toString();
		}

		if (value instanceof List) {
			return JSONFactoryUtil.createJSONArray(
				(List<?>)value
			).toString();
		}

		return String.valueOf(value);
	}

	private static String _withActions(Object value) {
		String stringValue = (value == null) ? "" : String.valueOf(value);

		if (stringValue.isEmpty()) {
			return "actions";
		}

		for (String existing : StringUtil.split(stringValue, CharPool.COMMA)) {
			if (Objects.equals(existing, "actions")) {
				return stringValue;
			}
		}

		return stringValue + ",actions";
	}

	private static final String _FIELDS_DESCRIPTION =
		"Comma-separated list of fields to include in the response. Pass " +
			"only the fields the user actually needs to save tokens.";

	private static final String[] _METHODS = {
		"delete", "get", "head", "options", "patch", "post", "put"
	};

	private static final String _MULTIPART_CONTENT_TYPE = "multipart/form-data";

	private static final String _NESTED_FIELDS_DESCRIPTION =
		"Comma-separated list of nested relationships to expand in the " +
			"response.";

	private static final String _RESTRICT_FIELDS_DESCRIPTION =
		"Comma-separated list of fields to exclude from the response.";

	private static final String[] _VULCAN_QUERY_PARAMETERS = {
		"fields", "nestedFields", "restrictFields"
	};

	private static final Set<String> _excludedSchemaKeys = Set.of(
		"actions", "example", "exclusiveMaximum", "exclusiveMinimum", "xml");

}
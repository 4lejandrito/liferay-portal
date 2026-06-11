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
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtil {

	public static Request getRequest(
		JSONObject inputJSONObject, JSONObject openAPIJSONObject,
		String toolName) {

		Operation operation = _getOperation(openAPIJSONObject, toolName);

		String pathWithQuery = _getPath(inputJSONObject, operation);

		String queryString = _getQueryString(inputJSONObject, operation);

		if (!queryString.isEmpty()) {
			pathWithQuery = pathWithQuery + StringPool.QUESTION + queryString;
		}

		String method = StringUtil.toUpperCase(operation._method);

		if (_isMultipartRequest(operation._operationJSONObject)) {
			String boundary = String.valueOf(UUID.randomUUID());

			byte[] body = _buildMultipartBody(
				boundary, inputJSONObject, openAPIJSONObject, operation);

			if (body == null) {
				return new Request(method, pathWithQuery, null, null);
			}

			return new Request(
				method, pathWithQuery,
				"multipart/form-data; boundary=" + boundary, body);
		}

		String body = _getBody(inputJSONObject);

		if (Validator.isNotNull(body)) {
			return new Request(
				method, pathWithQuery, ContentTypes.APPLICATION_JSON,
				body.getBytes(StandardCharsets.UTF_8));
		}

		return new Request(method, pathWithQuery, null, null);
	}

	public static Tool getTool(JSONObject openAPIJSONObject, String toolName) {
		Operation operation = _getOperation(openAPIJSONObject, toolName);

		return new Tool() {
			{
				setDescription(
					() -> _getDescription(
						operation._method, operation._operationJSONObject,
						operation._path));
				setInputSchema(
					() -> _getInputSchema(
						openAPIJSONObject, operation._operationJSONObject,
						operation._pathParametersJSONArray));

				setName(() -> toolName);
			}
		};
	}

	public static List<ToolSummary> getToolSummaries(
		JSONObject openAPIJSONObject) {

		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

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
					() -> _getDescription(method, operationJSONObject, path));
				toolSummary.setName(
					() -> operationJSONObject.getString("operationId"));

				toolSummaries.add(toolSummary);
			}
		}

		return toolSummaries;
	}

	public static class Request {

		public Request(
			String method, String pathWithQuery, String contentType,
			byte[] body) {

			_method = method;
			_pathWithQuery = pathWithQuery;
			_contentType = contentType;
			_body = body;
		}

		public byte[] getBody() {
			return _body;
		}

		public String getContentType() {
			return _contentType;
		}

		public String getMethod() {
			return _method;
		}

		public String getPathWithQuery() {
			return _pathWithQuery;
		}

		private final byte[] _body;
		private final String _contentType;
		private final String _method;
		private final String _pathWithQuery;

	}

	private static void _addMultipartParts(
		JSONObject operationJSONObject, Map<String, Object> properties,
		List<Object> required, JSONObject openAPIJSONObject) {

		Map<String, Object> resolvedSchema = (Map<String, Object>)_resolveRefs(
			openAPIJSONObject, _getBodySchemaJSONObject(operationJSONObject),
			new HashSet<>());

		Map<String, Object> partProperties =
			(Map<String, Object>)resolvedSchema.get("properties");

		if (partProperties == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : partProperties.entrySet()) {
			String partName = entry.getKey();
			Map<String, Object> partSchema =
				(Map<String, Object>)entry.getValue();

			if (_isBinaryPartSchema(partSchema)) {
				properties.put(partName, _buildBinaryPartEnvelope(partSchema));
			}
			else {
				properties.put(partName, partSchema);
			}
		}

		List<Object> schemaRequired = (List<Object>)resolvedSchema.get(
			"required");

		if (schemaRequired != null) {
			required.addAll(schemaRequired);
		}
	}

	private static void _addParameter(
		JSONObject parameterJSONObject, Set<String> processedParameterNames,
		Map<String, Object> properties, List<Object> required) {

		String name = parameterJSONObject.getString("name");

		if (_vulcanFieldSelectionParameterNames.contains(name) ||
			!processedParameterNames.add(name)) {

			return;
		}

		Map<String, Object> property = new LinkedHashMap<>();

		JSONObject schemaJSONObject = parameterJSONObject.getJSONObject(
			"schema");

		for (String key : schemaJSONObject.keySet()) {
			property.put(key, schemaJSONObject.get(key));
		}

		if (parameterJSONObject.has("description")) {
			property.put(
				"description", parameterJSONObject.getString("description"));
		}

		properties.put(name, property);

		if (Objects.equals(parameterJSONObject.getString("in"), "path")) {
			required.add(name);
		}
	}

	private static void _addParameters(
		JSONArray parametersJSONArray, Set<String> processedParameterNames,
		Map<String, Object> properties, List<Object> required) {

		if (parametersJSONArray == null) {
			return;
		}

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			_addParameter(
				parametersJSONArray.getJSONObject(i), processedParameterNames,
				properties, required);
		}
	}

	private static void _appendQueryParameter(
		String name, StringBundler queryStringSB, Object value) {

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

	private static Map<String, Object> _buildBinaryPartEnvelope(
		Map<String, Object> partSchema) {

		String description = (String)partSchema.get("description");

		String envelopeDescription =
			"Provide as an object with `data` (base64-encoded bytes), and " +
				"optional `filename` and `contentType`.";

		if (Validator.isNotNull(description)) {
			envelopeDescription = description + ". " + envelopeDescription;
		}

		return HashMapBuilder.<String, Object>put(
			"description", envelopeDescription
		).put(
			"properties",
			HashMapBuilder.<String, Object>put(
				"contentType",
				HashMapBuilder.<String, Object>put(
					"description", "MIME type of the file content."
				).put(
					"type", "string"
				).build()
			).put(
				"data",
				HashMapBuilder.<String, Object>put(
					"description", "Base64-encoded file bytes."
				).put(
					"type", "string"
				).build()
			).put(
				"filename",
				HashMapBuilder.<String, Object>put(
					"description", "Original file name."
				).put(
					"type", "string"
				).build()
			).build()
		).put(
			"required", Arrays.asList("data")
		).put(
			"type", "object"
		).build();
	}

	private static byte[] _buildMultipartBody(
		String boundary, JSONObject inputJSONObject,
		JSONObject openAPIJSONObject, Operation operation) {

		Map<String, Object> bodySchema = (Map<String, Object>)_resolveRefs(
			openAPIJSONObject,
			_getBodySchemaJSONObject(operation._operationJSONObject),
			new HashSet<>());

		Map<String, Object> partProperties =
			(Map<String, Object>)bodySchema.get("properties");

		if (partProperties == null) {
			return null;
		}

		List<FilePart> fileParts = new ArrayList<>();
		Map<String, String> parts = new LinkedHashMap<>();

		for (Map.Entry<String, Object> entry : partProperties.entrySet()) {
			String partName = entry.getKey();

			if (!inputJSONObject.has(partName)) {
				continue;
			}

			Object value = inputJSONObject.get(partName);

			if (value == null) {
				continue;
			}

			Map<String, Object> partSchema =
				(Map<String, Object>)entry.getValue();

			if (Objects.equals(partSchema.get("format"), "binary")) {
				fileParts.add(_getFilePart(partName, value));
			}
			else {
				parts.put(partName, _getPart(value));
			}
		}

		if (fileParts.isEmpty() && parts.isEmpty()) {
			return null;
		}

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		try {
			for (Map.Entry<String, String> entry : parts.entrySet()) {
				_writeASCII(byteArrayOutputStream, "--", boundary, _CRLF);
				_writeASCII(
					byteArrayOutputStream,
					"Content-Disposition: form-data; name=\"", entry.getKey(),
					"\"", _CRLF, "Content-Type: ", ContentTypes.TEXT_PLAIN_UTF8,
					_CRLF, _CRLF);

				byteArrayOutputStream.write(
					entry.getValue(
					).getBytes(
						StandardCharsets.UTF_8
					));

				_writeASCII(byteArrayOutputStream, _CRLF);
			}

			for (FilePart filePart : fileParts) {
				_writeASCII(byteArrayOutputStream, "--", boundary, _CRLF);
				_writeASCII(
					byteArrayOutputStream,
					"Content-Disposition: form-data; name=\"", filePart._name,
					"\"; filename=\"", filePart._fileName, "\"", _CRLF,
					"Content-Type: ", filePart._contentType, _CRLF, _CRLF);

				byteArrayOutputStream.write(filePart._bytes);

				_writeASCII(byteArrayOutputStream, _CRLF);
			}

			_writeASCII(byteArrayOutputStream, "--", boundary, "--", _CRLF);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return byteArrayOutputStream.toByteArray();
	}

	private static Map<String, Object> _collectMatchingParameters(
		JSONObject inputJSONObject, Operation operation, String filterIn) {

		Map<String, Object> matchingParameters = new LinkedHashMap<>();

		for (JSONArray parametersJSONArray :
				Arrays.asList(
					operation._operationJSONObject.getJSONArray("parameters"),
					operation._pathParametersJSONArray)) {

			if (parametersJSONArray == null) {
				continue;
			}

			for (int i = 0; i < parametersJSONArray.length(); i++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.getJSONObject(i);

				if (!Objects.equals(
						parameterJSONObject.getString("in"), filterIn)) {

					continue;
				}

				String name = parameterJSONObject.getString("name");

				if (matchingParameters.containsKey(name) ||
					!inputJSONObject.has(name)) {

					continue;
				}

				Object value = inputJSONObject.get(name);

				if (value == null) {
					continue;
				}

				matchingParameters.put(name, value);
			}
		}

		return matchingParameters;
	}

	private static Map<String, Object> _expandDiscriminator(
		JSONObject baseJSONObject, String baseRef, JSONObject openAPIJSONObject,
		Set<String> visitedRefs) {

		List<Object> oneOfList = new ArrayList<>();

		JSONObject componentsJSONObject = openAPIJSONObject.getJSONObject(
			"components");

		JSONObject schemasJSONObject = (componentsJSONObject != null) ?
			componentsJSONObject.getJSONObject("schemas") : null;

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

				oneOfList.add(
					_resolveRefs(
						openAPIJSONObject, candidateJSONObject,
						subtypeVisitedRefs));
			}
		}

		if (oneOfList.isEmpty()) {
			Map<String, Object> resolvedMap = new LinkedHashMap<>();

			for (String key : baseJSONObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				resolvedMap.put(
					key,
					_resolveRefs(
						openAPIJSONObject, baseJSONObject.get(key),
						visitedRefs));
			}

			return resolvedMap;
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"oneOf", oneOfList
		).build();
	}

	private static String _getBody(JSONObject jsonObject) {
		Object body = jsonObject.get("body");

		if (body instanceof JSONObject) {
			return body.toString();
		}
		else if (body != null) {
			return String.valueOf(body);
		}

		return null;
	}

	private static JSONObject _getBodySchemaJSONObject(
		JSONObject operationJSONObject) {

		JSONObject requestBodyJSONObject = operationJSONObject.getJSONObject(
			"requestBody");

		JSONObject contentJSONObject = requestBodyJSONObject.getJSONObject(
			"content");

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
				"Request body content has no \"schema\"");
		}

		return schemaJSONObject;
	}

	private static String _getDescription(
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

	private static FilePart _getFilePart(String name, Object value) {
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
				"Multipart part \"" + name + "\" has no \"data\"");
		}

		Base64.Decoder decoder = Base64.getDecoder();

		return new FilePart(
			name, fileName, contentType, decoder.decode(base64Data));
	}

	private static Map<String, Object> _getInputSchema(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		JSONArray pathParametersJSONArray) {

		Map<String, Object> properties = new LinkedHashMap<>();
		List<Object> required = new ArrayList<>();

		if (operationJSONObject.has("requestBody")) {
			if (_isMultipartRequest(operationJSONObject)) {
				_addMultipartParts(
					operationJSONObject, properties, required,
					openAPIJSONObject);
			}
			else {
				properties.put(
					"body",
					_resolveRefs(
						openAPIJSONObject,
						_getBodySchemaJSONObject(operationJSONObject),
						new HashSet<>()));

				required.add("body");
			}
		}

		Set<String> processedParameterNames = new HashSet<>();

		_addParameters(
			operationJSONObject.getJSONArray("parameters"),
			processedParameterNames, properties, required);
		_addParameters(
			pathParametersJSONArray, processedParameterNames, properties,
			required);

		return LinkedHashMapBuilder.<String, Object>put(
			"properties", properties
		).put(
			"required", required
		).put(
			"type", "object"
		).build();
	}

	private static Operation _getOperation(
		JSONObject openAPIJSONObject, String toolName) {

		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

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
					!toolName.equals(
						operationJSONObject.getString("operationId"))) {

					continue;
				}

				return new Operation(
					method, operationJSONObject, path,
					pathItemJSONObject.getJSONArray("parameters"));
			}
		}

		throw new IllegalArgumentException(
			"OpenAPI document has no tool with name \"" + toolName + "\"");
	}

	private static String _getPart(Object value) {
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

	private static String _getPath(
		JSONObject inputJSONObject, Operation operation) {

		Map<String, Object> pathParameters = _collectMatchingParameters(
			inputJSONObject, operation, "path");

		String path = operation._path;

		for (Map.Entry<String, Object> entry : pathParameters.entrySet()) {
			path = StringUtil.replace(
				path, "{" + entry.getKey() + "}",
				URLCodec.encodeURL(String.valueOf(entry.getValue())));
		}

		return path;
	}

	private static String _getQueryString(
		JSONObject inputJSONObject, Operation operation) {

		StringBundler sb = new StringBundler();

		Map<String, Object> queryParameters = _collectMatchingParameters(
			inputJSONObject, operation, "query");

		for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
			String name = entry.getKey();

			if (_vulcanFieldSelectionParameterNames.contains(name)) {
				continue;
			}

			_appendQueryParameter(name, sb, entry.getValue());
		}

		if (Objects.equals(operation._method, "get")) {
			_appendQueryParameter("restrictFields", sb, "actions");
		}

		return sb.toString();
	}

	private static boolean _isBinaryPartSchema(Map<String, Object> partSchema) {
		if (partSchema == null) {
			return false;
		}

		return Objects.equals(partSchema.get("format"), "binary");
	}

	private static boolean _isMultipartRequest(JSONObject operationJSONObject) {
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

	private static Map<String, Object> _mergeAllOf(
		JSONObject jsonObject, JSONObject openAPIJSONObject,
		Set<String> visitedRefs) {

		Map<String, Object> mergedMap = new LinkedHashMap<>();
		Map<String, Object> mergedProperties = new LinkedHashMap<>();
		List<Object> mergedRequired = new ArrayList<>();
		Set<String> mergedRequiredSet = new HashSet<>();

		for (String key : jsonObject.keySet()) {
			if (key.equals("allOf") || _excludedSchemaKeys.contains(key) ||
				key.startsWith("x-")) {

				continue;
			}

			mergedMap.put(
				key,
				_resolveRefs(
					openAPIJSONObject, jsonObject.get(key), visitedRefs));
		}

		JSONArray allOfJSONArray = jsonObject.getJSONArray("allOf");

		for (int i = 0; i < allOfJSONArray.length(); i++) {
			Object resolvedObject = _resolveRefs(
				openAPIJSONObject, allOfJSONArray.getJSONObject(i),
				visitedRefs);

			if (!(resolvedObject instanceof Map)) {
				continue;
			}

			Map<String, Object> memberMap = (Map<String, Object>)resolvedObject;

			Map<String, Object> memberProperties =
				(Map<String, Object>)memberMap.get("properties");

			if (memberProperties != null) {
				mergedProperties.putAll(memberProperties);
			}

			List<Object> memberRequired = (List<Object>)memberMap.get(
				"required");

			if (memberRequired != null) {
				for (Object name : memberRequired) {
					if (mergedRequiredSet.add(String.valueOf(name))) {
						mergedRequired.add(name);
					}
				}
			}

			for (Map.Entry<String, Object> entry : memberMap.entrySet()) {
				String key = entry.getKey();

				if (mergedMap.containsKey(key) || key.equals("properties") ||
					key.equals("required")) {

					continue;
				}

				mergedMap.put(key, entry.getValue());
			}
		}

		if (!mergedProperties.isEmpty()) {
			mergedMap.put("properties", mergedProperties);
		}

		if (!mergedRequired.isEmpty()) {
			mergedMap.put("required", mergedRequired);
		}

		if (!mergedMap.containsKey("type")) {
			mergedMap.put("type", "object");
		}

		return mergedMap;
	}

	private static JSONObject _resolveRef(
		String ref, JSONObject openAPIJSONObject) {

		JSONObject currentJSONObject = openAPIJSONObject;

		for (String part : StringUtil.split(ref.substring(2), CharPool.SLASH)) {
			currentJSONObject = currentJSONObject.getJSONObject(part);
		}

		return currentJSONObject;
	}

	private static Object _resolveRefs(
		JSONObject openAPIJSONObject, Object value, Set<String> visitedRefs) {

		if (value instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)value;

			if (jsonObject.has("$ref")) {
				String ref = jsonObject.getString("$ref");

				if (visitedRefs.contains(ref)) {
					return HashMapBuilder.<String, Object>put(
						"type", "object"
					).build();
				}

				JSONObject referencedJSONObject = _resolveRef(
					ref, openAPIJSONObject);

				Set<String> currentVisitedRefs = new HashSet<>(visitedRefs);

				currentVisitedRefs.add(ref);

				if (referencedJSONObject.has("discriminator")) {
					return _expandDiscriminator(
						referencedJSONObject, ref, openAPIJSONObject,
						currentVisitedRefs);
				}

				return _resolveRefs(
					openAPIJSONObject, referencedJSONObject,
					currentVisitedRefs);
			}

			if (jsonObject.has("allOf")) {
				return _mergeAllOf(jsonObject, openAPIJSONObject, visitedRefs);
			}

			Map<String, Object> resolvedMap = new LinkedHashMap<>();

			for (String key : jsonObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				resolvedMap.put(
					key,
					_resolveRefs(
						openAPIJSONObject, jsonObject.get(key), visitedRefs));
			}

			return resolvedMap;
		}

		if (value instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)value;

			List<Object> resolvedList = new ArrayList<>();

			for (int i = 0; i < jsonArray.length(); i++) {
				resolvedList.add(
					_resolveRefs(
						openAPIJSONObject, jsonArray.get(i), visitedRefs));
			}

			return resolvedList;
		}

		return value;
	}

	private static void _writeASCII(
			ByteArrayOutputStream byteArrayOutputStream, String... strings)
		throws IOException {

		for (String string : strings) {
			byteArrayOutputStream.write(
				string.getBytes(StandardCharsets.UTF_8));
		}
	}

	private static final String _CRLF = "\r\n";

	private static final String[] _METHODS = {
		"delete", "get", "head", "options", "patch", "post", "put"
	};

	private static final Set<String> _excludedSchemaKeys = Set.of(
		"actions", "example", "exclusiveMaximum", "exclusiveMinimum", "xml");
	private static final Set<String> _vulcanFieldSelectionParameterNames =
		Set.of("fields", "restrictFields");

	private static class FilePart {

		private FilePart(
			String name, String fileName, String contentType, byte[] bytes) {

			_name = name;
			_fileName = fileName;
			_contentType = contentType;
			_bytes = bytes;
		}

		private final byte[] _bytes;
		private final String _contentType;
		private final String _fileName;
		private final String _name;

	}

	private static class Operation {

		private Operation(
			String method, JSONObject operationJSONObject, String path,
			JSONArray pathParametersJSONArray) {

			_method = method;
			_operationJSONObject = operationJSONObject;
			_path = path;
			_pathParametersJSONArray = pathParametersJSONArray;
		}

		private final String _method;
		private final JSONObject _operationJSONObject;
		private final String _path;
		private final JSONArray _pathParametersJSONArray;

	}

}
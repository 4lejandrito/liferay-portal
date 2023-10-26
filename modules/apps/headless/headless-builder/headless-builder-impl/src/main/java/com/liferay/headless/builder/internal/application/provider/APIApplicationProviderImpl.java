/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.application.provider;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.application.provider.APIApplicationProvider;
import com.liferay.headless.builder.internal.helper.ObjectEntryHelper;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 * @author Carlos Correa
 * @author Alejandro Tardín
 */
@Component(service = APIApplicationProvider.class)
public class APIApplicationProviderImpl implements APIApplicationProvider {

	@Override
	public APIApplication fetchAPIApplication(String baseURL, long companyId)
		throws Exception {

		return _toApiApplication(
			_objectEntryHelper.getObjectEntry(
				companyId, "baseURL eq '" + baseURL + "'", "L_API_APPLICATION"),
			companyId);
	}

	@Override
	public List<APIApplication> getPublishedAPIApplications(long companyId)
		throws Exception {

		return TransformUtil.transform(
			_objectEntryHelper.getObjectEntries(
				companyId, "applicationStatus eq 'published'",
				"L_API_APPLICATION"),
			objectEntry -> _toApiApplication(objectEntry, companyId));
	}

	private List<APIApplication.Endpoint> _getEndpoints(
		OpenAPI openAPI, List<APIApplication.Schema> schemas) {

		Paths paths = openAPI.getPaths();

		return TransformUtil.transform(
			paths.entrySet(),
			pathEntry -> {
				PathItem pathItem = pathEntry.getValue();

				Operation getOperation = pathItem.getGet();

				Map<String, Object> extensions = getOperation.getExtensions();

				ApiResponses apiResponses = getOperation.getResponses();

				ApiResponse apiResponse = apiResponses.get("200");

				Content content = apiResponse.getContent();

				MediaType mediaType = content.get("application/json");

				Schema<?> responseSchema = mediaType.getSchema();

				return new APIApplication.Endpoint() {

					@Override
					public APIApplication.Filter getFilter() {
						return () -> (String)extensions.get(
							"x-liferay-odata-filter");
					}

					@Override
					public Http.Method getMethod() {
						return Http.Method.GET;
					}

					@Override
					public String getPath() {
						return pathEntry.getKey();
					}

					@Override
					public String getPathParameter() {
						return (String)extensions.get(
							"x-liferay-path-parameter");
					}

					@Override
					public APIApplication.Schema getRequestSchema() {
						return null;
					}

					@Override
					public APIApplication.Schema getResponseSchema() {
						Schema<?> itemSchema = _getItemSchema(responseSchema);

						return _getSchema(itemSchema.get$ref(), schemas);
					}

					@Override
					public RetrieveType getRetrieveType() {
						if (Objects.equals(responseSchema.getType(), "array")) {
							return RetrieveType.COLLECTION;
						}

						return RetrieveType.SINGLE_ELEMENT;
					}

					@Override
					public Scope getScope() {
						return Scope.parse(
							(String)extensions.get("x-liferay-scope"));
					}

					@Override
					public APIApplication.Sort getSort() {
						return () -> (String)extensions.get(
							"x-liferay-odata-sort");
					}

				};
			});
	}

	private Schema<?> _getItemSchema(Schema<?> schema) {
		if (schema instanceof ArraySchema) {
			ArraySchema arraySchema = (ArraySchema)schema;

			return arraySchema.getItems();
		}

		return schema;
	}

	private List<APIApplication.Property> _getProperties(
		Schema<?> schema, long companyId) {

		Map<String, Object> schemaExtensions = schema.getExtensions();

		Map<String, Schema> properties = schema.getProperties();

		return TransformUtil.transform(
			properties.entrySet(),
			propertyEntry -> {
				String mainObjectDefinitionERC = (String)schemaExtensions.get(
					"x-liferay-object-definition-external-reference-code");

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.
						getObjectDefinitionByExternalReferenceCode(
							mainObjectDefinitionERC, companyId);

				Schema<?> propertySchema = propertyEntry.getValue();

				Map<String, Object> propertySchemaExtensions =
					propertySchema.getExtensions();

				String objectRelationshipNames =
					(String)propertySchemaExtensions.get(
						"x-liferay-object-relationship-names");

				if (!Validator.isBlank(objectRelationshipNames)) {
					objectDefinition =
						_objectEntryHelper.getPropertyObjectDefinition(
							objectDefinition,
							ListUtil.fromArray(
								objectRelationshipNames.split(",")));
				}

				ObjectField objectField =
					_objectFieldLocalService.getObjectField(
						(String)propertySchemaExtensions.get(
							"x-liferay-object-field-external-reference-code"),
						objectDefinition.getObjectDefinitionId());

				return new APIApplication.Property() {

					@Override
					public String getDescription() {
						return propertySchema.getDescription();
					}

					@Override
					public String getName() {
						return propertyEntry.getKey();
					}

					@Override
					public List<String> getObjectRelationshipNames() {
						if (objectRelationshipNames == null) {
							return Collections.emptyList();
						}

						return ListUtil.fromString(
							objectRelationshipNames, ",");
					}

					@Override
					public String getSourceFieldName() {
						return objectField.getName();
					}

					@Override
					public Type getType() {
						Type type = _propertyTypes.get(
							objectField.getBusinessType());

						if (type == null) {
							throw new IllegalStateException(
								"Object field business type " +
									objectField.getBusinessType() +
										" not supported");
						}

						return type;
					}

				};
			});
	}

	private APIApplication.Schema _getSchema(
		String ref, List<APIApplication.Schema> schemas) {

		if (ref == null) {
			return null;
		}

		for (APIApplication.Schema schema : schemas) {
			if (StringUtil.equals(
					schema.getName(),
					StringUtil.removeFirst(ref, "#/components/schemas/"))) {

				return schema;
			}
		}

		throw new IllegalStateException(
			"The schema with ref " + ref + " is not defined");
	}

	private List<APIApplication.Schema> _getSchemas(
		OpenAPI openAPI, long companyId) {

		Components components = openAPI.getComponents();

		Map<String, Schema> schemas = components.getSchemas();

		return TransformUtil.transform(
			schemas.entrySet(),
			schemaEntry -> {
				Schema<?> schema = schemaEntry.getValue();

				List<APIApplication.Property> applicationProperties =
					_getProperties(schema, companyId);

				return new APIApplication.Schema() {

					@Override
					public String getDescription() {
						return schema.getDescription();
					}

					@Override
					public String
						getMainObjectDefinitionExternalReferenceCode() {

						Map<String, Object> extensions = schema.getExtensions();

						return (String)extensions.get(
							"x-liferay-object-definition-external-reference-" +
								"code");
					}

					@Override
					public String getName() {
						return schemaEntry.getKey();
					}

					@Override
					public List<APIApplication.Property> getProperties() {
						return applicationProperties;
					}

				};
			});
	}

	private APIApplication _toApiApplication(
		ObjectEntry apiApplicationObjectEntry, long companyId) {

		if (apiApplicationObjectEntry == null) {
			return null;
		}

		OpenAPIParser openAPIParser = new OpenAPIParser();

		SwaggerParseResult swaggerParseResult = openAPIParser.readContents(
			(String)apiApplicationObjectEntry.getPropertyValue("openAPIJSON"),
			null, null);

		OpenAPI openAPI = swaggerParseResult.getOpenAPI();

		Info info = openAPI.getInfo();

		List<APIApplication.Schema> schemas = _getSchemas(openAPI, companyId);

		return new APIApplication() {

			@Override
			public String getBaseURL() {
				Map<String, Object> properties =
					apiApplicationObjectEntry.getProperties();

				return (String)properties.get("baseURL");
			}

			@Override
			public long getCompanyId() {
				return companyId;
			}

			@Override
			public String getDescription() {
				return info.getDescription();
			}

			@Override
			public List<Endpoint> getEndpoints() {
				return _getEndpoints(openAPI, schemas);
			}

			@Override
			public List<Schema> getSchemas() {
				return schemas;
			}

			@Override
			public String getTitle() {
				return info.getTitle();
			}

			@Override
			public String getVersion() {
				return info.getVersion();
			}

		};
	}

	private static final Map<String, APIApplication.Property.Type>
		_propertyTypes = HashMapBuilder.put(
			ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION,
			APIApplication.Property.Type.AGGREGATION
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			APIApplication.Property.Type.ATTACHMENT
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
			APIApplication.Property.Type.BOOLEAN
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_DATE,
			APIApplication.Property.Type.DATE
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
			APIApplication.Property.Type.DATE_TIME
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
			APIApplication.Property.Type.DECIMAL
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			APIApplication.Property.Type.INTEGER
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
			APIApplication.Property.Type.LONG_INTEGER
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
			APIApplication.Property.Type.LONG_TEXT
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
			APIApplication.Property.Type.MULTISELECT_PICKLIST
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			APIApplication.Property.Type.PICKLIST
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
			APIApplication.Property.Type.PRECISION_DECIMAL
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			APIApplication.Property.Type.RICH_TEXT
		).put(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			APIApplication.Property.Type.TEXT
		).build();

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryHelper _objectEntryHelper;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.headless.builder.internal.generator.operation.handler;

import com.liferay.headless.builder.internal.generator.application.Operation;
import com.liferay.headless.builder.internal.generator.application.Property;
import com.liferay.headless.builder.internal.generator.application.Schema;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author Luis Miguel Barcos
 */
public class GetHTTPOperationHandlerImpl extends HTTPOperationHandler {

	public GetHTTPOperationHandlerImpl(
		AcceptLanguage contextAcceptLanguage, Company contextCompany,
		HttpServletRequest contextHttpServletRequest, UriInfo contextUriInfo,
		User contextUser, DTOConverterRegistry dtoConverterRegistry,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectFieldLocalService objectFieldLocalService) {

		super(
			contextAcceptLanguage, contextCompany, contextHttpServletRequest,
			contextUriInfo, contextUser, dtoConverterRegistry,
			objectDefinitionLocalService, objectFieldLocalService);

		_objectEntryManagerRegistry = objectEntryManagerRegistry;
	}

	@Override
	public Response getResponse(Operation operation) throws Exception {
		Schema responseSchema = operation.getResponse();

		if (responseSchema == null) {
			return Response.ok(
			).build();
		}

		String mainObjectDefinitionERC =
			responseSchema.getMainInternalSchemaIdentifier();

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					mainObjectDefinitionERC, contextCompany.getCompanyId());

		Map<Property, ObjectField> propertyObjectFieldHashMap = new HashMap<>();

		for (Property property : responseSchema.getProperties()) {
			propertyObjectFieldHashMap.put(
				property,
				objectFieldLocalService.fetchObjectField(
					property.getInternalPropertyIdentifier(),
					objectDefinition.getObjectDefinitionId()));
		}

		Page<ObjectEntry> objectEntryPage = _getObjectEntries(
			null, objectDefinition);

		List<Map<String, Object>> schemas = new ArrayList<>();

		for (ObjectEntry item : objectEntryPage.getItems()) {
			Map<String, Object> objectEntryProperties =
				_getAllObjectEntryProperties(item);

			Map<String, Object> schema = new HashMap<>();

			for (Map.Entry<Property, ObjectField> propertyObjectFieldEntry :
					propertyObjectFieldHashMap.entrySet()) {

				Property property = propertyObjectFieldEntry.getKey();
				ObjectField objectField = propertyObjectFieldEntry.getValue();

				schema.put(
					property.getName(),
					objectEntryProperties.get(objectField.getName()));
			}

			schemas.add(schema);
		}

		return Response.ok(
		).entity(
			schemas
		).build();
	}

	private Map<String, Object> _getAllObjectEntryProperties(
		ObjectEntry objectEntry) {

		return HashMapBuilder.<String, Object>putAll(
			objectEntry.getProperties()
		).put(
			"createDate", objectEntry.getDateCreated()
		).put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"modifiedDate", objectEntry.getDateModified()
		).build();
	}

	private Page<ObjectEntry> _getObjectEntries(
			String filter, ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getStorageType());

		return objectEntryManager.getObjectEntries(
			contextCompany.getCompanyId(), objectDefinition, null, null,
			getDTOConverterContext(null), filter,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null, null);
	}

	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;

}
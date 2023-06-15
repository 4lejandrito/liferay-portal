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

package com.liferay.headless.builder.internal.generator.consumer.object.model;

import com.liferay.headless.builder.internal.generator.application.Operation;
import com.liferay.headless.builder.internal.generator.application.Schema;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.pagination.Page;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Luis Miguel Barcos
 */
public class ApiEndpointsObjectModel extends ObjectModel {

	public ApiEndpointsObjectModel(
			long companyId,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryManager objectEntryManager,
			PermissionCheckerFactory permissionCheckerFactory,
			String relatedObjectEntry, UserLocalService userLocalService)
		throws Exception {

		super(
			companyId, objectDefinitionLocalService, objectEntryLocalService,
			objectEntryManager, permissionCheckerFactory, relatedObjectEntry,
			userLocalService);
	}

	public List<Operation> getOperations() {
		return _operations;
	}

	@Override
	protected void initObjectModel() throws Exception {
		Page<ObjectEntry> objectEntriesPage = getObjectEntries(
			String.format(
				"%s/externalReferenceCode eq '%s'",
				_APPLICATION_ENDPOINT_OBJECT_RELATIONSHIP_NAME,
				relatedObjectEntryERC),
			_OBJECT_DEFINITION_ERC);

		Collection<ObjectEntry> items = objectEntriesPage.getItems();

		_operations = new ArrayList<>();

		for (ObjectEntry endpointObjectEntry : items) {
			ListEntry endpointScopeListEntryValue =
				(ListEntry)getObjectEntryPropertyValue(
					endpointObjectEntry, _SCOPE_PROPERTY_NAME);

			ListEntry endpointHTTPMethodListEntryValue =
				(ListEntry)getObjectEntryPropertyValue(
					endpointObjectEntry, _HTTP_METHOD_PROPERTY_NAME);

			_operations.add(
				new Operation(
					endpointHTTPMethodListEntryValue.getKey(),
					(String)getObjectEntryPropertyValue(
						endpointObjectEntry, _PATH_PROPERTY_NAME),
					_getResponse(endpointObjectEntry),
					endpointScopeListEntryValue.getKey()));
		}
	}

	private Schema _getResponse(ObjectEntry endpointObjectEntry)
		throws Exception {

		String responseSchemaERC = (String)getObjectEntryPropertyValue(
			endpointObjectEntry, _RELATED_RESPONSE_SCHEMA_ERC);

		if (responseSchemaERC.isEmpty()) {
			return null;
		}

		ObjectDefinition schemaObjectDefinition = getObjectDefinition(
			_SCHEMA_OBJECT_DEFINITION_ERC);

		com.liferay.object.model.ObjectEntry schemaObjectEntry =
			objectEntryLocalService.getObjectEntry(
				responseSchemaERC,
				schemaObjectDefinition.getObjectDefinitionId());

		Map<String, Serializable> values = schemaObjectEntry.getValues();

		return new Schema(
			(String)values.get(
				_SCHEMA_MAIN_OBJECT_DEFINITION_ERC_PROPERTY_VALUE),
			(String)values.get(_SCHEMA_NAME_PROPERTY_VALUE),
			new ApiPropertyObjectModel(
				companyId, objectDefinitionLocalService,
				objectEntryLocalService, objectEntryManager,
				permissionCheckerFactory,
				schemaObjectEntry.getExternalReferenceCode(), userLocalService
			).getProperties());
	}

	private static final String _APPLICATION_ENDPOINT_OBJECT_RELATIONSHIP_NAME =
		"apiApplicationToAPIEndpoints";

	private static final String _HTTP_METHOD_PROPERTY_NAME = "httpMethod";

	private static final String _OBJECT_DEFINITION_ERC = "MSOD_API_ENDPOINT";

	private static final String _PATH_PROPERTY_NAME = "path";

	private static final String _RELATED_RESPONSE_SCHEMA_ERC =
		"responseAPISchemaToAPIEndpointsERC";

	private static final String
		_SCHEMA_MAIN_OBJECT_DEFINITION_ERC_PROPERTY_VALUE =
			"mainObjectDefinitionERC";

	private static final String _SCHEMA_NAME_PROPERTY_VALUE = "name";

	private static final String _SCHEMA_OBJECT_DEFINITION_ERC =
		"MSOD_API_SCHEMA";

	private static final String _SCOPE_PROPERTY_NAME = "scope";

	private List<Operation> _operations;

}
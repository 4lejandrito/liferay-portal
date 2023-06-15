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

import com.liferay.headless.builder.internal.generator.application.Property;
import com.liferay.headless.builder.internal.generator.application.Schema;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Miguel Barcos
 */
public class ApiSchemasObjectModel extends ObjectModel {

	public ApiSchemasObjectModel(
			long companyId,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryManager objectEntryManager,
			PermissionCheckerFactory permissionCheckerFactory,
			String relatedObjectEntryERC, UserLocalService userLocalService)
		throws Exception {

		super(
			companyId, objectDefinitionLocalService, objectEntryLocalService,
			objectEntryManager, permissionCheckerFactory, relatedObjectEntryERC,
			userLocalService);
	}

	public List<Schema> getSchemas() {
		return _schemas;
	}

	@Override
	protected void initObjectModel() throws Exception {
		Page<ObjectEntry> objectEntriesPage = getObjectEntries(
			String.format(
				"%s/externalReferenceCode eq '%s'",
				_APPLICATION_SCHEMA_OBJECT_RELATIONSHIP_NAME,
				relatedObjectEntryERC),
			_OBJECT_DEFINITION_ERC);

		_schemas = new ArrayList<>();

		for (ObjectEntry schemaObjectEntry : objectEntriesPage.getItems()) {
			_schemas.add(
				new Schema(
					(String)getObjectEntryPropertyValue(
						schemaObjectEntry,
						_MAIN_OBJECT_DEFINITION_ERC_PROPERTY_VALUE),
					(String)getObjectEntryPropertyValue(
						schemaObjectEntry, _NAME_PROPERTY_VALUE),
					_getSchemaProperties(
						schemaObjectEntry.getExternalReferenceCode())));
		}
	}

	private List<Property> _getSchemaProperties(String schemaERC)
		throws Exception {

		return new ApiPropertyObjectModel(
			companyId, objectDefinitionLocalService, objectEntryLocalService,
			objectEntryManager, permissionCheckerFactory, schemaERC,
			userLocalService
		).getProperties();
	}

	private static final String _APPLICATION_SCHEMA_OBJECT_RELATIONSHIP_NAME =
		"apiApplicationToAPISchemas";

	private static final String _MAIN_OBJECT_DEFINITION_ERC_PROPERTY_VALUE =
		"mainObjectDefinitionERC";

	private static final String _NAME_PROPERTY_VALUE = "name";

	private static final String _OBJECT_DEFINITION_ERC = "MSOD_API_SCHEMA";

	private List<Schema> _schemas;

}
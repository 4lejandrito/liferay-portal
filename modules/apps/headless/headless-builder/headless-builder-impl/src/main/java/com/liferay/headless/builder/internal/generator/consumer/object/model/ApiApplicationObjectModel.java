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
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.List;

/**
 * @author Luis Miguel Barcos
 */
public class ApiApplicationObjectModel extends ObjectModel {

	public ApiApplicationObjectModel(
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

	public String getBaseURL() {
		return _baseURL;
	}

	public List<Operation> getOperations() {
		return _operations;
	}

	public List<Schema> getSchemas() {
		return _schemas;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	protected void initObjectModel() throws Exception {
		Page<ObjectEntry> objectEntriesPage = getObjectEntries(
			"externalReferenceCode eq '" + relatedObjectEntryERC + "'",
			_OBJECT_DEFINITION_ERC);

		ObjectEntry objectEntry = objectEntriesPage.fetchFirstItem();

		_baseURL = (String)getObjectEntryPropertyValue(
			objectEntry, _BASE_URL_PROPERTY_NAME);

		_operations = new ApiEndpointsObjectModel(
			companyId, objectDefinitionLocalService, objectEntryLocalService,
			objectEntryManager, permissionCheckerFactory, relatedObjectEntryERC,
			userLocalService
		).getOperations();

		_schemas = new ApiSchemasObjectModel(
			companyId, objectDefinitionLocalService, objectEntryLocalService,
			objectEntryManager, permissionCheckerFactory, relatedObjectEntryERC,
			userLocalService
		).getSchemas();

		_title = (String)getObjectEntryPropertyValue(
			objectEntry, _TITLE_PROPERTY_NAME);
	}

	private static final String _BASE_URL_PROPERTY_NAME = "baseURL";

	private static final String _OBJECT_DEFINITION_ERC = "MSOD_API_APPLICATION";

	private static final String _TITLE_PROPERTY_NAME = "title";

	private String _baseURL;
	private List<Operation> _operations;
	private List<Schema> _schemas;
	private String _title;

}
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

import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Map;

/**
 * @author Luis Miguel Barcos
 */
public abstract class ObjectModel {

	public ObjectModel(
			long companyId,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryManager objectEntryManager,
			PermissionCheckerFactory permissionCheckerFactory,
			String relatedObjectEntryERC, UserLocalService userLocalService)
		throws Exception {

		this.companyId = companyId;
		this.objectDefinitionLocalService = objectDefinitionLocalService;
		this.objectEntryLocalService = objectEntryLocalService;
		this.objectEntryManager = objectEntryManager;
		this.permissionCheckerFactory = permissionCheckerFactory;
		this.relatedObjectEntryERC = relatedObjectEntryERC;
		this.userLocalService = userLocalService;

		initObjectModel();
	}

	public long getCompanyId() {
		return companyId;
	}

	protected DefaultDTOConverterContext getDefaultDTOConverterContext()
		throws Exception {

		return new DefaultDTOConverterContext(
			false, null, null, null, null, LocaleUtil.getSiteDefault(), null,
			_getUser());
	}

	protected ObjectDefinition getObjectDefinition(String objectDefinitionERC)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionERC, companyId);

		if (objectDefinition == null) {
			throw new NoSuchObjectDefinitionException();
		}

		return objectDefinition;
	}

	protected Page<ObjectEntry> getObjectEntries(
			String filter, String objectDefinitionERC)
		throws Exception {

		PermissionThreadLocal.setPermissionChecker(
			permissionCheckerFactory.create(_getUser()));

		return objectEntryManager.getObjectEntries(
			companyId, getObjectDefinition(objectDefinitionERC), null, null,
			getDefaultDTOConverterContext(), filter,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null, null);
	}

	protected Object getObjectEntryPropertyValue(
		ObjectEntry objectEntry, String propertyName) {

		Map<String, Object> properties = objectEntry.getProperties();

		return properties.get(propertyName);
	}

	protected abstract void initObjectModel() throws Exception;

	protected final long companyId;
	protected final ObjectDefinitionLocalService objectDefinitionLocalService;
	protected final ObjectEntryLocalService objectEntryLocalService;
	protected final ObjectEntryManager objectEntryManager;
	protected final PermissionCheckerFactory permissionCheckerFactory;
	protected final String relatedObjectEntryERC;
	protected final UserLocalService userLocalService;

	private User _getUser() throws Exception {
		if (_user == null) {
			com.liferay.object.model.ObjectEntry objectEntry =
				objectEntryLocalService.getObjectEntry(
					relatedObjectEntryERC, CompanyThreadLocal.getCompanyId(),
					GroupThreadLocal.getGroupId());

			_user = userLocalService.getUser(objectEntry.getUserId());
		}

		return _user;
	}

	private static User _user;

}
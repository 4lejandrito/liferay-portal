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

package com.liferay.headless.builder.internal.generator.consumer.object;

import com.liferay.headless.builder.internal.generator.application.ApiApplication;
import com.liferay.headless.builder.internal.generator.consumer.Consumer;
import com.liferay.headless.builder.internal.generator.consumer.object.model.ApiApplicationObjectModel;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(service = Consumer.class)
public class ObjectsConsumerImpl implements Consumer {

	@Override
	public ApiApplication getApiApplication(String apiApplicationERC)
		throws Exception {

		ApiApplicationObjectModel apiApplicationObjectModel =
			new ApiApplicationObjectModel(
				CompanyThreadLocal.getCompanyId(),
				_objectDefinitionLocalService, _objectEntryLocalService,
				_objectEntryManager, _permissionCheckerFactory,
				apiApplicationERC, _userLocalService);

		return new ApiApplication(
			apiApplicationObjectModel.getBaseURL(),
			apiApplicationObjectModel.getCompanyId(),
			apiApplicationObjectModel.getOperations(),
			apiApplicationObjectModel.getSchemas(),
			apiApplicationObjectModel.getTitle());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}
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

package com.liferay.document.library.internal.security.permission.resource;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	service = DLFileEntryDynamicModelResourcePermissionLogic.class
)
public class DLFileEntryDynamicModelResourcePermissionLogic
	implements ModelResourcePermissionLogic<DLFileEntry> {

	@Override
	public Boolean contains(
			PermissionChecker permissionChecker, String name,
			DLFileEntry dlFileEntry, String actionId)
		throws PortalException {

		for (ModelResourcePermissionLogic modelResourcePermissionLogic :
				_serviceTrackerList) {

			Boolean contains = modelResourcePermissionLogic.contains(
				permissionChecker, name, dlFileEntry, actionId);

			if (contains != null) {
				return contains;
			}
		}

		return null;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ModelResourcePermissionLogic.class,
			"(model.class.name=" + DLFileEntry.class.getName() + ")");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList
		<ModelResourcePermissionLogic, ModelResourcePermissionLogic>
			_serviceTrackerList;

}
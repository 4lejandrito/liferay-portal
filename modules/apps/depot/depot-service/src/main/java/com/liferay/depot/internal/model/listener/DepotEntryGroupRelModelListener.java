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

package com.liferay.depot.internal.model.listener;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia Garcia
 */
@Component(immediate = true, service = ModelListener.class)
public class DepotEntryGroupRelModelListener
	extends BaseModelListener<DepotEntryGroupRel> {

	@Override
	public void onBeforeRemove(DepotEntryGroupRel depotEntryGroupRel)
		throws ModelListenerException {

		super.onBeforeRemove(depotEntryGroupRel);

		Group group = _groupLocalService.fetchGroup(
			depotEntryGroupRel.getToGroupId());

		if (group.getLiveGroup() != null) {
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntryGroupRel.isDdmStructuresAvailable(),
				depotEntryGroupRel.getDepotEntryId(), group.getLiveGroupId(),
				depotEntryGroupRel.isSearchable());
		}
	}

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}
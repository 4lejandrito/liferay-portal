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

package com.liferay.depot.internal.exportimport.staged.model.repository;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryHelper;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.depot.model.DepotEntryGroupRel",
	service = StagedModelRepository.class
)
public class DepotEntryGroupRelStagedModelRepository
	implements StagedModelRepository<DepotEntryGroupRel> {

	@Override
	public DepotEntryGroupRel addStagedModel(
			PortletDataContext portletDataContext,
			DepotEntryGroupRel depotEntryGroupRel)
		throws PortalException {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			depotEntryGroupRel);

		if (portletDataContext.isDataStrategyMirror()) {
			serviceContext.setUuid(depotEntryGroupRel.getUuid());
		}

		long toGroupId = depotEntryGroupRel.getToGroupId();

		DepotEntry depotEntry = _depotEntryLocalService.fetchDepotEntry(
			depotEntryGroupRel.getDepotEntryId());

		Group depotEntryGroup = depotEntry.getGroup();

		long depotEntryId = _getDepotEntryId(
			depotEntryGroupRel.getDepotEntryId(), depotEntryGroup);

		Group group = _groupLocalService.fetchGroup(toGroupId);

		Group liveGroup = group.getLiveGroup();

		if (liveGroup != null) {
			if (depotEntryGroup.isStaged()) {
				DepotEntryGroupRel liveDepotEntryGroupRel =
					_depotEntryGroupRelLocalService.
						fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
							depotEntryId, liveGroup.getGroupId());

				if (liveDepotEntryGroupRel != null) {
					_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
						liveDepotEntryGroupRel);
				}
			}
			else {
				toGroupId = liveGroup.getGroupId();
			}
		}

		return _depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntryGroupRel.isDdmStructuresAvailable(), depotEntryId,
			toGroupId, depotEntryGroupRel.isSearchable());
	}

	@Override
	public void deleteStagedModel(DepotEntryGroupRel depotEntryGroupRel) {
		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	@Override
	public void deleteStagedModel(
		String uuid, long groupId, String className, String extraData) {

		DepotEntryGroupRel depotEntryGroupRel =
			fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (depotEntryGroupRel != null) {
			deleteStagedModel(depotEntryGroupRel);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DepotEntryGroupRel fetchMissingReference(String uuid, long groupId) {
		return _stagedModelRepositoryHelper.fetchMissingReference(
			uuid, groupId, this);
	}

	@Override
	public DepotEntryGroupRel fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _depotEntryGroupRelLocalService.
			fetchDepotEntryGroupRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<DepotEntryGroupRel> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _depotEntryGroupRelLocalService.
			getDepotEntryGroupRelsByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext) {

		return _depotEntryGroupRelLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public DepotEntryGroupRel getStagedModel(long depotEntryGroupRelId)
		throws PortalException {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	@Override
	public DepotEntryGroupRel saveStagedModel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return _depotEntryGroupRelLocalService.updateDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel updateStagedModel(
		PortletDataContext portletDataContext,
		DepotEntryGroupRel depotEntryGroupRel) {

		return _depotEntryGroupRelLocalService.updateDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	private long _getDepotEntryId(long depotEntryId, Group depotEntryGroup) {
		if (!depotEntryGroup.isStaged() ||
			(depotEntryGroup.getStagingGroup() == null)) {

			return depotEntryId;
		}

		Group depotEntryGroupStagingGroup = depotEntryGroup.getStagingGroup();

		DepotEntry depotEntryStagingGroup =
			_depotEntryLocalService.fetchGroupDepotEntry(
				depotEntryGroupStagingGroup.getGroupId());

		return depotEntryStagingGroup.getDepotEntryId();
	}

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private StagedModelRepositoryHelper _stagedModelRepositoryHelper;

}
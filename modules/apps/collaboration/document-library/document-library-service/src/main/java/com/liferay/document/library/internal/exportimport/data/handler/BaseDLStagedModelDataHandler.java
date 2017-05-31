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

package com.liferay.document.library.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.portal.kernel.model.StagedModel;

import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseDLStagedModelDataHandler<T extends StagedModel>
	extends BaseStagedModelDataHandler<T> {

	@Override
	public void exportStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (_shouldExport(portletDataContext)) {
			super.exportStagedModel(portletDataContext, stagedModel);
		}
	}

	private boolean _shouldExport(PortletDataContext portletDataContext) {
		try {
			Map<String, Boolean> exportPortletControlsMap =
				ExportImportHelperUtil.getExportPortletControlsMap(
					portletDataContext.getCompanyId(),
					"com_liferay_document_library_web_portlet_DLAdminPortlet",
					portletDataContext.getParameterMap(),
					portletDataContext.getType());

			return exportPortletControlsMap.get(
				PortletDataHandlerKeys.PORTLET_DATA);
		}
		catch (Exception e) {
			return true;
		}
	}

}
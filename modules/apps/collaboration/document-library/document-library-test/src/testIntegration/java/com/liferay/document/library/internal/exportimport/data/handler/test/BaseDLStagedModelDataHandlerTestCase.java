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

package com.liferay.document.library.internal.exportimport.data.handler.test;

import com.liferay.document.library.web.constants.DLPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.lar.test.BaseStagedModelDataHandlerTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseDLStagedModelDataHandlerTestCase
	extends BaseStagedModelDataHandlerTestCase {

	@Test
	public void testDoesNotExportIfTheDLAdminPortletIsNotExported()
		throws Exception {

		StagedModel stagedModel = addStagedModel(
			stagingGroup, addDependentStagedModelsMap(stagingGroup));

		initExport();

		portletDataContext.getParameterMap().put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL, new String[] {"false"});

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		initImport();

		Assert.assertNull(readExportedStagedModel(stagedModel));
	}

	@Test
	public void testExportsIfTheDLAdminPortletIsExported() throws Exception {
		StagedModel stagedModel = addStagedModel(
			stagingGroup, addDependentStagedModelsMap(stagingGroup));

		initExport();

		portletDataContext.getParameterMap().put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL, new String[] {"false"});

		portletDataContext.getParameterMap().put(
			PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
			new String[] {"true"});

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		initImport();

		Assert.assertNotNull(readExportedStagedModel(stagedModel));
	}

}
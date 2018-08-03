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

package com.liferay.frontend.editor.configuration.web.internal.portlet.action;

import com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalService;
import com.liferay.frontend.editor.configuration.web.internal.constants.EditorConfigurationPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + EditorConfigurationPortletKeys.EDITOR_CONFIGURATION,
		"mvc.command.name=/editor_configuration/edit"
	},
	service = MVCActionCommand.class
)
public class EditEditorConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String portletName = ParamUtil.getString(actionRequest, "portletName");
		String editorName = ParamUtil.getString(actionRequest, "editorName");
		String editorConfigKey = ParamUtil.getString(
			actionRequest, "editorConfigKey");
		String customConfiguration = ParamUtil.getString(
			actionRequest, "customConfiguration");

		boolean useCustomConfiguration = ParamUtil.getBoolean(
			actionRequest, "useCustomConfiguration");

		_editorConfigurationEntryLocalService.updateEditorConfigurationEntry(
			portletName, editorName, editorConfigKey, useCustomConfiguration,
			customConfiguration);
	}

	@Reference
	private EditorConfigurationEntryLocalService
		_editorConfigurationEntryLocalService;

}
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

import com.liferay.frontend.editor.configuration.web.internal.constants.EditorConfigurationPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.PortletLocalService;

import java.util.Arrays;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + EditorConfigurationPortletKeys.EDITOR_CONFIGURATION,
		"mvc.command.name=/", "mvc.command.name=/editor_configuration/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			"editorNames", Arrays.asList("alloyeditor", "ckeditor"));

		renderRequest.setAttribute(
			"portlets", _portletLocalService.getPortlets());

		return "/frontend_configuration/view.jsp";
	}

	@Reference
	private PortletLocalService _portletLocalService;

}
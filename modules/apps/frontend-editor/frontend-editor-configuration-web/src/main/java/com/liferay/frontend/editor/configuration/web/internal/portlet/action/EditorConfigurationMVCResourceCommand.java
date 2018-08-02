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
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + EditorConfigurationPortletKeys.EDITOR_CONFIGURATION,
		"mvc.command.name=/editor_configuration/configuration"
	},
	service = MVCResourceCommand.class
)
public class EditorConfigurationMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String portletName = ParamUtil.getString(
			resourceRequest, "portletName");
		String editorName = ParamUtil.getString(resourceRequest, "editorName");
		String editorConfigKey = ParamUtil.getString(
			resourceRequest, "editorConfigKey");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<String, Object> attributes = new HashMap<>();

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				portletName, editorConfigKey, editorName, attributes,
				themeDisplay,
				_getRequestBackedPortletURLFactory(resourceRequest));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			editorConfiguration.getConfigJSONObject());
	}

	private RequestBackedPortletURLFactory _getRequestBackedPortletURLFactory(
		ResourceRequest resourceRequest) {

		PortletRequest portletRequest =
			(PortletRequest)resourceRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest == null) {
			return RequestBackedPortletURLFactoryUtil.create(resourceRequest);
		}

		return RequestBackedPortletURLFactoryUtil.create(portletRequest);
	}

}
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

package com.liferay.frontend.editor.configuration.web.internal.editor.configuration;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.editor.configuration.EditorConfigTransformer;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactory;
import com.liferay.portal.kernel.editor.configuration.EditorOptions;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	service = {
		EditorConfigurationFactory.class,
		EditorConfigurationFactoryExtended.class
	}
)
public class EditorConfigurationFactoryImpl
	implements EditorConfigurationFactoryExtended {

	@Activate
	public void activate(BundleContext bundleContext) {
		_editorConfigTransformerServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, EditorConfigTransformer.class, "editor.name");
	}

	@Override
	public EditorConfiguration getEditorConfiguration(
		String portletName, String editorConfigKey, String editorName,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return getEditorConfiguration(
			portletName, editorConfigKey, editorName,
			inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory, true);
	}

	@Override
	public EditorConfiguration getEditorConfiguration(
		String portletName, String editorConfigKey, String editorName,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		boolean useCustomConfiguration) {

		JSONObject configJSONObject = _editorConfigProvider.getConfigJSONObject(
			portletName, editorConfigKey, editorName,
			inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		EditorOptions editorOptions = _editorOptionsProvider.getEditorOptions(
			portletName, editorConfigKey, editorName,
			inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		EditorConfigTransformer editorConfigTransformer =
			_editorConfigTransformerServiceTrackerMap.getService(editorName);

		if (editorConfigTransformer != null) {
			editorConfigTransformer.transform(
				editorOptions, inputEditorTaglibAttributes, configJSONObject,
				themeDisplay, requestBackedPortletURLFactory);
		}

		return new EditorConfigurationImpl(configJSONObject, editorOptions);
	}

	@Reference
	private EditorConfigProvider _editorConfigProvider;

	private ServiceTrackerMap<String, EditorConfigTransformer>
		_editorConfigTransformerServiceTrackerMap;

	@Reference
	private EditorOptionsProvider _editorOptionsProvider;

}
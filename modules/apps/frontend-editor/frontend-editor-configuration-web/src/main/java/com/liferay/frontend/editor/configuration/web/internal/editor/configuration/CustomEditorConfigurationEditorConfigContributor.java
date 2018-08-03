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

import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;
import com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "service.ranking:Integer=99999",
	service = EditorConfigContributor.class
)
public class CustomEditorConfigurationEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		boolean useCustomConfiguration = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:useCustomConfiguration"),
			true);

		if (useCustomConfiguration) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			String portletName = portletDisplay.getPortletName();

			String editorName = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:editorName"));

			String editorConfigKey = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:name"));

			EditorConfigurationEntry editorConfigurationEntry =
				_editorConfigurationEntryLocalService.
					fetchEditorConfigurationEntry(
						portletName, editorName, editorConfigKey);

			if ((editorConfigurationEntry != null) &&
				editorConfigurationEntry.isEnabled()) {

				try {
					JSONObject customJSONObject =
						JSONFactoryUtil.createJSONObject(
							editorConfigurationEntry.getConfiguration());

					for (String key : _getKeys(jsonObject)) {
						jsonObject.remove(key);
					}

					for (String key : _getKeys(customJSONObject)) {
						jsonObject.put(key, customJSONObject.get(key));
					}
				}
				catch (JSONException jsone) {
					_log.error(
						StringBundler.concat(
							"Unable to parse editor configuration for ",
							portletName, ":", editorName, ":", editorConfigKey),
						jsone);
				}
			}
		}
	}

	private List<String> _getKeys(JSONObject jsonObject) {
		List<String> keys = new ArrayList<>();

		Iterator<String> keysIterator = jsonObject.keys();

		keysIterator.forEachRemaining(keys::add);

		return keys;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomEditorConfigurationEditorConfigContributor.class);

	@Reference
	private EditorConfigurationEntryLocalService
		_editorConfigurationEntryLocalService;

}
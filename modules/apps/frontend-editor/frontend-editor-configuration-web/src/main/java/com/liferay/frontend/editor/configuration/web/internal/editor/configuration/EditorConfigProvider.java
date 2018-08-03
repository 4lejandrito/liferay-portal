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
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
@Component(service = EditorConfigProvider.class)
public class EditorConfigProvider
	extends BaseEditorProvider<EditorConfigContributor> {

	@Activate
	public void activate(BundleContext bundleContext) {
		super.activate(EditorConfigContributor.class, bundleContext);
	}

	public JSONObject getConfigJSONObject(
		String portletName, String editorConfigKey, String editorName,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		boolean useCustomConfiguration) {

		if (useCustomConfiguration) {
			EditorConfigurationEntry editorConfigurationEntry =
				_editorConfigurationEntryLocalService.
					fetchEditorConfigurationEntry(
						portletName, editorName, editorConfigKey);

			if ((editorConfigurationEntry != null) &&
				editorConfigurationEntry.isEnabled()) {

				try {
					return JSONFactoryUtil.createJSONObject(
						editorConfigurationEntry.getConfiguration());
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

		JSONObject configJSONObject = JSONFactoryUtil.createJSONObject();

		visitEditorContributors(
			editorConfigContributor ->
				editorConfigContributor.populateConfigJSONObject(
					configJSONObject, inputEditorTaglibAttributes, themeDisplay,
					requestBackedPortletURLFactory),
			portletName, editorConfigKey, editorName);

		return configJSONObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditorConfigProvider.class);

	@Reference
	private EditorConfigurationEntryLocalService
		_editorConfigurationEntryLocalService;

}
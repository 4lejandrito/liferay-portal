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

package com.liferay.frontend.editor.configuration.service.impl;

import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;
import com.liferay.frontend.editor.configuration.service.base.EditorConfigurationEntryLocalServiceBaseImpl;
import com.liferay.petra.string.StringPool;

/**
 * The implementation of the editor configuration entry local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntryLocalServiceBaseImpl
 * @see com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalServiceUtil
 */
public class EditorConfigurationEntryLocalServiceImpl
	extends EditorConfigurationEntryLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalServiceUtil} to access the editor configuration entry local service.
	 */
	@Override
	public EditorConfigurationEntry fetchEditorConfigurationEntry(
		String portletName, String editorName, String editorConfigKey) {

		EditorConfigurationEntry editorConfigurationEntry =
			editorConfigurationEntryPersistence.fetchByP_E_E(
				portletName, editorName, editorConfigKey);

		if (editorConfigurationEntry == null) {
			editorConfigurationEntry =
				editorConfigurationEntryPersistence.fetchByP_E_E(
					portletName, editorName, StringPool.BLANK);
		}

		return editorConfigurationEntry;
	}

	@Override
	public EditorConfigurationEntry updateEditorConfigurationEntry(
		String portletName, String editorName, String editorConfigKey,
		boolean enabled, String configuration) {

		EditorConfigurationEntry editorConfigurationEntry =
			editorConfigurationEntryPersistence.fetchByP_E_E(
				portletName, editorName, editorConfigKey);

		if (editorConfigurationEntry != null) {
			editorConfigurationEntry.setConfiguration(configuration);
			editorConfigurationEntry.setEnabled(enabled);
		}
		else {
			long editorConfigurationEntryId = counterLocalService.increment();

			editorConfigurationEntry =
				editorConfigurationEntryPersistence.create(
					editorConfigurationEntryId);

			editorConfigurationEntry.setPortletName(portletName);
			editorConfigurationEntry.setEditorName(editorName);
			editorConfigurationEntry.setEditorConfigKey(editorConfigKey);
			editorConfigurationEntry.setConfiguration(configuration);
			editorConfigurationEntry.setEnabled(enabled);
		}

		return updateEditorConfigurationEntry(editorConfigurationEntry);
	}

}
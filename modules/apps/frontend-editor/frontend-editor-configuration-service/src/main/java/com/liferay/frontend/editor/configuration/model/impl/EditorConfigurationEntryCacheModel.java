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

package com.liferay.frontend.editor.configuration.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;

import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing EditorConfigurationEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntry
 * @generated
 */
@ProviderType
public class EditorConfigurationEntryCacheModel implements CacheModel<EditorConfigurationEntry>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof EditorConfigurationEntryCacheModel)) {
			return false;
		}

		EditorConfigurationEntryCacheModel editorConfigurationEntryCacheModel = (EditorConfigurationEntryCacheModel)obj;

		if (editorConfigurationEntryId == editorConfigurationEntryCacheModel.editorConfigurationEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, editorConfigurationEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(21);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", editorConfigurationEntryId=");
		sb.append(editorConfigurationEntryId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", portletName=");
		sb.append(portletName);
		sb.append(", editorName=");
		sb.append(editorName);
		sb.append(", editorConfigKey=");
		sb.append(editorConfigKey);
		sb.append(", configuration=");
		sb.append(configuration);
		sb.append(", enabled=");
		sb.append(enabled);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public EditorConfigurationEntry toEntityModel() {
		EditorConfigurationEntryImpl editorConfigurationEntryImpl = new EditorConfigurationEntryImpl();

		if (uuid == null) {
			editorConfigurationEntryImpl.setUuid("");
		}
		else {
			editorConfigurationEntryImpl.setUuid(uuid);
		}

		editorConfigurationEntryImpl.setEditorConfigurationEntryId(editorConfigurationEntryId);
		editorConfigurationEntryImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			editorConfigurationEntryImpl.setCreateDate(null);
		}
		else {
			editorConfigurationEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			editorConfigurationEntryImpl.setModifiedDate(null);
		}
		else {
			editorConfigurationEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (portletName == null) {
			editorConfigurationEntryImpl.setPortletName("");
		}
		else {
			editorConfigurationEntryImpl.setPortletName(portletName);
		}

		if (editorName == null) {
			editorConfigurationEntryImpl.setEditorName("");
		}
		else {
			editorConfigurationEntryImpl.setEditorName(editorName);
		}

		if (editorConfigKey == null) {
			editorConfigurationEntryImpl.setEditorConfigKey("");
		}
		else {
			editorConfigurationEntryImpl.setEditorConfigKey(editorConfigKey);
		}

		if (configuration == null) {
			editorConfigurationEntryImpl.setConfiguration("");
		}
		else {
			editorConfigurationEntryImpl.setConfiguration(configuration);
		}

		editorConfigurationEntryImpl.setEnabled(enabled);

		editorConfigurationEntryImpl.resetOriginalValues();

		return editorConfigurationEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		editorConfigurationEntryId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		portletName = objectInput.readUTF();
		editorName = objectInput.readUTF();
		editorConfigKey = objectInput.readUTF();
		configuration = objectInput.readUTF();

		enabled = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(editorConfigurationEntryId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (portletName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(portletName);
		}

		if (editorName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(editorName);
		}

		if (editorConfigKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(editorConfigKey);
		}

		if (configuration == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(configuration);
		}

		objectOutput.writeBoolean(enabled);
	}

	public String uuid;
	public long editorConfigurationEntryId;
	public long companyId;
	public long createDate;
	public long modifiedDate;
	public String portletName;
	public String editorName;
	public String editorConfigKey;
	public String configuration;
	public boolean enabled;
}
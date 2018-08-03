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

package com.liferay.frontend.editor.configuration.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class EditorConfigurationEntrySoap implements Serializable {
	public static EditorConfigurationEntrySoap toSoapModel(
		EditorConfigurationEntry model) {
		EditorConfigurationEntrySoap soapModel = new EditorConfigurationEntrySoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setEditorConfigurationEntryId(model.getEditorConfigurationEntryId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPortletName(model.getPortletName());
		soapModel.setEditorName(model.getEditorName());
		soapModel.setEditorConfigKey(model.getEditorConfigKey());
		soapModel.setConfiguration(model.getConfiguration());
		soapModel.setEnabled(model.isEnabled());

		return soapModel;
	}

	public static EditorConfigurationEntrySoap[] toSoapModels(
		EditorConfigurationEntry[] models) {
		EditorConfigurationEntrySoap[] soapModels = new EditorConfigurationEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static EditorConfigurationEntrySoap[][] toSoapModels(
		EditorConfigurationEntry[][] models) {
		EditorConfigurationEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new EditorConfigurationEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new EditorConfigurationEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static EditorConfigurationEntrySoap[] toSoapModels(
		List<EditorConfigurationEntry> models) {
		List<EditorConfigurationEntrySoap> soapModels = new ArrayList<EditorConfigurationEntrySoap>(models.size());

		for (EditorConfigurationEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new EditorConfigurationEntrySoap[soapModels.size()]);
	}

	public EditorConfigurationEntrySoap() {
	}

	public long getPrimaryKey() {
		return _editorConfigurationEntryId;
	}

	public void setPrimaryKey(long pk) {
		setEditorConfigurationEntryId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getEditorConfigurationEntryId() {
		return _editorConfigurationEntryId;
	}

	public void setEditorConfigurationEntryId(long editorConfigurationEntryId) {
		_editorConfigurationEntryId = editorConfigurationEntryId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public String getPortletName() {
		return _portletName;
	}

	public void setPortletName(String portletName) {
		_portletName = portletName;
	}

	public String getEditorName() {
		return _editorName;
	}

	public void setEditorName(String editorName) {
		_editorName = editorName;
	}

	public String getEditorConfigKey() {
		return _editorConfigKey;
	}

	public void setEditorConfigKey(String editorConfigKey) {
		_editorConfigKey = editorConfigKey;
	}

	public String getConfiguration() {
		return _configuration;
	}

	public void setConfiguration(String configuration) {
		_configuration = configuration;
	}

	public boolean getEnabled() {
		return _enabled;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	private String _uuid;
	private long _editorConfigurationEntryId;
	private long _companyId;
	private Date _createDate;
	private Date _modifiedDate;
	private String _portletName;
	private String _editorName;
	private String _editorConfigKey;
	private String _configuration;
	private boolean _enabled;
}
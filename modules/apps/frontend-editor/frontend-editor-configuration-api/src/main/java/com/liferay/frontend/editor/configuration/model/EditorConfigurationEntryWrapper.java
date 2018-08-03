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

import com.liferay.expando.kernel.model.ExpandoBridge;

import com.liferay.exportimport.kernel.lar.StagedModelType;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class is a wrapper for {@link EditorConfigurationEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntry
 * @generated
 */
@ProviderType
public class EditorConfigurationEntryWrapper implements EditorConfigurationEntry,
	ModelWrapper<EditorConfigurationEntry> {
	public EditorConfigurationEntryWrapper(
		EditorConfigurationEntry editorConfigurationEntry) {
		_editorConfigurationEntry = editorConfigurationEntry;
	}

	@Override
	public Class<?> getModelClass() {
		return EditorConfigurationEntry.class;
	}

	@Override
	public String getModelClassName() {
		return EditorConfigurationEntry.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("editorConfigurationEntryId",
			getEditorConfigurationEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("portletName", getPortletName());
		attributes.put("editorName", getEditorName());
		attributes.put("editorConfigKey", getEditorConfigKey());
		attributes.put("configuration", getConfiguration());
		attributes.put("enabled", isEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long editorConfigurationEntryId = (Long)attributes.get(
				"editorConfigurationEntryId");

		if (editorConfigurationEntryId != null) {
			setEditorConfigurationEntryId(editorConfigurationEntryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String portletName = (String)attributes.get("portletName");

		if (portletName != null) {
			setPortletName(portletName);
		}

		String editorName = (String)attributes.get("editorName");

		if (editorName != null) {
			setEditorName(editorName);
		}

		String editorConfigKey = (String)attributes.get("editorConfigKey");

		if (editorConfigKey != null) {
			setEditorConfigKey(editorConfigKey);
		}

		String configuration = (String)attributes.get("configuration");

		if (configuration != null) {
			setConfiguration(configuration);
		}

		Boolean enabled = (Boolean)attributes.get("enabled");

		if (enabled != null) {
			setEnabled(enabled);
		}
	}

	@Override
	public Object clone() {
		return new EditorConfigurationEntryWrapper((EditorConfigurationEntry)_editorConfigurationEntry.clone());
	}

	@Override
	public int compareTo(EditorConfigurationEntry editorConfigurationEntry) {
		return _editorConfigurationEntry.compareTo(editorConfigurationEntry);
	}

	/**
	* Returns the company ID of this editor configuration entry.
	*
	* @return the company ID of this editor configuration entry
	*/
	@Override
	public long getCompanyId() {
		return _editorConfigurationEntry.getCompanyId();
	}

	/**
	* Returns the configuration of this editor configuration entry.
	*
	* @return the configuration of this editor configuration entry
	*/
	@Override
	public String getConfiguration() {
		return _editorConfigurationEntry.getConfiguration();
	}

	/**
	* Returns the create date of this editor configuration entry.
	*
	* @return the create date of this editor configuration entry
	*/
	@Override
	public Date getCreateDate() {
		return _editorConfigurationEntry.getCreateDate();
	}

	/**
	* Returns the editor config key of this editor configuration entry.
	*
	* @return the editor config key of this editor configuration entry
	*/
	@Override
	public String getEditorConfigKey() {
		return _editorConfigurationEntry.getEditorConfigKey();
	}

	/**
	* Returns the editor configuration entry ID of this editor configuration entry.
	*
	* @return the editor configuration entry ID of this editor configuration entry
	*/
	@Override
	public long getEditorConfigurationEntryId() {
		return _editorConfigurationEntry.getEditorConfigurationEntryId();
	}

	/**
	* Returns the editor name of this editor configuration entry.
	*
	* @return the editor name of this editor configuration entry
	*/
	@Override
	public String getEditorName() {
		return _editorConfigurationEntry.getEditorName();
	}

	/**
	* Returns the enabled of this editor configuration entry.
	*
	* @return the enabled of this editor configuration entry
	*/
	@Override
	public boolean getEnabled() {
		return _editorConfigurationEntry.getEnabled();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _editorConfigurationEntry.getExpandoBridge();
	}

	/**
	* Returns the modified date of this editor configuration entry.
	*
	* @return the modified date of this editor configuration entry
	*/
	@Override
	public Date getModifiedDate() {
		return _editorConfigurationEntry.getModifiedDate();
	}

	/**
	* Returns the portlet name of this editor configuration entry.
	*
	* @return the portlet name of this editor configuration entry
	*/
	@Override
	public String getPortletName() {
		return _editorConfigurationEntry.getPortletName();
	}

	/**
	* Returns the primary key of this editor configuration entry.
	*
	* @return the primary key of this editor configuration entry
	*/
	@Override
	public long getPrimaryKey() {
		return _editorConfigurationEntry.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _editorConfigurationEntry.getPrimaryKeyObj();
	}

	/**
	* Returns the uuid of this editor configuration entry.
	*
	* @return the uuid of this editor configuration entry
	*/
	@Override
	public String getUuid() {
		return _editorConfigurationEntry.getUuid();
	}

	@Override
	public int hashCode() {
		return _editorConfigurationEntry.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _editorConfigurationEntry.isCachedModel();
	}

	/**
	* Returns <code>true</code> if this editor configuration entry is enabled.
	*
	* @return <code>true</code> if this editor configuration entry is enabled; <code>false</code> otherwise
	*/
	@Override
	public boolean isEnabled() {
		return _editorConfigurationEntry.isEnabled();
	}

	@Override
	public boolean isEscapedModel() {
		return _editorConfigurationEntry.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _editorConfigurationEntry.isNew();
	}

	@Override
	public void persist() {
		_editorConfigurationEntry.persist();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_editorConfigurationEntry.setCachedModel(cachedModel);
	}

	/**
	* Sets the company ID of this editor configuration entry.
	*
	* @param companyId the company ID of this editor configuration entry
	*/
	@Override
	public void setCompanyId(long companyId) {
		_editorConfigurationEntry.setCompanyId(companyId);
	}

	/**
	* Sets the configuration of this editor configuration entry.
	*
	* @param configuration the configuration of this editor configuration entry
	*/
	@Override
	public void setConfiguration(String configuration) {
		_editorConfigurationEntry.setConfiguration(configuration);
	}

	/**
	* Sets the create date of this editor configuration entry.
	*
	* @param createDate the create date of this editor configuration entry
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_editorConfigurationEntry.setCreateDate(createDate);
	}

	/**
	* Sets the editor config key of this editor configuration entry.
	*
	* @param editorConfigKey the editor config key of this editor configuration entry
	*/
	@Override
	public void setEditorConfigKey(String editorConfigKey) {
		_editorConfigurationEntry.setEditorConfigKey(editorConfigKey);
	}

	/**
	* Sets the editor configuration entry ID of this editor configuration entry.
	*
	* @param editorConfigurationEntryId the editor configuration entry ID of this editor configuration entry
	*/
	@Override
	public void setEditorConfigurationEntryId(long editorConfigurationEntryId) {
		_editorConfigurationEntry.setEditorConfigurationEntryId(editorConfigurationEntryId);
	}

	/**
	* Sets the editor name of this editor configuration entry.
	*
	* @param editorName the editor name of this editor configuration entry
	*/
	@Override
	public void setEditorName(String editorName) {
		_editorConfigurationEntry.setEditorName(editorName);
	}

	/**
	* Sets whether this editor configuration entry is enabled.
	*
	* @param enabled the enabled of this editor configuration entry
	*/
	@Override
	public void setEnabled(boolean enabled) {
		_editorConfigurationEntry.setEnabled(enabled);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_editorConfigurationEntry.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_editorConfigurationEntry.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_editorConfigurationEntry.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the modified date of this editor configuration entry.
	*
	* @param modifiedDate the modified date of this editor configuration entry
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_editorConfigurationEntry.setModifiedDate(modifiedDate);
	}

	@Override
	public void setNew(boolean n) {
		_editorConfigurationEntry.setNew(n);
	}

	/**
	* Sets the portlet name of this editor configuration entry.
	*
	* @param portletName the portlet name of this editor configuration entry
	*/
	@Override
	public void setPortletName(String portletName) {
		_editorConfigurationEntry.setPortletName(portletName);
	}

	/**
	* Sets the primary key of this editor configuration entry.
	*
	* @param primaryKey the primary key of this editor configuration entry
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_editorConfigurationEntry.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_editorConfigurationEntry.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets the uuid of this editor configuration entry.
	*
	* @param uuid the uuid of this editor configuration entry
	*/
	@Override
	public void setUuid(String uuid) {
		_editorConfigurationEntry.setUuid(uuid);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<EditorConfigurationEntry> toCacheModel() {
		return _editorConfigurationEntry.toCacheModel();
	}

	@Override
	public EditorConfigurationEntry toEscapedModel() {
		return new EditorConfigurationEntryWrapper(_editorConfigurationEntry.toEscapedModel());
	}

	@Override
	public String toString() {
		return _editorConfigurationEntry.toString();
	}

	@Override
	public EditorConfigurationEntry toUnescapedModel() {
		return new EditorConfigurationEntryWrapper(_editorConfigurationEntry.toUnescapedModel());
	}

	@Override
	public String toXmlString() {
		return _editorConfigurationEntry.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof EditorConfigurationEntryWrapper)) {
			return false;
		}

		EditorConfigurationEntryWrapper editorConfigurationEntryWrapper = (EditorConfigurationEntryWrapper)obj;

		if (Objects.equals(_editorConfigurationEntry,
					editorConfigurationEntryWrapper._editorConfigurationEntry)) {
			return true;
		}

		return false;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return _editorConfigurationEntry.getStagedModelType();
	}

	@Override
	public EditorConfigurationEntry getWrappedModel() {
		return _editorConfigurationEntry;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _editorConfigurationEntry.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _editorConfigurationEntry.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_editorConfigurationEntry.resetOriginalValues();
	}

	private final EditorConfigurationEntry _editorConfigurationEntry;
}
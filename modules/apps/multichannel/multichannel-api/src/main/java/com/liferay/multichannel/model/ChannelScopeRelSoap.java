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

package com.liferay.multichannel.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class ChannelScopeRelSoap implements Serializable {

	public static ChannelScopeRelSoap toSoapModel(ChannelScopeRel model) {
		ChannelScopeRelSoap soapModel = new ChannelScopeRelSoap();

		soapModel.setEntryId(model.getEntryId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setChannelId(model.getChannelId());
		soapModel.setScopeId(model.getScopeId());

		return soapModel;
	}

	public static ChannelScopeRelSoap[] toSoapModels(ChannelScopeRel[] models) {
		ChannelScopeRelSoap[] soapModels =
			new ChannelScopeRelSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static ChannelScopeRelSoap[][] toSoapModels(
		ChannelScopeRel[][] models) {

		ChannelScopeRelSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new ChannelScopeRelSoap[models.length][models[0].length];
		}
		else {
			soapModels = new ChannelScopeRelSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static ChannelScopeRelSoap[] toSoapModels(
		List<ChannelScopeRel> models) {

		List<ChannelScopeRelSoap> soapModels =
			new ArrayList<ChannelScopeRelSoap>(models.size());

		for (ChannelScopeRel model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new ChannelScopeRelSoap[soapModels.size()]);
	}

	public ChannelScopeRelSoap() {
	}

	public long getPrimaryKey() {
		return _entryId;
	}

	public void setPrimaryKey(long pk) {
		setEntryId(pk);
	}

	public long getEntryId() {
		return _entryId;
	}

	public void setEntryId(long entryId) {
		_entryId = entryId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getChannelId() {
		return _channelId;
	}

	public void setChannelId(long channelId) {
		_channelId = channelId;
	}

	public long getScopeId() {
		return _scopeId;
	}

	public void setScopeId(long scopeId) {
		_scopeId = scopeId;
	}

	private long _entryId;
	private long _companyId;
	private long _channelId;
	private long _scopeId;

}
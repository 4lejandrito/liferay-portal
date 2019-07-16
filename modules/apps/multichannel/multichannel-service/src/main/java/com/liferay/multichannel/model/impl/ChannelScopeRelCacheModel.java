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

package com.liferay.multichannel.model.impl;

import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The cache model class for representing ChannelScopeRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class ChannelScopeRelCacheModel
	implements CacheModel<ChannelScopeRel>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ChannelScopeRelCacheModel)) {
			return false;
		}

		ChannelScopeRelCacheModel channelScopeRelCacheModel =
			(ChannelScopeRelCacheModel)obj;

		if (entryId == channelScopeRelCacheModel.entryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, entryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(9);

		sb.append("{entryId=");
		sb.append(entryId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", channelId=");
		sb.append(channelId);
		sb.append(", scopeId=");
		sb.append(scopeId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ChannelScopeRel toEntityModel() {
		ChannelScopeRelImpl channelScopeRelImpl = new ChannelScopeRelImpl();

		channelScopeRelImpl.setEntryId(entryId);
		channelScopeRelImpl.setCompanyId(companyId);
		channelScopeRelImpl.setChannelId(channelId);
		channelScopeRelImpl.setScopeId(scopeId);

		channelScopeRelImpl.resetOriginalValues();

		return channelScopeRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		entryId = objectInput.readLong();

		companyId = objectInput.readLong();

		channelId = objectInput.readLong();

		scopeId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(entryId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(channelId);

		objectOutput.writeLong(scopeId);
	}

	public long entryId;
	public long companyId;
	public long channelId;
	public long scopeId;

}
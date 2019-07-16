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

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>
 * This class is a wrapper for {@link ChannelScopeRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ChannelScopeRel
 * @generated
 */
@ProviderType
public class ChannelScopeRelWrapper
	extends BaseModelWrapper<ChannelScopeRel>
	implements ChannelScopeRel, ModelWrapper<ChannelScopeRel> {

	public ChannelScopeRelWrapper(ChannelScopeRel channelScopeRel) {
		super(channelScopeRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("entryId", getEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("channelId", getChannelId());
		attributes.put("scopeId", getScopeId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long entryId = (Long)attributes.get("entryId");

		if (entryId != null) {
			setEntryId(entryId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long channelId = (Long)attributes.get("channelId");

		if (channelId != null) {
			setChannelId(channelId);
		}

		Long scopeId = (Long)attributes.get("scopeId");

		if (scopeId != null) {
			setScopeId(scopeId);
		}
	}

	/**
	 * Returns the channel ID of this channel scope rel.
	 *
	 * @return the channel ID of this channel scope rel
	 */
	@Override
	public long getChannelId() {
		return model.getChannelId();
	}

	/**
	 * Returns the company ID of this channel scope rel.
	 *
	 * @return the company ID of this channel scope rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the entry ID of this channel scope rel.
	 *
	 * @return the entry ID of this channel scope rel
	 */
	@Override
	public long getEntryId() {
		return model.getEntryId();
	}

	/**
	 * Returns the primary key of this channel scope rel.
	 *
	 * @return the primary key of this channel scope rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the scope ID of this channel scope rel.
	 *
	 * @return the scope ID of this channel scope rel
	 */
	@Override
	public long getScopeId() {
		return model.getScopeId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the channel ID of this channel scope rel.
	 *
	 * @param channelId the channel ID of this channel scope rel
	 */
	@Override
	public void setChannelId(long channelId) {
		model.setChannelId(channelId);
	}

	/**
	 * Sets the company ID of this channel scope rel.
	 *
	 * @param companyId the company ID of this channel scope rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the entry ID of this channel scope rel.
	 *
	 * @param entryId the entry ID of this channel scope rel
	 */
	@Override
	public void setEntryId(long entryId) {
		model.setEntryId(entryId);
	}

	/**
	 * Sets the primary key of this channel scope rel.
	 *
	 * @param primaryKey the primary key of this channel scope rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the scope ID of this channel scope rel.
	 *
	 * @param scopeId the scope ID of this channel scope rel
	 */
	@Override
	public void setScopeId(long scopeId) {
		model.setScopeId(scopeId);
	}

	@Override
	protected ChannelScopeRelWrapper wrap(ChannelScopeRel channelScopeRel) {
		return new ChannelScopeRelWrapper(channelScopeRel);
	}

}
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

package com.liferay.multichannel.service.impl;

import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.multichannel.service.base.ChannelScopeRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.multichannel.model.ChannelScopeRel",
	service = AopService.class
)
public class ChannelScopeRelLocalServiceImpl
	extends ChannelScopeRelLocalServiceBaseImpl {

	public List<Group> getChannelGroups(long groupId) throws PortalException {
		List<Group> groups = new ArrayList<>(
			channelScopeRelPersistence.findByChannelId(
				groupId
			).size() + 1);

		groups.add(groupLocalService.getGroup(groupId));

		for (ChannelScopeRel channelScopeRel :
				channelScopeRelPersistence.findByChannelId(groupId)) {

			groups.add(
				groupLocalService.getGroup(channelScopeRel.getScopeId()));
		}

		return groups;
	}

}
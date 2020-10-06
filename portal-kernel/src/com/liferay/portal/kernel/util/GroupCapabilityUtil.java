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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Alejandro Tardín
 */
public class GroupCapabilityUtil {

	public static boolean supportsPages(Group group) {
		for (GroupCapabilityProvider groupCapabilityProvider :
				_getCapabilityProviders(group)) {

			if (!groupCapabilityProvider.supportsPages()) {
				return false;
			}
		}

		return true;
	}

	public static boolean supportsPortlet(Group group, Portlet portlet) {
		for (GroupCapabilityProvider groupCapabilityProvider :
				_getCapabilityProviders(group)) {

			if (!groupCapabilityProvider.supportsPortlet(portlet)) {
				return false;
			}
		}

		return true;
	}

	private static List<GroupCapabilityProvider> _getCapabilityProviders(
		Group group) {

		List<GroupCapabilityProvider> groupCapabilityProviders =
			new ArrayList<>();

		for (GroupCapabilityProviderContributor
				groupCapabilityProviderContributor :
					_groupCapabilityProviderContributors) {

			Optional<GroupCapabilityProvider> capabilityProviderOptional =
				groupCapabilityProviderContributor.
					getGroupCapabilityProviderOptional(group);

			capabilityProviderOptional.ifPresent(groupCapabilityProviders::add);
		}

		return groupCapabilityProviders;
	}

	private static final ServiceTrackerList<GroupCapabilityProviderContributor>
		_groupCapabilityProviderContributors =
			ServiceTrackerCollections.openList(
				GroupCapabilityProviderContributor.class);

}
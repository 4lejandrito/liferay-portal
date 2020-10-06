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

package com.liferay.depot.web.internal.util;

import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.GroupCapabilityProvider;
import com.liferay.portal.kernel.util.GroupCapabilityProviderContributor;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = GroupCapabilityProviderContributor.class)
public class DepotGroupCapabilityProviderContributor
	implements GroupCapabilityProviderContributor {

	@Override
	public Optional<GroupCapabilityProvider> getGroupCapabilityProviderOptional(
		Group group) {

		if (!group.isDepot()) {
			return Optional.empty();
		}

		return Optional.of(
			new GroupCapabilityProvider() {

				@Override
				public boolean supportsPages() {
					return false;
				}

				@Override
				public boolean supportsPortlet(Portlet portlet) {
					return _depotApplicationController.isEnabled(
						portlet.getPortletId(), group.getGroupId());
				}

			});
	}

	@Reference
	private DepotApplicationController _depotApplicationController;

}
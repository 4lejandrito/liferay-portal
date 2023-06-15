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

package com.liferay.headless.builder.test.object.util;

import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectRelationshipTestUtil {

	public static void deleteObjectEntriesRelationship(
			long objectEntryId1, long objectEntryId2,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectRelationshipLocalServiceUtil.
			deleteObjectRelationshipMappingTableValues(
				objectRelationship.getObjectRelationshipId(), objectEntryId1,
				objectEntryId2);
	}

	public static void relateObjectEntries(
			long objectEntryId1, long objectEntryId2,
			ObjectRelationship objectRelationship, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAssetTagNames(null);

		ObjectRelationshipLocalServiceUtil.
			addObjectRelationshipMappingTableValues(
				userId, objectRelationship.getObjectRelationshipId(),
				objectEntryId1, objectEntryId2, serviceContext);
	}

}
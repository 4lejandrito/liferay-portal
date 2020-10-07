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

package com.liferay.depot.internal.upgrade.v2_0_0;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Alicia Garcia
 */
public class UpgradeDepotEntryGroupRel extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasColumn("DepotEntryGroupRel", "lastPublishDate")) {
			alter(
				DepotEntryGroupRel.class,
				new AlterTableAddColumn("lastPublishDate", "DATE null"));
		}

		if (!hasColumn("DepotEntryGroupRel", "userId")) {
			alter(
				DepotEntryGroupRel.class,
				new AlterTableAddColumn("userId", "LONG"));
		}

		if (!hasColumn("DepotEntryGroupRel", "userName")) {
			alter(
				DepotEntryGroupRel.class,
				new AlterTableAddColumn("userName", "VARCHAR(75) null"));
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.test.util;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import org.junit.Assert;

/**
 * @author Mauricio Valdivia
 */
public class HeadlessBatchEngineTestUtil {

	public static ImportTask waitForFinish(
			String expectedExecuteStatus, String importTaskJSON,
			Company company)
		throws Exception {

		ImportTask importTask = ImportTask.toDTO(importTaskJSON);

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			company.getCompanyId());

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			company.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		while (true) {
			importTask = importTaskResource.getImportTask(importTask.getId());

			if (StringUtil.equals(
					importTask.getExecuteStatusAsString(), "COMPLETED") ||
				StringUtil.equals(
					importTask.getExecuteStatusAsString(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus,
					importTask.getExecuteStatusAsString());

				break;
			}
		}

		return importTask;
	}

}
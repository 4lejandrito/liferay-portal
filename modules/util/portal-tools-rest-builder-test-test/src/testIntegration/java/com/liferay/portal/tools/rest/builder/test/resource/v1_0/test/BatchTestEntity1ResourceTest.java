/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity1;

import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@Ignore
@RunWith(Arquillian.class)
public class BatchTestEntity1ResourceTest
	extends BaseBatchTestEntity1ResourceTestCase {

	@Override
	protected BatchTestEntity1
			testBatchEngineDeleteImportTask_addBatchTestEntity1()
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1
			testDeleteBatchTestEntity1ByExternalReferenceCode_addBatchTestEntity1()
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1
			testGetBatchTestEntities1Page_addBatchTestEntity1(
				BatchTestEntity1 batchTestEntity)
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1 testGetBatchTestEntity1_addBatchTestEntity1()
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1
			testGetBatchTestEntity1ByExternalReferenceCode_addBatchTestEntity1()
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1 testPostBatchTestEntity1_addBatchTestEntity1(
			BatchTestEntity1 batchTestEntity)
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

	@Override
	protected BatchTestEntity1
			testPutBatchTestEntity1ByExternalReferenceCode_addBatchTestEntity1()
		throws Exception {

		return batchTestEntity1Resource.postBatchTestEntity1(
			randomBatchTestEntity1());
	}

}
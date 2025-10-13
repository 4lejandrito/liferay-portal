/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity2;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntity2Resource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/batch-test-entity2.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = BatchTestEntity2Resource.class
)
public class BatchTestEntity2ResourceImpl
	extends BaseBatchTestEntity2ResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<BatchTestEntity2> {

	@Override
	public void deleteBatchTestEntity2ByExternalReferenceCode(
		String externalReferenceCode) {

		BatchTestEntity2 batchTestEntity2 = _fetchBatchTestEntity2(
			externalReferenceCode);

		if (batchTestEntity2 != null) {
			_batchTestEntities2.remove(
				batchTestEntity2.getExternalReferenceCode());
		}
	}

	@Override
	public Page<BatchTestEntity2> getBatchTestEntities2Page() {
		return Page.of(_batchTestEntities2.values());
	}

	@Override
	public BatchTestEntity2 getBatchTestEntity2ByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		BatchTestEntity2 batchTestEntity2 = _fetchBatchTestEntity2(
			externalReferenceCode);

		if (batchTestEntity2 == null) {
			throw new NoSuchModelException();
		}

		return batchTestEntity2;
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportVulcanBatchEngineTaskItemDelegate.
			ExportImportDescriptor() {

			@Override
			public UnsafeFunction<String, String, Exception>
				filterApplicableExternalReferenceCode() {

				return externalReferenceCode -> {
					BatchTestEntity2 batchTestEntity2 = _fetchBatchTestEntity2(
						externalReferenceCode);

					if (batchTestEntity2 == null) {
						return null;
					}

					return batchTestEntity2.getExternalReferenceCode();
				};
			}

			@Override
			public String getLabel() {
				return "Batch Test Entity 2";
			}

			@Override
			public String getModelClassName() {
				return "com_liferay_portal_tools_rest_builder_test_portlet_" +
					   "BatchTestEntityPortlet";
			}

			@Override
			public String getPortletId() {
				return "com_liferay_portal_tools_rest_builder_test_portlet_" +
					"BatchTestEntityPortlet";
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public BatchTestEntity2 postBatchTestEntity2(
		BatchTestEntity2 batchTestEntity2) {

		if (Validator.isNull(batchTestEntity2.getExternalReferenceCode())) {
			batchTestEntity2.setExternalReferenceCode(
				StringUtil.randomString());
		}

		_batchTestEntities2.put(
			batchTestEntity2.getExternalReferenceCode(), batchTestEntity2);

		return batchTestEntity2;
	}

	@Override
	public BatchTestEntity2 putBatchTestEntity2ByExternalReferenceCode(
		String externalReferenceCode, BatchTestEntity2 batchTestEntity2) {

		BatchTestEntity2 existingBatchTestEntity2 = _fetchBatchTestEntity2(
			externalReferenceCode);

		if (existingBatchTestEntity2 == null) {
			return postBatchTestEntity2(batchTestEntity2);
		}

		batchTestEntity2.setExternalReferenceCode(externalReferenceCode);
		batchTestEntity2.setName(batchTestEntity2.getName());

		_batchTestEntities2.put(
			batchTestEntity2.getExternalReferenceCode(), batchTestEntity2);

		return batchTestEntity2;
	}

	@Override
	public Page<BatchTestEntity2> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getBatchTestEntities2Page();
	}

	private BatchTestEntity2 _fetchBatchTestEntity2(
		String externalReferenceCode) {

		for (BatchTestEntity2 batchTestEntity2 : _batchTestEntities2.values()) {
			if (Objects.equals(
					externalReferenceCode,
					batchTestEntity2.getExternalReferenceCode())) {

				return batchTestEntity2;
			}
		}

		return null;
	}

	private static final Map<String, BatchTestEntity2> _batchTestEntities2 =
		new TreeMap<>();

}
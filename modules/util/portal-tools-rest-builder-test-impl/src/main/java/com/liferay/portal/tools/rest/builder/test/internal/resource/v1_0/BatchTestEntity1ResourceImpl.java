/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity1;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntity1Resource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/batch-test-entity1.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = BatchTestEntity1Resource.class
)
public class BatchTestEntity1ResourceImpl
	extends BaseBatchTestEntity1ResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<BatchTestEntity1> {

	@Override
	public void deleteBatchTestEntity1ByExternalReferenceCode(
		String externalReferenceCode) {

		BatchTestEntity1 batchTestEntity = _fetchBatchTestEntity1(
			externalReferenceCode);

		if (batchTestEntity != null) {
			_batchTestEntities1.remove(batchTestEntity.getId());
			_relationships.remove(batchTestEntity.getId());
		}
	}

	@Override
	public Page<BatchTestEntity1> getBatchTestEntities1Page() {
		return Page.of(
			transform(_batchTestEntities1.values(), this::_toBatchTestEntity1));
	}

	@Override
	public BatchTestEntity1 getBatchTestEntity1(Long batchTestEntityId)
		throws NoSuchModelException {

		BatchTestEntity1 originalBatchTestEntity = _fetchBatchTestEntity1(
			batchTestEntityId);

		if (originalBatchTestEntity == null) {
			throw new NoSuchModelException();
		}

		return _toBatchTestEntity1(originalBatchTestEntity);
	}

	@Override
	public BatchTestEntity1 getBatchTestEntity1ByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		BatchTestEntity1 batchTestEntity = _fetchBatchTestEntity1(
			externalReferenceCode);

		if (batchTestEntity == null) {
			throw new NoSuchModelException();
		}

		return _toBatchTestEntity1(batchTestEntity);
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportVulcanBatchEngineTaskItemDelegate.
			ExportImportDescriptor() {

			@Override
			public UnsafeFunction<String, String, Exception>
				filterApplicableExternalReferenceCode() {

				return externalReferenceCode -> {
					BatchTestEntity1 batchTestEntity1 = _fetchBatchTestEntity1(
						externalReferenceCode);

					if (batchTestEntity1 == null) {
						return null;
					}

					return batchTestEntity1.getExternalReferenceCode();
				};
			}

			@Override
			public String getLabelLanguageKey() {
				return "batch-test-entity-1";
			}

			@Override
			public String getModelClassName() {
				return "com_liferay_portal_tools_rest_builder_test_portlet_" +
					"BatchTestEntityPortlet";
			}

			@Override
			public List<String> getNestedFields() {
				return Arrays.asList("nestedField", "relatedCompanyTestEntity");
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
	public BatchTestEntity1 postBatchTestEntity1(
			BatchTestEntity1 batchTestEntity)
		throws Exception {

		long batchTestEntityId = _counter.increment();

		if (Validator.isNull(batchTestEntity.getExternalReferenceCode())) {
			batchTestEntity.setExternalReferenceCode(StringUtil.randomString());
		}

		batchTestEntity.setId(batchTestEntityId);

		CompanyTestEntity companyTestEntity =
			batchTestEntity.getRelatedCompanyTestEntity();

		if (companyTestEntity != null) {
			CompanyTestEntityResource companyTestEntityResource =
				_factory.create(
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build();

			CompanyTestEntity finalCompanyTestEntity = companyTestEntity;

			companyTestEntity = _emptyModelManager.getOrAddEmptyModel(
				CompanyTestEntity.class, contextCompany.getCompanyId(),
				() -> {
					try {
						return companyTestEntityResource.postCompanyTestEntity(
							finalCompanyTestEntity);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				},
				companyTestEntity.getExternalReferenceCode(),
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						return null;
					}
				},
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				});

			batchTestEntity.setRelatedCompanyTestEntity(companyTestEntity);

			_relationships.put(
				batchTestEntity.getId(), companyTestEntity.getId());
		}
		else {
			_relationships.remove(batchTestEntityId);
		}

		_batchTestEntities1.put(batchTestEntityId, batchTestEntity);

		return _toBatchTestEntity1(batchTestEntity);
	}

	@Override
	public BatchTestEntity1 putBatchTestEntity1ByExternalReferenceCode(
			String externalReferenceCode, BatchTestEntity1 batchTestEntity1)
		throws Exception {

		BatchTestEntity1 existingBatchTestEntity = _fetchBatchTestEntity1(
			externalReferenceCode);

		if (existingBatchTestEntity == null) {
			return postBatchTestEntity1(batchTestEntity1);
		}

		batchTestEntity1.setExternalReferenceCode(externalReferenceCode);
		batchTestEntity1.setId(existingBatchTestEntity.getId());

		CompanyTestEntity companyTestEntity =
			batchTestEntity1.getRelatedCompanyTestEntity();

		if (companyTestEntity != null) {
			CompanyTestEntityResource companyTestEntityResource =
				_factory.create(
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build();

			CompanyTestEntity finalCompanyTestEntity = companyTestEntity;

			companyTestEntity = _emptyModelManager.getOrAddEmptyModel(
				CompanyTestEntity.class, contextCompany.getCompanyId(),
				() -> {
					try {
						return companyTestEntityResource.postCompanyTestEntity(
							finalCompanyTestEntity);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				},
				companyTestEntity.getExternalReferenceCode(),
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						return null;
					}
				},
				(relatedExternalReferenceCode, companyId) -> {
					try {
						return companyTestEntityResource.
							getCompanyTestEntityByExternalReferenceCode(
								relatedExternalReferenceCode);
					}
					catch (Exception exception) {
						throw new PortalException(exception);
					}
				});

			_relationships.put(
				batchTestEntity1.getId(), companyTestEntity.getId());

			batchTestEntity1.setRelatedCompanyTestEntity(companyTestEntity);
		}
		else {
			_relationships.remove(batchTestEntity1.getId());
		}

		_batchTestEntities1.put(batchTestEntity1.getId(), batchTestEntity1);

		return _toBatchTestEntity1(batchTestEntity1);
	}

	@Override
	public Page<BatchTestEntity1> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getBatchTestEntities1Page();
	}

	private BatchTestEntity1 _fetchBatchTestEntity1(long id) {
		if (_batchTestEntities1.containsKey(id)) {
			return _batchTestEntities1.get(id);
		}

		return null;
	}

	private BatchTestEntity1 _fetchBatchTestEntity1(
		String externalReferenceCode) {

		for (BatchTestEntity1 batchTestEntity : _batchTestEntities1.values()) {
			if (Objects.equals(
					externalReferenceCode,
					batchTestEntity.getExternalReferenceCode())) {

				return batchTestEntity;
			}
		}

		return null;
	}

	private BatchTestEntity1 _toBatchTestEntity1(
		BatchTestEntity1 originalBatchTestEntity1) {

		return new BatchTestEntity1() {
			{
				setCustomFields(
					() -> transform(
						originalBatchTestEntity1.getCustomFields(),
						originalCustomField -> {
							CustomField customField = new CustomField();

							customField.setAttributeType(
								() -> NestedFieldsSupplier.supply(
									"customFields.attributeType",
									nestedField ->
										originalCustomField.
											getAttributeType()));
							customField.setCustomValue(
								originalCustomField.getCustomValue());
							customField.setDataType(
								originalCustomField.getDataType());
							customField.setName(originalCustomField.getName());

							return customField;
						},
						CustomField.class));
				setExternalReferenceCode(
					originalBatchTestEntity1.getExternalReferenceCode());
				setId(originalBatchTestEntity1.getId());
				setName(originalBatchTestEntity1.getName());
				setNestedField(
					() -> NestedFieldsSupplier.supply(
						"nestedField",
						nestedField ->
							originalBatchTestEntity1.getNestedField()));
				setRelatedCompanyTestEntity(
					() -> NestedFieldsSupplier.supply(
						"relatedCompanyTestEntity",
						nestedField -> {
							if (!_relationships.containsKey(
									originalBatchTestEntity1.getId())) {

								return null;
							}

							CompanyTestEntityResource
								companyTestEntityResource = _factory.create(
								).uriInfo(
									contextUriInfo
								).user(
									contextUser
								).build();

							return companyTestEntityResource.
								getCompanyTestEntity(
									_relationships.get(
										originalBatchTestEntity1.getId()));
						}));
			}
		};
	}

	private static final Map<Long, BatchTestEntity1> _batchTestEntities1 =
		new TreeMap<>();
	private static final LongWrapper _counter = new LongWrapper();
	private static final Map<Long, Long> _relationships = new TreeMap<>();

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private CompanyTestEntityResource.Factory _factory;

}
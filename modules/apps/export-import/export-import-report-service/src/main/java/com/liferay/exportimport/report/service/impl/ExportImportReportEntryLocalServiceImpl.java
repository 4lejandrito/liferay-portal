/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.impl;

import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.base.ExportImportReportEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 * @author Jonathan McCann
 */
@Component(
	property = "model.class.name=com.liferay.exportimport.report.model.ExportImportReportEntry",
	service = AopService.class
)
public class ExportImportReportEntryLocalServiceImpl
	extends ExportImportReportEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	public ExportImportReportEntry addEmptyExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long exportImportConfigurationId, int origin,
		String scope, String scopeKey) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.create(
				counterLocalService.increment());

		exportImportReportEntry.setGroupId(groupId);
		exportImportReportEntry.setCompanyId(companyId);
		exportImportReportEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		exportImportReportEntry.setClassNameId(classNameId);
		exportImportReportEntry.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntry.setOrigin(origin);
		exportImportReportEntry.setScope(scope);
		exportImportReportEntry.setScopeKey(scopeKey);
		exportImportReportEntry.setType(
			ExportImportReportEntryConstants.TYPE_EMPTY);
		exportImportReportEntry.setStatus(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED);

		return exportImportReportEntryPersistence.update(
			exportImportReportEntry);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExportImportReportEntry addErrorExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long classPK, long exportImportConfigurationId,
		String errorMessage, String errorStacktrace, int origin, String scope,
		String scopeKey) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.create(
				counterLocalService.increment());

		exportImportReportEntry.setGroupId(groupId);
		exportImportReportEntry.setCompanyId(companyId);
		exportImportReportEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		exportImportReportEntry.setClassNameId(classNameId);
		exportImportReportEntry.setClassPK(classPK);
		exportImportReportEntry.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntry.setErrorMessage(errorMessage);
		exportImportReportEntry.setErrorStacktrace(errorStacktrace);
		exportImportReportEntry.setOrigin(origin);
		exportImportReportEntry.setScope(scope);
		exportImportReportEntry.setScopeKey(scopeKey);
		exportImportReportEntry.setType(
			ExportImportReportEntryConstants.TYPE_ERROR);
		exportImportReportEntry.setStatus(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED);

		return exportImportReportEntryPersistence.update(
			exportImportReportEntry);
	}

	@Override
	public List<ExportImportReportEntry> getExportImportReportEntries(
		long companyId, long exportImportConfigurationId) {

		return exportImportReportEntryPersistence.findByC_E(
			companyId, exportImportConfigurationId);
	}

	@Override
	public String getModelName(
		ExportImportReportEntry exportImportReportEntry, Locale locale) {

		String modelResourceNamePrefix =
			_resourceActions.getModelResourceNamePrefix();

		return _language.get(
			locale,
			modelResourceNamePrefix.concat(
				exportImportReportEntry.getClassName()),
			null);
	}

	@Reference
	private Language _language;

	@Reference
	private ResourceActions _resourceActions;

}
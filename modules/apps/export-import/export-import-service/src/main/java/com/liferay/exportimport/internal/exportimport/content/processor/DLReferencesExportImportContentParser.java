/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.exportimport.content.processor;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.content.processor.ExportImportContentParser;
import com.liferay.exportimport.content.processor.constants.ExportImportContentParserConstants;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.staging.StagingGroupHelper;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = ExportImportContentParserConstants.CONTENT_PARSER_TYPE + "=" + ExportImportContentParserConstants.DOCUMENT_LIBRARY,
	service = ExportImportContentParser.class
)
public class DLReferencesExportImportContentParser
	implements ExportImportContentParser {

	@Override
	public String parseExportContent(
		String content, PortletDataContext portletDataContext) {

		StringBuilder sb = new StringBuilder(content);

		DLReferencesReverseIterator dlReferencesReverseIterator =
			new DLReferencesReverseIterator(
				content, _fileEntryFriendlyURLResolver,
				portletDataContext.getScopeGroupId());

		while (dlReferencesReverseIterator.hasNext()) {
			DLReferencesReverseIterator.DLReference dlReference =
				dlReferencesReverseIterator.next();

			FileEntry fileEntry = dlReference.getFileEntry();

			if (fileEntry == null) {
				continue;
			}

			DocumentLibraryReference documentLibraryReference =
				new DocumentLibraryReference(
					fileEntry, dlReference.getFriendlyURL());

			sb.replace(
				dlReference.getBeginPos(), dlReference.getEndPos(),
				documentLibraryReference.toString());
		}

		return sb.toString();
	}

	@Override
	public String parseImportContent(
			String className, String content, Object item, String modelName,
			PortletDataContext portletDataContext)
		throws Exception {

		for (DocumentLibraryReference documentLibraryReference :
				DocumentLibraryReference.parse(content)) {

			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					portletDataContext.getGroupId(),
					documentLibraryReference.getExternalReferenceCode());

			if (fileEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"The FileEntry with external reference code ",
							documentLibraryReference.getExternalReferenceCode(),
							" does not exist yet. A warning is added just in ",
							"case the FileEntry is not imported"));
				}

				portletDataContext.addUnsafeRunnable(
					() -> {
						boolean addWarning = false;

						String friendlyURL =
							documentLibraryReference.getFriendlyURL();

						try {
							FileEntry referencedFileEntry =
								_fileEntryFriendlyURLResolver.
									resolveFriendlyURL(
										portletDataContext.getGroupId(),
										friendlyURL);

							if ((referencedFileEntry == null) ||
								!StringUtil.equals(
									referencedFileEntry.
										getExternalReferenceCode(),
									documentLibraryReference.
										getExternalReferenceCode()) ||
								!StringUtil.equals(
									referencedFileEntry.getUuid(),
									documentLibraryReference.getUuid())) {

								addWarning = true;
							}
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(
									"Error resolving the friendlyURL " +
										friendlyURL,
									exception);
							}

							addWarning = true;
						}

						if (addWarning) {
							Group group = _groupLocalService.getGroup(
								portletDataContext.getGroupId());

							boolean companyGroup =
								_stagingGroupHelper.isCompanyGroup(group);

							_exportImportReportEntryLocalService.
								addWarningExportImportReportEntry(
									companyGroup ? 0L :
										portletDataContext.getGroupId(),
									group.getCompanyId(),
									documentLibraryReference.
										getExternalReferenceCode(),
									_classNameLocalService.getClassNameId(
										className),
									"This item may contain a wrong reference " +
										"to a FileEntry",
									modelName);
						}
					});
			}
			else if ((fileEntry != null) &&
					 !StringUtil.equals(
						 documentLibraryReference.getUuid(),
						 fileEntry.getUuid())) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"The UUID of the referenced FileEntry does not match " +
							"the UUID of the FileEntry");
				}

				fileEntry = null;
			}

			String url = null;

			if (fileEntry == null) {
				url = _dlurlHelper.getPreviewURL(
					documentLibraryReference.getFriendlyURL(),
					_groupLocalService.getGroup(
						portletDataContext.getGroupId()));
			}
			else {
				url = _dlurlHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false);
			}

			content = StringUtil.replaceLast(
				content, documentLibraryReference.getReference(), url);
		}

		return content;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLReferencesExportImportContentParser.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	private static class DocumentLibraryReference {

		public static List<DocumentLibraryReference> parse(String value) {
			List<DocumentLibraryReference> documentLibraryReferences =
				new ArrayList<>();

			int startIndex = -1;

			while ((startIndex = value.indexOf(_BEGIN)) != -1) {
				int endIndex = value.indexOf(_END, startIndex);

				String reference = value.substring(
					startIndex, endIndex + _END.length());

				String externalReferenceCode = reference.substring(
					reference.indexOf("$dl-external-reference-code=") +
						"$dl-external-reference-code=".length(),
					reference.indexOf("$,$dl-group-id="));
				long groupId = GetterUtil.getLong(
					reference.substring(
						reference.indexOf("$,$dl-group-id=") +
							"$,$dl-group-id=".length(),
						reference.indexOf("$,$friendly-url=")));
				String friendlyURL = reference.substring(
					reference.indexOf("$,$friendly-url=") +
						"$,$friendly-url=".length(),
					reference.indexOf("$,$uuid="));
				String uuid = reference.substring(
					reference.indexOf("$,$uuid=") + "$,$uuid=".length(),
					reference.indexOf(_END));

				documentLibraryReferences.add(
					new DocumentLibraryReference(
						externalReferenceCode, friendlyURL, groupId, reference,
						uuid));

				value = value.substring(endIndex + _END.length());
			}

			return documentLibraryReferences;
		}

		public DocumentLibraryReference(
			FileEntry fileEntry, String friendlyURL) {

			_friendlyURL = friendlyURL;

			_externalReferenceCode = fileEntry.getExternalReferenceCode();
			_groupId = fileEntry.getGroupId();
			_uuid = fileEntry.getUuid();
		}

		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		public String getFriendlyURL() {
			return _friendlyURL;
		}

		public long getGroupId() {
			return _groupId;
		}

		public String getReference() {
			return _referenceString;
		}

		public String getUuid() {
			return _uuid;
		}

		@Override
		public String toString() {
			return StringBundler.concat(
				"[$dl-reference$ $dl-external-reference-code=",
				_externalReferenceCode, "$,$dl-group-id=", _groupId,
				"$,$friendly-url=", _friendlyURL, "$,$uuid=", _uuid, "$]");
		}

		private DocumentLibraryReference(
			String externalReferenceCode, String friendlyURL, long groupId,
			String referenceString, String uuid) {

			_externalReferenceCode = externalReferenceCode;
			_friendlyURL = friendlyURL;
			_groupId = groupId;
			_referenceString = referenceString;
			_uuid = uuid;
		}

		private static final String _BEGIN = "[$dl-reference$";

		private static final String _END = "$]";

		private final String _externalReferenceCode;
		private final String _friendlyURL;
		private final long _groupId;
		private String _referenceString;
		private final String _uuid;

	}

}
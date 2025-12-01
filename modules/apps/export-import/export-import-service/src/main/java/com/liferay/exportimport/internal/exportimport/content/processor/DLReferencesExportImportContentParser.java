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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

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
			String content, PortletDataContext portletDataContext)
		throws Exception {

		DocumentLibraryReference documentLibraryReference = null;

		while ((documentLibraryReference = DocumentLibraryReference.parse(
					content)) != null) {

			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					portletDataContext.getScopeGroupId(),
					documentLibraryReference.getExternalReferenceCode());

			if ((fileEntry != null) &&
				!StringUtil.equals(
					documentLibraryReference.getUuid(), fileEntry.getUuid())) {

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
						portletDataContext.getScopeGroupId()));
			}
			else {
				url = _dlurlHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false);
			}

			content = StringUtil.replaceLast(
				content, documentLibraryReference.toString(), url);
		}

		return content;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLReferencesExportImportContentParser.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	private static class DocumentLibraryReference {

		public static DocumentLibraryReference parse(String value) {
			int lastIndex = value.lastIndexOf("[$dl-reference$");

			if (lastIndex == -1) {
				return null;
			}

			value = value.substring(lastIndex);

			String externalReferenceCode = value.substring(
				value.indexOf("$dl-external-reference-code=") +
					"$dl-external-reference-code=".length(),
				value.indexOf("$,$dl-group-id="));
			long groupId = GetterUtil.getLong(
				value.substring(
					value.indexOf("$,$dl-group-id=") +
						"$,$dl-group-id=".length(),
					value.indexOf("$,$friendly-url=")));
			String friendlyURL = value.substring(
				value.indexOf("$,$friendly-url=") + "$,$friendly-url=".length(),
				value.indexOf("$,$uuid="));
			String uuid = value.substring(
				value.indexOf("$,$uuid=") + "$,$uuid=".length(),
				value.indexOf("$]"));

			return new DocumentLibraryReference(
				externalReferenceCode, friendlyURL, groupId, uuid);
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
			String uuid) {

			_externalReferenceCode = externalReferenceCode;
			_friendlyURL = friendlyURL;
			_groupId = groupId;
			_uuid = uuid;
		}

		private final String _externalReferenceCode;
		private final String _friendlyURL;
		private final long _groupId;
		private final String _uuid;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorReturnType;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "launch.entry.manager.class.name=com.liferay.fragment.model.FragmentEntry",
	service = LaunchEntryManager.class
)
public class FragmentEntryLaunchEntryManager implements LaunchEntryManager {

	@Override
	public Draft addDraft(long classPK, String versionKey)
		throws PortalException {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(classPK);

		FragmentEntryVersion fragmentEntryVersion =
			_fragmentEntryLocalService.getVersion(
				fragmentEntry, GetterUtil.getInteger(versionKey));

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return _toDraft(
			_fragmentEntryLocalService.addFragmentEntry(
				null, serviceContext.getUserId(), fragmentEntry.getGroupId(),
				fragmentEntry.getFragmentCollectionId(), null,
				fragmentEntryVersion.getName(), fragmentEntryVersion.getCss(),
				fragmentEntryVersion.getHtml(), fragmentEntryVersion.getJs(),
				fragmentEntry.isCacheable(),
				fragmentEntryVersion.getConfiguration(),
				fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
				false, false, fragmentEntry.getType(),
				fragmentEntryVersion.getTypeOptions(),
				WorkflowConstants.STATUS_DRAFT, serviceContext));
	}

	@Override
	public void deleteDraft(long classPK, long draftId) throws PortalException {
		FragmentEntry launchDraftFragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(draftId);

		if (launchDraftFragmentEntry == null) {
			return;
		}

		_fragmentEntryLocalService.deleteFragmentEntry(
			launchDraftFragmentEntry);
	}

	@Override
	public Draft fetchDraft(long classPK, long draftId) {
		FragmentEntry launchDraftFragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(draftId);

		if (launchDraftFragmentEntry == null) {
			return null;
		}

		return _toDraft(launchDraftFragmentEntry);
	}

	@Override
	public Item fetchItem(long classPK) {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(classPK);

		if (fragmentEntry == null) {
			return null;
		}

		return new Item() {

			@Override
			public long getGroupId() {
				return fragmentEntry.getGroupId();
			}

			@Override
			public Serializable getPrimaryKey() {
				return fragmentEntry.getFragmentEntryId();
			}

			@Override
			public String getTitle(Locale locale) {
				return fragmentEntry.getName();
			}

			@Override
			public String getTypeName(Locale locale) {
				return _language.get(locale, "fragment");
			}

		};
	}

	@Override
	public Version fetchPublishedVersion(long classPK) {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(classPK);

		if (fragmentEntry == null) {
			return null;
		}

		FragmentEntryVersion publishedFragmentEntryVersion = null;

		for (FragmentEntryVersion fragmentEntryVersion :
				_fragmentEntryLocalService.getVersions(fragmentEntry)) {

			if ((publishedFragmentEntryVersion == null) ||
				(fragmentEntryVersion.getVersion() >
					publishedFragmentEntryVersion.getVersion())) {

				publishedFragmentEntryVersion = fragmentEntryVersion;
			}
		}

		if (publishedFragmentEntryVersion == null) {
			return null;
		}

		return _toVersion(fragmentEntry, publishedFragmentEntryVersion);
	}

	@Override
	public Version fetchVersion(long classPK, String versionKey) {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(classPK);

		if (fragmentEntry == null) {
			return null;
		}

		int version = GetterUtil.getInteger(versionKey);

		for (FragmentEntryVersion fragmentEntryVersion :
				_fragmentEntryLocalService.getVersions(fragmentEntry)) {

			if (fragmentEntryVersion.getVersion() == version) {
				return _toVersion(fragmentEntry, fragmentEntryVersion);
			}
		}

		return null;
	}

	@Override
	public long getClassPK(Map<String, String> selectedItemData) {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				GetterUtil.getLong(selectedItemData.get("groupId")),
				GetterUtil.getString(selectedItemData.get("fragmentEntryKey")));

		if (fragmentEntry == null) {
			return 0;
		}

		return fragmentEntry.getFragmentEntryId();
	}

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion() {
		FragmentEntryItemSelectorCriterion fragmentEntryItemSelectorCriterion =
			new FragmentEntryItemSelectorCriterion();

		fragmentEntryItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FragmentEntryItemSelectorReturnType());

		fragmentEntryItemSelectorCriterion.
			setIncludeFragmentCollectionContributors(false);

		return fragmentEntryItemSelectorCriterion;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "fragment");
	}

	@Override
	public Class<?> getModelClass() {
		return FragmentEntry.class;
	}

	@Override
	public List<Version> getVersions(long classPK, Locale locale) {
		List<Version> versions = new ArrayList<>();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(classPK);

		if (fragmentEntry == null) {
			return versions;
		}

		List<FragmentEntryVersion> fragmentEntryVersions = new ArrayList<>(
			_fragmentEntryLocalService.getVersions(fragmentEntry));

		fragmentEntryVersions.sort(
			Comparator.comparingInt(
				FragmentEntryVersion::getVersion
			).reversed());

		for (FragmentEntryVersion fragmentEntryVersion :
				fragmentEntryVersions) {

			versions.add(_toVersion(fragmentEntry, fragmentEntryVersion));
		}

		return versions;
	}

	@Override
	public Version publishDraft(long classPK, long draftId)
		throws PortalException {

		FragmentEntry launchDraftFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(draftId);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(classPK);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		FragmentEntry publishedFragmentEntry =
			_fragmentEntryLocalService.updateFragmentEntry(
				serviceContext.getUserId(), classPK,
				fragmentEntry.getFragmentCollectionId(),
				launchDraftFragmentEntry.getName(),
				launchDraftFragmentEntry.getCss(),
				launchDraftFragmentEntry.getHtml(),
				launchDraftFragmentEntry.getJs(), fragmentEntry.isCacheable(),
				launchDraftFragmentEntry.getConfiguration(),
				fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
				fragmentEntry.isReadOnly(),
				launchDraftFragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED);

		for (long groupId : _getGroupIds(publishedFragmentEntry)) {
			for (FragmentEntryLink fragmentEntryLink :
					_fragmentEntryLinkLocalService.
						getFragmentEntryLinksByFragmentEntry(
							groupId, publishedFragmentEntry)) {

				_fragmentEntryLinkLocalService.updateLatestChanges(
					publishedFragmentEntry, fragmentEntryLink);
			}
		}

		_fragmentEntryLocalService.deleteFragmentEntry(
			launchDraftFragmentEntry);

		return fetchPublishedVersion(classPK);
	}

	private String _getEditURL(
		FragmentEntry fragmentEntry, String redirect,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				FragmentPortletKeys.FRAGMENT)
		).setMVCRenderCommandName(
			"/fragment/edit_fragment_entry"
		).setRedirect(
			redirect
		).setBackURL(
			redirect
		).setParameter(
			"backURLTitle",
			_language.get(LocaleThreadLocal.getThemeDisplayLocale(), "launches")
		).setParameter(
			"fragmentCollectionId", fragmentEntry.getFragmentCollectionId()
		).setParameter(
			"fragmentEntryId", fragmentEntry.getFragmentEntryId()
		).buildString();
	}

	private List<Long> _getGroupIds(FragmentEntry fragmentEntry)
		throws PortalException {

		List<Long> groupIds = ListUtil.fromArray(fragmentEntry.getGroupId());

		Group group = _groupLocalService.getGroup(fragmentEntry.getGroupId());

		if (!group.isDepot()) {
			return groupIds;
		}

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			group.getGroupId());

		if (depotEntry == null) {
			return groupIds;
		}

		for (DepotEntryGroupRel depotEntryGroupRel :
				_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
					depotEntry)) {

			groupIds.add(depotEntryGroupRel.getToGroupId());
		}

		return groupIds;
	}

	private Draft _toDraft(FragmentEntry launchDraftFragmentEntry) {
		return new Draft() {

			@Override
			public long getDraftId() {
				return launchDraftFragmentEntry.getFragmentEntryId();
			}

			@Override
			public String getEditURL(
				String redirect,
				RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

				return _getEditURL(
					launchDraftFragmentEntry, redirect,
					requestBackedPortletURLFactory);
			}

			@Override
			public Date getModifiedDate() {
				return launchDraftFragmentEntry.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				return launchDraftFragmentEntry;
			}

			@Override
			public int getStatus() {
				return launchDraftFragmentEntry.getStatus();
			}

			@Override
			public String getUserName() {
				return launchDraftFragmentEntry.getUserName();
			}

		};
	}

	private Version _toVersion(
		FragmentEntry fragmentEntry,
		FragmentEntryVersion fragmentEntryVersion) {

		return new Version() {

			@Override
			public String getLabel(Locale locale) {
				return String.valueOf(fragmentEntryVersion.getVersion());
			}

			@Override
			public Date getModifiedDate() {
				return fragmentEntryVersion.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				FragmentEntry previewFragmentEntry =
					(FragmentEntry)fragmentEntry.clone();

				fragmentEntryVersion.populateVersionedModel(
					previewFragmentEntry);

				return previewFragmentEntry;
			}

			@Override
			public int getStatus() {
				return fragmentEntryVersion.getStatus();
			}

			@Override
			public String getUserName() {
				return fragmentEntryVersion.getUserName();
			}

			@Override
			public String getVersionKey() {
				return String.valueOf(fragmentEntryVersion.getVersion());
			}

		};
	}

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

}
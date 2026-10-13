/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.internal.entry.manager.util.LaunchPreviewLayoutUtil;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.content.creator.LayoutContentVersionCreator;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.restorer.LayoutContentVersionRestorer;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.util.comparator.LayoutContentVersionVersionComparator;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.layout.item.selector.LayoutItemSelectorReturnType;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.preview.PreviewableResolverUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
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
	property = "launch.entry.manager.class.name=com.liferay.portal.kernel.model.Layout",
	service = LaunchEntryManager.class
)
public class LayoutLaunchEntryManager implements LaunchEntryManager {

	@Override
	public Draft addDraft(long classPK, String versionKey)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(classPK);

		if (!layout.isTypeContent()) {
			throw new PortalException(
				"Layout " + classPK + " is not a content page");
		}

		Layout baseLayout = _fetchLayout(classPK, versionKey);

		if (baseLayout != null) {
			return _toDraft(
				_copyLayoutContent(
					baseLayout,
					_addLaunchLayout(
						layout, baseLayout,
						_getLaunchDraftExternalReferenceCode(layout))));
		}

		LayoutContentVersion layoutContentVersion = _fetchLayoutContentVersion(
			layout, versionKey);

		if (layoutContentVersion == null) {
			throw new PortalException(
				StringBundler.concat(
					"Version ", versionKey, " is not a version of the layout ",
					classPK));
		}

		Layout launchDraftLayout = _copyLayoutContent(
			layout,
			_addLaunchLayout(
				layout, layout, _getLaunchDraftExternalReferenceCode(layout)));

		if (layoutContentVersion.getLayoutContentVersionId() ==
				_getLatestApprovedLayoutContentVersionId(layout)) {

			return _toDraft(launchDraftLayout);
		}

		return _toDraft(
			_restoreLayoutContentVersion(
				launchDraftLayout, layoutContentVersion));
	}

	@Override
	public void deleteDraft(long classPK, long draftId) throws PortalException {
		Layout launchDraftLayout = _fetchLaunchDraftLayout(classPK, draftId);

		if (launchDraftLayout == null) {
			return;
		}

		_layoutLocalService.deleteLayout(launchDraftLayout);
	}

	@Override
	public Draft fetchDraft(long classPK, long draftId) {
		Layout launchDraftLayout = _fetchLaunchDraftLayout(classPK, draftId);

		if (launchDraftLayout == null) {
			return null;
		}

		return _toDraft(launchDraftLayout);
	}

	@Override
	public Item fetchItem(long classPK) {
		Layout layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			return null;
		}

		return new Item() {

			@Override
			public long getGroupId() {
				return layout.getGroupId();
			}

			@Override
			public Serializable getPrimaryKey() {
				return layout.getPlid();
			}

			@Override
			public String getTitle(Locale locale) {
				return layout.getName(locale);
			}

			@Override
			public String getTypeName(Locale locale) {
				return _language.get(locale, "content-page");
			}

		};
	}

	@Override
	public Version fetchPublishedVersion(long classPK) {
		Layout layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			return null;
		}

		LayoutContentVersion layoutContentVersion =
			_fetchLatestApprovedLayoutContentVersion(layout);

		if (layoutContentVersion != null) {
			return _toVersion(layout, layoutContentVersion);
		}

		return _toVersion(layout);
	}

	@Override
	public Version fetchVersion(long classPK, String versionKey) {
		Layout layout = _fetchLayout(classPK, versionKey);

		if (layout != null) {
			return _toVersion(layout);
		}

		layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			return null;
		}

		LayoutContentVersion layoutContentVersion = _fetchLayoutContentVersion(
			layout, versionKey);

		if (layoutContentVersion == null) {
			return null;
		}

		return _toVersion(layout, layoutContentVersion);
	}

	@Override
	public long getClassPK(Map<String, String> selectedItemData) {
		Layout layout = _layoutLocalService.fetchLayout(
			GetterUtil.getLong(selectedItemData.get("plid")));

		if ((layout == null) || !layout.isTypeContent() ||
			layout.isDraftLayout()) {

			return 0;
		}

		return layout.getPlid();
	}

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion() {
		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new LayoutItemSelectorReturnType());

		return layoutItemSelectorCriterion;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "page");
	}

	@Override
	public Class<?> getModelClass() {
		return Layout.class;
	}

	@Override
	public List<Version> getVersions(long classPK, Locale locale) {
		List<Version> versions = new ArrayList<>();

		Layout layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			return versions;
		}

		List<LayoutContentVersion> layoutContentVersions =
			_getLayoutContentVersions(layout);

		if (layoutContentVersions.isEmpty()) {
			versions.add(_toVersion(layout));
		}
		else {
			for (LayoutContentVersion layoutContentVersion :
					layoutContentVersions) {

				versions.add(_toVersion(layout, layoutContentVersion));
			}
		}

		if (!layout.isPublished()) {
			Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

			if (draftLayout != null) {
				versions.add(_toVersion(draftLayout));
			}
		}

		return versions;
	}

	@Override
	public Version publishDraft(long classPK, long draftId)
		throws PortalException {

		Layout launchDraftLayout = _fetchLaunchDraftLayout(classPK, draftId);

		if (launchDraftLayout == null) {
			throw new NoSuchLayoutException(
				StringBundler.concat(
					"No launch draft exists with the key {plid=", classPK,
					", draftId=", draftId, "}"));
		}

		_copyLayoutContent(
			launchDraftLayout, _layoutLocalService.getLayout(classPK));

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		if (draftLayout != null) {
			draftLayout = _copyLayoutContent(launchDraftLayout, draftLayout);

			UnicodeProperties typeSettingsUnicodeProperties =
				draftLayout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED,
				Boolean.TRUE.toString());

			draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

			draftLayout = _layoutLocalService.updateLayout(draftLayout);

			_createLayoutContentVersion(draftLayout);
		}

		_layoutLocalService.deleteLayout(launchDraftLayout);

		return fetchPublishedVersion(classPK);
	}

	private Layout _addLaunchLayout(
			Layout layout, Layout baseLayout, String externalReferenceCode)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return _layoutLocalService.addLayout(
			externalReferenceCode, serviceContext.getUserId(),
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getParentLayoutId(), _portal.getClassNameId(Layout.class),
			layout.getPlid(), baseLayout.getNameMap(), baseLayout.getTitleMap(),
			baseLayout.getDescriptionMap(), baseLayout.getKeywordsMap(),
			baseLayout.getRobotsMap(), layout.getType(),
			baseLayout.getTypeSettings(), true, true, Collections.emptyMap(),
			baseLayout.getMasterLayoutPageTemplateEntryERC(), serviceContext);
	}

	private Layout _copyLayoutContent(Layout sourceLayout, Layout targetLayout)
		throws PortalException {

		try {
			targetLayout = _layoutLocalService.copyLayoutContent(
				sourceLayout, targetLayout);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		targetLayout.setNameMap(sourceLayout.getNameMap());
		targetLayout.setTitleMap(sourceLayout.getTitleMap());
		targetLayout.setDescriptionMap(sourceLayout.getDescriptionMap());
		targetLayout.setKeywordsMap(sourceLayout.getKeywordsMap());
		targetLayout.setRobotsMap(sourceLayout.getRobotsMap());

		return _layoutLocalService.updateLayout(targetLayout);
	}

	private void _createLayoutContentVersion(Layout layout) {
		LayoutContentVersionCreator layoutContentVersionCreator =
			_layoutContentVersionCreatorSnapshot.get();

		if (layoutContentVersionCreator == null) {
			return;
		}

		layoutContentVersionCreator.createLayoutContentVersion(layout);
	}

	private LayoutContentVersion _fetchLatestApprovedLayoutContentVersion(
		Layout layout) {

		LayoutContentVersionLocalService layoutContentVersionLocalService =
			_fetchLayoutContentVersionLocalService(layout);

		if (layoutContentVersionLocalService == null) {
			return null;
		}

		long layoutContentVersionId = _getLatestApprovedLayoutContentVersionId(
			layout);

		if (layoutContentVersionId == 0) {
			return null;
		}

		return layoutContentVersionLocalService.fetchLayoutContentVersion(
			layoutContentVersionId);
	}

	private Layout _fetchLaunchDraftLayout(long classPK, long draftId) {
		Layout launchDraftLayout = _layoutLocalService.fetchLayout(draftId);

		if ((launchDraftLayout == null) ||
			(launchDraftLayout.getClassPK() != classPK) ||
			!_isLaunchDraftLayout(launchDraftLayout)) {

			return null;
		}

		return launchDraftLayout;
	}

	private Layout _fetchLayout(long classPK, String versionKey) {
		Layout layout = _layoutLocalService.fetchLayout(
			GetterUtil.getLong(versionKey));

		if ((layout == null) ||
			LaunchPreviewLayoutUtil.isPreviewLayout(layout) ||
			_isLaunchDraftLayout(layout)) {

			return null;
		}

		if ((layout.getPlid() != classPK) &&
			(!layout.isDraftLayout() || (layout.getClassPK() != classPK))) {

			return null;
		}

		return layout;
	}

	private LayoutContentVersion _fetchLayoutContentVersion(
		Layout layout, String versionKey) {

		LayoutContentVersionLocalService layoutContentVersionLocalService =
			_fetchLayoutContentVersionLocalService(layout);

		if (layoutContentVersionLocalService == null) {
			return null;
		}

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionLocalService.
				fetchLayoutContentVersionByExternalReferenceCode(
					versionKey, layout.getGroupId());

		if (layoutContentVersion == null) {
			layoutContentVersion =
				layoutContentVersionLocalService.fetchLayoutContentVersion(
					GetterUtil.getLong(versionKey));
		}

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layout.getPlid());

		if ((layoutContentVersion == null) || (draftLayout == null) ||
			(layoutContentVersion.getPlid() != draftLayout.getPlid())) {

			return null;
		}

		return layoutContentVersion;
	}

	/**
	 * Returns the service that keeps the page's version history, or
	 * <code>null</code> when the feature is unavailable. A page that is not
	 * versioned still has a published version and a current draft to branch
	 * from, so every caller falls back to the layouts themselves.
	 */
	private LayoutContentVersionLocalService
		_fetchLayoutContentVersionLocalService(Layout layout) {

		if (!FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-10622")) {

			return null;
		}

		return _layoutContentVersionLocalServiceSnapshot.get();
	}

	private String _getEditURL(Layout layout, String redirect) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		try {
			return HttpComponentsUtil.addParameters(
				_portal.getLayoutFullURL(layout, themeDisplay), "p_l_back_url",
				redirect, "p_l_back_url_title",
				_language.get(themeDisplay.getLocale(), "launches"), "p_l_mode",
				Constants.EDIT);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private String _getLabel(Layout layout, Locale locale) {
		if (!layout.isDraftLayout()) {
			return _language.get(locale, "published");
		}

		return _language.get(locale, "current-draft");
	}

	private long _getLatestApprovedLayoutContentVersionId(Layout layout) {
		LayoutContentVersionLocalService layoutContentVersionLocalService =
			_fetchLayoutContentVersionLocalService(layout);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layout.getPlid());

		if ((layoutContentVersionLocalService == null) ||
			(draftLayout == null)) {

			return 0;
		}

		return layoutContentVersionLocalService.
			getLatestApprovedLayoutContentVersionId(draftLayout.getPlid());
	}

	private String _getLaunchDraftExternalReferenceCode(Layout layout) {
		for (int i = 1;; i++) {
			String externalReferenceCode = StringBundler.concat(
				layout.getExternalReferenceCode(),
				LayoutConstants.EXTERNAL_REFERENCE_CODE_INFIX_LAUNCH_DRAFT, i);

			Layout launchDraftLayout =
				_layoutLocalService.fetchLayoutByExternalReferenceCode(
					externalReferenceCode, layout.getGroupId());

			if (launchDraftLayout == null) {
				return externalReferenceCode;
			}
		}
	}

	private List<LayoutContentVersion> _getLayoutContentVersions(
		Layout layout) {

		LayoutContentVersionLocalService layoutContentVersionLocalService =
			_fetchLayoutContentVersionLocalService(layout);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layout.getPlid());

		if ((layoutContentVersionLocalService == null) ||
			(draftLayout == null)) {

			return Collections.emptyList();
		}

		try {
			return ListUtil.sort(
				layoutContentVersionLocalService.getLayoutContentVersions(
					draftLayout.getPlid()),
				LayoutContentVersionVersionComparator.getInstance(false));
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Materializes the version as a layout of its own the first time it is
	 * previewed, and hands that same layout to every preview after it. A
	 * version holds the page as data, while rendering resolves a page's
	 * structure and its fragment entry links by PLID, so the only version that
	 * a page can render on its own is the one that is live. Returns
	 * <code>null</code> when the version cannot be materialized, which leaves
	 * the preview on the live page rather than failing the request it is
	 * rendering.
	 */
	private Layout _getPreviewLayout(
		Layout layout, LayoutContentVersion layoutContentVersion) {

		String externalReferenceCode =
			LaunchPreviewLayoutUtil.getExternalReferenceCode(
				layout, layoutContentVersion);

		Layout previewLayout =
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				externalReferenceCode, layout.getGroupId());

		if (previewLayout != null) {
			return previewLayout;
		}

		User user = null;

		try {
			user = _userLocalService.getUser(layout.getUserId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to materialize version " +
					_getVersionKey(layoutContentVersion),
				portalException);

			return null;
		}

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(null);

			AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout, user)) {

			return _restoreLayoutContentVersion(
				_copyLayoutContent(
					layout,
					_addLaunchLayout(layout, layout, externalReferenceCode)),
				layoutContentVersion);
		}
		catch (Exception exception) {
			previewLayout =
				_layoutLocalService.fetchLayoutByExternalReferenceCode(
					externalReferenceCode, layout.getGroupId());

			if (previewLayout != null) {
				return previewLayout;
			}

			_log.error(
				"Unable to materialize version " +
					_getVersionKey(layoutContentVersion),
				exception);

			return null;
		}
	}

	private String _getVersionKey(LayoutContentVersion layoutContentVersion) {
		String externalReferenceCode =
			layoutContentVersion.getExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			return externalReferenceCode;
		}

		return String.valueOf(layoutContentVersion.getLayoutContentVersionId());
	}

	private boolean _isLaunchDraftLayout(Layout layout) {
		if (LaunchPreviewLayoutUtil.isPreviewLayout(layout)) {
			return false;
		}

		String externalReferenceCode = layout.getExternalReferenceCode();

		return externalReferenceCode.contains(
			LayoutConstants.EXTERNAL_REFERENCE_CODE_INFIX_LAUNCH_DRAFT);
	}

	private Layout _restoreLayoutContentVersion(
			Layout launchDraftLayout, LayoutContentVersion layoutContentVersion)
		throws PortalException {

		LayoutContentVersionRestorer layoutContentVersionRestorer =
			_layoutContentVersionRestorerSnapshot.get();

		if (layoutContentVersionRestorer == null) {
			throw new PortalException(
				"Unable to branch from version " +
					_getVersionKey(layoutContentVersion));
		}

		try {
			return layoutContentVersionRestorer.restoreLayoutContentVersion(
				launchDraftLayout, layoutContentVersion,
				ServiceContextThreadLocal.getServiceContext());
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private Draft _toDraft(Layout launchDraftLayout) {
		return new Draft() {

			@Override
			public long getDraftId() {
				return launchDraftLayout.getPlid();
			}

			@Override
			public String getEditURL(
				String redirect,
				RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

				return _getEditURL(launchDraftLayout, redirect);
			}

			@Override
			public Date getModifiedDate() {
				return launchDraftLayout.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				return new LaunchLayoutWrapper(launchDraftLayout);
			}

			@Override
			public int getStatus() {
				return WorkflowConstants.STATUS_DRAFT;
			}

			@Override
			public String getUserName() {
				return launchDraftLayout.getUserName();
			}

		};
	}

	private Version _toVersion(Layout layout) {
		return new Version() {

			@Override
			public String getLabel(Locale locale) {
				return _getLabel(layout, locale);
			}

			@Override
			public Date getModifiedDate() {
				return layout.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				return layout;
			}

			@Override
			public int getStatus() {
				if (layout.isDraftLayout()) {
					return WorkflowConstants.STATUS_DRAFT;
				}

				return layout.getStatus();
			}

			@Override
			public String getUserName() {
				return layout.getUserName();
			}

			@Override
			public String getVersionKey() {
				return String.valueOf(layout.getPlid());
			}

		};
	}

	/**
	 * Reports the page itself as the model to preview, because a version holds
	 * the page's content rather than a page of its own. Only the version that
	 * is live can be rendered, which is the one a launch that has not been
	 * superseded yet points at.
	 */
	private Version _toVersion(
		Layout layout, LayoutContentVersion layoutContentVersion) {

		return new Version() {

			@Override
			public String getLabel(Locale locale) {
				return String.valueOf(layoutContentVersion.getVersion());
			}

			@Override
			public Date getModifiedDate() {
				return layoutContentVersion.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				if (layoutContentVersion.getLayoutContentVersionId() ==
						_getLatestApprovedLayoutContentVersionId(layout)) {

					return layout;
				}

				Layout previewLayout = _getPreviewLayout(
					layout, layoutContentVersion);

				if (previewLayout == null) {
					return layout;
				}

				return new LaunchLayoutWrapper(previewLayout);
			}

			@Override
			public int getStatus() {
				return layoutContentVersion.getStatus();
			}

			@Override
			public String getUserName() {
				return layoutContentVersion.getUserName();
			}

			@Override
			public String getVersionKey() {
				return _getVersionKey(layoutContentVersion);
			}

		};
	}

	/**
	 * Reports the friendly URL of the layout the launch copies so that the
	 * preview stays on the live page's URL. Without this, the friendly URL
	 * servlet compares the requested URL against the copy's own friendly URL
	 * and redirects the browser to it, stranding the user on a layout that
	 * renders the copy no matter which launch is being previewed.
	 */
	private class LaunchLayoutWrapper extends LayoutWrapper {

		public LaunchLayoutWrapper(Layout launchDraftLayout) {
			super(launchDraftLayout);

			Layout layout = _layoutLocalService.fetchLayout(
				launchDraftLayout.getClassPK());

			if (layout == null) {
				layout = launchDraftLayout;
			}

			_layout = layout;
		}

		@Override
		public String getFriendlyURL() {
			return _layout.getFriendlyURL();
		}

		@Override
		public String getFriendlyURL(Locale locale) {
			return _layout.getFriendlyURL(locale);
		}

		@Override
		public Map<Locale, String> getFriendlyURLMap() {
			return _layout.getFriendlyURLMap();
		}

		@Override
		public String getFriendlyURLsXML() {
			return _layout.getFriendlyURLsXML();
		}

		private final Layout _layout;

	}

	private static final Snapshot<LayoutContentVersionCreator>
		_layoutContentVersionCreatorSnapshot = new Snapshot<>(
			LayoutLaunchEntryManager.class, LayoutContentVersionCreator.class,
			null, true);
	private static final Snapshot<LayoutContentVersionLocalService>
		_layoutContentVersionLocalServiceSnapshot = new Snapshot<>(
			LayoutLaunchEntryManager.class,
			LayoutContentVersionLocalService.class, null, true);
	private static final Snapshot<LayoutContentVersionRestorer>
		_layoutContentVersionRestorerSnapshot = new Snapshot<>(
			LayoutLaunchEntryManager.class, LayoutContentVersionRestorer.class,
			null, true);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutLaunchEntryManager.class);

}
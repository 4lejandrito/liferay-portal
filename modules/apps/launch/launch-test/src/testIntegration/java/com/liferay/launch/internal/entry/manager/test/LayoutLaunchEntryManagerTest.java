/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-10622"), @FeatureFlag("LPD-72278")}
)
@RunWith(Arquillian.class)
public class LayoutLaunchEntryManagerTest
	extends BaseLaunchEntryManagerTestCase {

	@Override
	@Test
	public void testAddDraft() throws Exception {
		super.testAddDraft();

		long classPK = addEntry();

		addDraft(classPK);

		Layout layout = _layoutLocalService.getLayout(classPK);

		Assert.assertTrue(layout.isPublished());

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		String externalReferenceCode = draftLayout.getExternalReferenceCode();

		Assert.assertFalse(
			externalReferenceCode,
			externalReferenceCode.contains(
				LayoutConstants.EXTERNAL_REFERENCE_CODE_INFIX_LAUNCH_DRAFT));
	}

	@Override
	@Test
	public void testGetPreviewBaseModel() throws Exception {
		super.testGetPreviewBaseModel();

		long classPK = addEntry();

		Layout layout = _layoutLocalService.getLayout(classPK);

		LaunchEntryManager launchEntryManager = getLaunchEntryManager();

		String title1 = RandomTestUtil.randomString();

		LaunchEntryManager.Draft draft1 = addDraft(classPK);

		updateDraft(title1, draft1);

		Layout launchDraftLayout = (Layout)draft1.getPreviewBaseModel();

		Assert.assertEquals(draft1.getDraftId(), launchDraftLayout.getPlid());

		Assert.assertEquals(
			layout.getFriendlyURL(), launchDraftLayout.getFriendlyURL());
		Assert.assertEquals(
			layout.getFriendlyURL(LocaleUtil.getSiteDefault()),
			launchDraftLayout.getFriendlyURL(LocaleUtil.getSiteDefault()));
		Assert.assertEquals(
			layout.getFriendlyURLMap(), launchDraftLayout.getFriendlyURLMap());

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		Assert.assertEquals(
			classPK,
			_getPreviewBaseModelPlid(
				classPK, publishedVersion1.getVersionKey()));

		String title2 = RandomTestUtil.randomString();

		LaunchEntryManager.Draft draft2 = addDraft(classPK);

		updateDraft(title2, draft2);

		LaunchEntryManager.Version publishedVersion2 =
			launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		long previewPlid = _getPreviewBaseModelPlid(
			classPK, publishedVersion1.getVersionKey());

		Assert.assertNotEquals(classPK, previewPlid);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		Assert.assertNotEquals(draftLayout.getPlid(), previewPlid);

		Layout previewLayout = _layoutLocalService.getLayout(previewPlid);

		Assert.assertEquals(
			title1, previewLayout.getName(LocaleUtil.getSiteDefault()));

		Assert.assertEquals(
			previewPlid,
			_getPreviewBaseModelPlid(
				classPK, publishedVersion1.getVersionKey()));

		List<String> versionKeys = TransformUtil.transform(
			launchEntryManager.getVersions(
				classPK, LocaleUtil.getSiteDefault()),
			LaunchEntryManager.Version::getVersionKey);

		Assert.assertFalse(
			versionKeys.toString(),
			versionKeys.contains(String.valueOf(previewPlid)));

		LaunchEntryManager.Draft draft3 = addDraft(classPK);

		updateDraft(RandomTestUtil.randomString(), draft3);

		launchEntryManager.publishDraft(classPK, draft3.getDraftId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		ServiceContextThreadLocal.popServiceContext();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(group.getCompanyId())));

			previewLayout = _layoutLocalService.getLayout(
				_getPreviewBaseModelPlid(
					classPK, publishedVersion2.getVersionKey()));

			Assert.assertEquals(
				title2, previewLayout.getName(LocaleUtil.getSiteDefault()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}
	}

	@Override
	protected long addEntry() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		ContentLayoutTestUtil.publishLayout(
			_layoutLocalService.fetchDraftLayout(layout.getPlid()), layout);

		return layout.getPlid();
	}

	@Override
	protected String getBackURLParameterName() {
		return "p_l_back_url";
	}

	@Override
	protected String getDraftTitle(LaunchEntryManager.Draft draft)
		throws Exception {

		Layout launchDraftLayout = _layoutLocalService.getLayout(
			draft.getDraftId());

		return launchDraftLayout.getName(LocaleUtil.getSiteDefault());
	}

	@Override
	protected String getEntryTitle(long classPK) throws Exception {
		Layout layout = _layoutLocalService.getLayout(classPK);

		return layout.getName(LocaleUtil.getSiteDefault());
	}

	@Override
	protected List<Serializable> getListedPrimaryKeys(long classPK)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(classPK);

		return TransformUtil.transform(
			_layoutLocalService.getLayouts(
				layout.getGroupId(), layout.isPrivateLayout()),
			Layout::getPlid);
	}

	@Override
	protected Class<?> getModelClass() {
		return Layout.class;
	}

	@Override
	protected Map<String, String> getSelectedItemData(long classPK) {
		return TreeMapBuilder.<String, String>create(
			String.CASE_INSENSITIVE_ORDER
		).put(
			"plid", String.valueOf(classPK)
		).build();
	}

	@Override
	protected List<Map<String, String>> getUnbranchableSelectedItemsData(
			long classPK)
		throws Exception {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		Layout typePortletLayout = LayoutTestUtil.addTypePortletLayout(group);

		return ListUtil.fromArray(
			getSelectedItemData(draftLayout.getPlid()),
			getSelectedItemData(typePortletLayout.getPlid()));
	}

	@Override
	protected void updateDraft(String title, LaunchEntryManager.Draft draft)
		throws Exception {

		_layoutLocalService.updateName(
			draft.getDraftId(), title,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
	}

	@Override
	protected void updateLiveEntry(String title, long classPK)
		throws Exception {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		_layoutLocalService.updateName(
			draftLayout.getPlid(), title,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

		Layout layout = _layoutLocalService.updateName(
			classPK, title,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

		ContentLayoutTestUtil.publishLayout(
			_layoutLocalService.fetchDraftLayout(classPK), layout);
	}

	private long _getPreviewBaseModelPlid(long classPK, String versionKey)
		throws Exception {

		LaunchEntryManager.Version version = getVersion(classPK, versionKey);

		Layout layout = (Layout)version.getPreviewBaseModel();

		return layout.getPlid();
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
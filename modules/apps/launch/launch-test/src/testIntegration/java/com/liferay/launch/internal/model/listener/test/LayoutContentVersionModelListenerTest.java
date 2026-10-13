/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-10622"), @FeatureFlag("LPD-72278")}
)
@RunWith(Arquillian.class)
public class LayoutContentVersionModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		long classPK = layout.getPlid();

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(
				Layout.class.getName());

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.fetchPublishedVersion(classPK);

		LaunchEntryManager.Draft draft1 = launchEntryManager.addDraft(
			classPK, publishedVersion.getVersionKey());

		LaunchEntryManager.Version publishedVersion1 =
			launchEntryManager.publishDraft(classPK, draft1.getDraftId());

		LaunchEntryManager.Draft draft2 = launchEntryManager.addDraft(
			classPK, publishedVersion1.getVersionKey());

		launchEntryManager.publishDraft(classPK, draft2.getDraftId());

		Layout previewLayout = (Layout)launchEntryManager.fetchVersion(
			classPK, publishedVersion1.getVersionKey()
		).getPreviewBaseModel();

		Assert.assertNotNull(
			_layoutLocalService.fetchLayout(previewLayout.getPlid()));

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.
				getLayoutContentVersionByExternalReferenceCode(
					publishedVersion1.getVersionKey(), _group.getGroupId());

		_layoutContentVersionLocalService.deleteLayoutContentVersion(
			layoutContentVersion.getLayoutContentVersionId());

		Assert.assertNull(
			_layoutLocalService.fetchLayout(previewLayout.getPlid()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

}
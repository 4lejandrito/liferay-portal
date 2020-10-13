/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotStagingTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia Garcia
 */
@RunWith(Arquillian.class)
public class StagingDepotEntryGroupRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveDepotEntry = _addDepotEntry();

		_liveGroup = _addGroup();
	}

	@Test
	public void testDepotEntryGroupRelDepotEntryStaged() throws Exception {
		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		Assert.assertNotNull(liveDepotEntryGroupRel);

		_stagingDepotEntry = _stageDepotEntry(_liveDepotEntry);

		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_stagingDepotEntry.getDepotEntryId(),
					_liveGroup.getGroupId()));
	}

	@Test
	public void testDepotEntryGroupRelLive() throws Exception {
		Assert.assertNotNull(
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId()));
	}

	@Test
	public void testDepotEntryGroupRelLiveThenStageDepotEntryAndGroup()
		throws Exception {

		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		Assert.assertNotNull(liveDepotEntryGroupRel);

		_stagingDepotEntry = _stageDepotEntry(_liveDepotEntry);

		_stagingGroup = DepotStagingTestUtil.enableLocalStaging(_liveGroup);

		DepotEntryGroupRel stagingDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_stagingDepotEntry.getDepotEntryId(),
					_stagingGroup.getGroupId());

		Assert.assertNotNull(stagingDepotEntryGroupRel);

		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_stagingDepotEntry.getDepotEntryId(),
					_liveGroup.getGroupId()));
		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_liveDepotEntry.getDepotEntryId(),
					_stagingGroup.getGroupId()));
	}

	@Test
	public void testDepotEntryGroupRelLiveThenStageGroupAndDepotEntry()
		throws Exception {

		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		Assert.assertNotNull(liveDepotEntryGroupRel);

		_stagingGroup = DepotStagingTestUtil.enableLocalStaging(_liveGroup);

		_stagingDepotEntry = _stageDepotEntry(_liveDepotEntry);

		DepotEntryGroupRel stagingDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_stagingDepotEntry.getDepotEntryId(),
					_stagingGroup.getGroupId());

		Assert.assertNull(stagingDepotEntryGroupRel);
	}

	@Test
	public void testDepotEntryGroupRelOnlyStaged() throws Exception {
		_stagingDepotEntry = _stageDepotEntry(_liveDepotEntry);
		_stagingGroup = DepotStagingTestUtil.enableLocalStaging(_liveGroup);

		DepotEntryGroupRel stagingDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_stagingDepotEntry.getDepotEntryId(),
				_stagingGroup.getGroupId());

		Assert.assertNotNull(stagingDepotEntryGroupRel);

		DepotEntryGroupRel liveDepotEntryGroupRel =
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_liveDepotEntry.getDepotEntryId(), _liveGroup.getGroupId());

		Assert.assertNull(liveDepotEntryGroupRel);

		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_stagingDepotEntry.getDepotEntryId(),
					_liveGroup.getGroupId()));
		Assert.assertNull(
			_depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					_liveDepotEntry.getDepotEntryId(),
					_stagingGroup.getGroupId()));
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext());
	}

	private Group _addGroup() throws Exception {
		return _groupLocalService.addGroup(
			TestPropsValues.getUserId(), 0, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			FriendlyURLNormalizerUtil.normalize(RandomTestUtil.randomString()),
			true, true, ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _stageDepotEntry(DepotEntry liveDepotEntry)
		throws Exception {

		Group stagingGroup = DepotStagingTestUtil.enableLocalStaging(
			liveDepotEntry.getGroup());

		return _depotEntryLocalService.fetchGroupDepotEntry(
			stagingGroup.getGroupId());
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private DepotEntry _liveDepotEntry;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private DepotEntry _stagingDepotEntry;
	private Group _stagingGroup;

}
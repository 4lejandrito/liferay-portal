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

package com.liferay.multichannel.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.multichannel.exception.NoSuchChannelScopeRelException;
import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.multichannel.service.ChannelScopeRelLocalServiceUtil;
import com.liferay.multichannel.service.persistence.ChannelScopeRelPersistence;
import com.liferay.multichannel.service.persistence.ChannelScopeRelUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class ChannelScopeRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.multichannel.service"));

	@Before
	public void setUp() {
		_persistence = ChannelScopeRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ChannelScopeRel> iterator = _channelScopeRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChannelScopeRel channelScopeRel = _persistence.create(pk);

		Assert.assertNotNull(channelScopeRel);

		Assert.assertEquals(channelScopeRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		_persistence.remove(newChannelScopeRel);

		ChannelScopeRel existingChannelScopeRel =
			_persistence.fetchByPrimaryKey(newChannelScopeRel.getPrimaryKey());

		Assert.assertNull(existingChannelScopeRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addChannelScopeRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChannelScopeRel newChannelScopeRel = _persistence.create(pk);

		newChannelScopeRel.setCompanyId(RandomTestUtil.nextLong());

		newChannelScopeRel.setChannelId(RandomTestUtil.nextLong());

		newChannelScopeRel.setScopeId(RandomTestUtil.nextLong());

		_channelScopeRels.add(_persistence.update(newChannelScopeRel));

		ChannelScopeRel existingChannelScopeRel = _persistence.findByPrimaryKey(
			newChannelScopeRel.getPrimaryKey());

		Assert.assertEquals(
			existingChannelScopeRel.getEntryId(),
			newChannelScopeRel.getEntryId());
		Assert.assertEquals(
			existingChannelScopeRel.getCompanyId(),
			newChannelScopeRel.getCompanyId());
		Assert.assertEquals(
			existingChannelScopeRel.getChannelId(),
			newChannelScopeRel.getChannelId());
		Assert.assertEquals(
			existingChannelScopeRel.getScopeId(),
			newChannelScopeRel.getScopeId());
	}

	@Test
	public void testCountByChannelId() throws Exception {
		_persistence.countByChannelId(RandomTestUtil.nextLong());

		_persistence.countByChannelId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		ChannelScopeRel existingChannelScopeRel = _persistence.findByPrimaryKey(
			newChannelScopeRel.getPrimaryKey());

		Assert.assertEquals(existingChannelScopeRel, newChannelScopeRel);
	}

	@Test(expected = NoSuchChannelScopeRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ChannelScopeRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ChannelScopeRel", "entryId", true, "companyId", true, "channelId",
			true, "scopeId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		ChannelScopeRel existingChannelScopeRel =
			_persistence.fetchByPrimaryKey(newChannelScopeRel.getPrimaryKey());

		Assert.assertEquals(existingChannelScopeRel, newChannelScopeRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChannelScopeRel missingChannelScopeRel = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingChannelScopeRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ChannelScopeRel newChannelScopeRel1 = addChannelScopeRel();
		ChannelScopeRel newChannelScopeRel2 = addChannelScopeRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChannelScopeRel1.getPrimaryKey());
		primaryKeys.add(newChannelScopeRel2.getPrimaryKey());

		Map<Serializable, ChannelScopeRel> channelScopeRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, channelScopeRels.size());
		Assert.assertEquals(
			newChannelScopeRel1,
			channelScopeRels.get(newChannelScopeRel1.getPrimaryKey()));
		Assert.assertEquals(
			newChannelScopeRel2,
			channelScopeRels.get(newChannelScopeRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ChannelScopeRel> channelScopeRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(channelScopeRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChannelScopeRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ChannelScopeRel> channelScopeRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, channelScopeRels.size());
		Assert.assertEquals(
			newChannelScopeRel,
			channelScopeRels.get(newChannelScopeRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ChannelScopeRel> channelScopeRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(channelScopeRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChannelScopeRel.getPrimaryKey());

		Map<Serializable, ChannelScopeRel> channelScopeRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, channelScopeRels.size());
		Assert.assertEquals(
			newChannelScopeRel,
			channelScopeRels.get(newChannelScopeRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ChannelScopeRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ChannelScopeRel>() {

				@Override
				public void performAction(ChannelScopeRel channelScopeRel) {
					Assert.assertNotNull(channelScopeRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ChannelScopeRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"entryId", newChannelScopeRel.getEntryId()));

		List<ChannelScopeRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ChannelScopeRel existingChannelScopeRel = result.get(0);

		Assert.assertEquals(existingChannelScopeRel, newChannelScopeRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ChannelScopeRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", RandomTestUtil.nextLong()));

		List<ChannelScopeRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ChannelScopeRel newChannelScopeRel = addChannelScopeRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ChannelScopeRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		Object newEntryId = newChannelScopeRel.getEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("entryId", new Object[] {newEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEntryId = result.get(0);

		Assert.assertEquals(existingEntryId, newEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ChannelScopeRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"entryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ChannelScopeRel addChannelScopeRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChannelScopeRel channelScopeRel = _persistence.create(pk);

		channelScopeRel.setCompanyId(RandomTestUtil.nextLong());

		channelScopeRel.setChannelId(RandomTestUtil.nextLong());

		channelScopeRel.setScopeId(RandomTestUtil.nextLong());

		_channelScopeRels.add(_persistence.update(channelScopeRel));

		return channelScopeRel;
	}

	private List<ChannelScopeRel> _channelScopeRels =
		new ArrayList<ChannelScopeRel>();
	private ChannelScopeRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
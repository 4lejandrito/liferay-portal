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

package com.liferay.frontend.editor.configuration.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.frontend.editor.configuration.exception.NoSuchCustomEditorConfigurationEntryException;
import com.liferay.frontend.editor.configuration.model.CustomEditorConfigurationEntry;
import com.liferay.frontend.editor.configuration.service.CustomEditorConfigurationEntryLocalServiceUtil;
import com.liferay.frontend.editor.configuration.service.persistence.CustomEditorConfigurationEntryPersistence;
import com.liferay.frontend.editor.configuration.service.persistence.CustomEditorConfigurationEntryUtil;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class CustomEditorConfigurationEntryPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED,
				"com.liferay.frontend.editor.configuration.service"));

	@Before
	public void setUp() {
		_persistence = CustomEditorConfigurationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CustomEditorConfigurationEntry> iterator = _customEditorConfigurationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CustomEditorConfigurationEntry customEditorConfigurationEntry = _persistence.create(pk);

		Assert.assertNotNull(customEditorConfigurationEntry);

		Assert.assertEquals(customEditorConfigurationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		_persistence.remove(newCustomEditorConfigurationEntry);

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = _persistence.fetchByPrimaryKey(newCustomEditorConfigurationEntry.getPrimaryKey());

		Assert.assertNull(existingCustomEditorConfigurationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCustomEditorConfigurationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = _persistence.create(pk);

		newCustomEditorConfigurationEntry.setUuid(RandomTestUtil.randomString());

		newCustomEditorConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		newCustomEditorConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		newCustomEditorConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCustomEditorConfigurationEntry.setPortletName(RandomTestUtil.randomString());

		newCustomEditorConfigurationEntry.setEditorName(RandomTestUtil.randomString());

		newCustomEditorConfigurationEntry.setEditorConfigKey(RandomTestUtil.randomString());

		newCustomEditorConfigurationEntry.setCustomConfiguration(RandomTestUtil.randomString());

		newCustomEditorConfigurationEntry.setEnabled(RandomTestUtil.randomBoolean());

		_customEditorConfigurationEntries.add(_persistence.update(
				newCustomEditorConfigurationEntry));

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = _persistence.findByPrimaryKey(newCustomEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingCustomEditorConfigurationEntry.getUuid(),
			newCustomEditorConfigurationEntry.getUuid());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getCustomEditorConfigurationEntryId(),
			newCustomEditorConfigurationEntry.getCustomEditorConfigurationEntryId());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getCompanyId(),
			newCustomEditorConfigurationEntry.getCompanyId());
		Assert.assertEquals(Time.getShortTimestamp(
				existingCustomEditorConfigurationEntry.getCreateDate()),
			Time.getShortTimestamp(
				newCustomEditorConfigurationEntry.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingCustomEditorConfigurationEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCustomEditorConfigurationEntry.getModifiedDate()));
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getPortletName(),
			newCustomEditorConfigurationEntry.getPortletName());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getEditorName(),
			newCustomEditorConfigurationEntry.getEditorName());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getEditorConfigKey(),
			newCustomEditorConfigurationEntry.getEditorConfigKey());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.getCustomConfiguration(),
			newCustomEditorConfigurationEntry.getCustomConfiguration());
		Assert.assertEquals(existingCustomEditorConfigurationEntry.isEnabled(),
			newCustomEditorConfigurationEntry.isEnabled());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByP_E_E() throws Exception {
		_persistence.countByP_E_E("", "", "");

		_persistence.countByP_E_E("null", "null", "null");

		_persistence.countByP_E_E((String)null, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = _persistence.findByPrimaryKey(newCustomEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingCustomEditorConfigurationEntry,
			newCustomEditorConfigurationEntry);
	}

	@Test(expected = NoSuchCustomEditorConfigurationEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<CustomEditorConfigurationEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("CustomEditorConfigurationEntry",
			"uuid", true, "customEditorConfigurationEntryId", true,
			"companyId", true, "createDate", true, "modifiedDate", true,
			"portletName", true, "editorName", true, "editorConfigKey", true,
			"customConfiguration", true, "enabled", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = _persistence.fetchByPrimaryKey(newCustomEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingCustomEditorConfigurationEntry,
			newCustomEditorConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CustomEditorConfigurationEntry missingCustomEditorConfigurationEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCustomEditorConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry1 = addCustomEditorConfigurationEntry();
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry2 = addCustomEditorConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCustomEditorConfigurationEntry1.getPrimaryKey());
		primaryKeys.add(newCustomEditorConfigurationEntry2.getPrimaryKey());

		Map<Serializable, CustomEditorConfigurationEntry> customEditorConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, customEditorConfigurationEntries.size());
		Assert.assertEquals(newCustomEditorConfigurationEntry1,
			customEditorConfigurationEntries.get(
				newCustomEditorConfigurationEntry1.getPrimaryKey()));
		Assert.assertEquals(newCustomEditorConfigurationEntry2,
			customEditorConfigurationEntries.get(
				newCustomEditorConfigurationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CustomEditorConfigurationEntry> customEditorConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(customEditorConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCustomEditorConfigurationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CustomEditorConfigurationEntry> customEditorConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, customEditorConfigurationEntries.size());
		Assert.assertEquals(newCustomEditorConfigurationEntry,
			customEditorConfigurationEntries.get(
				newCustomEditorConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CustomEditorConfigurationEntry> customEditorConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(customEditorConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCustomEditorConfigurationEntry.getPrimaryKey());

		Map<Serializable, CustomEditorConfigurationEntry> customEditorConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, customEditorConfigurationEntries.size());
		Assert.assertEquals(newCustomEditorConfigurationEntry,
			customEditorConfigurationEntries.get(
				newCustomEditorConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = CustomEditorConfigurationEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<CustomEditorConfigurationEntry>() {
				@Override
				public void performAction(
					CustomEditorConfigurationEntry customEditorConfigurationEntry) {
					Assert.assertNotNull(customEditorConfigurationEntry);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(CustomEditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq(
				"customEditorConfigurationEntryId",
				newCustomEditorConfigurationEntry.getCustomEditorConfigurationEntryId()));

		List<CustomEditorConfigurationEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = result.get(0);

		Assert.assertEquals(existingCustomEditorConfigurationEntry,
			newCustomEditorConfigurationEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(CustomEditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq(
				"customEditorConfigurationEntryId", RandomTestUtil.nextLong()));

		List<CustomEditorConfigurationEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(CustomEditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"customEditorConfigurationEntryId"));

		Object newCustomEditorConfigurationEntryId = newCustomEditorConfigurationEntry.getCustomEditorConfigurationEntryId();

		dynamicQuery.add(RestrictionsFactoryUtil.in(
				"customEditorConfigurationEntryId",
				new Object[] { newCustomEditorConfigurationEntryId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCustomEditorConfigurationEntryId = result.get(0);

		Assert.assertEquals(existingCustomEditorConfigurationEntryId,
			newCustomEditorConfigurationEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(CustomEditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"customEditorConfigurationEntryId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in(
				"customEditorConfigurationEntryId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CustomEditorConfigurationEntry newCustomEditorConfigurationEntry = addCustomEditorConfigurationEntry();

		_persistence.clearCache();

		CustomEditorConfigurationEntry existingCustomEditorConfigurationEntry = _persistence.findByPrimaryKey(newCustomEditorConfigurationEntry.getPrimaryKey());

		Assert.assertTrue(Objects.equals(
				existingCustomEditorConfigurationEntry.getPortletName(),
				ReflectionTestUtil.invoke(
					existingCustomEditorConfigurationEntry,
					"getOriginalPortletName", new Class<?>[0])));
		Assert.assertTrue(Objects.equals(
				existingCustomEditorConfigurationEntry.getEditorName(),
				ReflectionTestUtil.invoke(
					existingCustomEditorConfigurationEntry,
					"getOriginalEditorName", new Class<?>[0])));
		Assert.assertTrue(Objects.equals(
				existingCustomEditorConfigurationEntry.getEditorConfigKey(),
				ReflectionTestUtil.invoke(
					existingCustomEditorConfigurationEntry,
					"getOriginalEditorConfigKey", new Class<?>[0])));
	}

	protected CustomEditorConfigurationEntry addCustomEditorConfigurationEntry()
		throws Exception {
		long pk = RandomTestUtil.nextLong();

		CustomEditorConfigurationEntry customEditorConfigurationEntry = _persistence.create(pk);

		customEditorConfigurationEntry.setUuid(RandomTestUtil.randomString());

		customEditorConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		customEditorConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		customEditorConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		customEditorConfigurationEntry.setPortletName(RandomTestUtil.randomString());

		customEditorConfigurationEntry.setEditorName(RandomTestUtil.randomString());

		customEditorConfigurationEntry.setEditorConfigKey(RandomTestUtil.randomString());

		customEditorConfigurationEntry.setCustomConfiguration(RandomTestUtil.randomString());

		customEditorConfigurationEntry.setEnabled(RandomTestUtil.randomBoolean());

		_customEditorConfigurationEntries.add(_persistence.update(
				customEditorConfigurationEntry));

		return customEditorConfigurationEntry;
	}

	private List<CustomEditorConfigurationEntry> _customEditorConfigurationEntries =
		new ArrayList<CustomEditorConfigurationEntry>();
	private CustomEditorConfigurationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}
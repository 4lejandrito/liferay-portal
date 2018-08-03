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

import com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException;
import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;
import com.liferay.frontend.editor.configuration.service.EditorConfigurationEntryLocalServiceUtil;
import com.liferay.frontend.editor.configuration.service.persistence.EditorConfigurationEntryPersistence;
import com.liferay.frontend.editor.configuration.service.persistence.EditorConfigurationEntryUtil;

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
public class EditorConfigurationEntryPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED,
				"com.liferay.frontend.editor.configuration.service"));

	@Before
	public void setUp() {
		_persistence = EditorConfigurationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<EditorConfigurationEntry> iterator = _editorConfigurationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EditorConfigurationEntry editorConfigurationEntry = _persistence.create(pk);

		Assert.assertNotNull(editorConfigurationEntry);

		Assert.assertEquals(editorConfigurationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		_persistence.remove(newEditorConfigurationEntry);

		EditorConfigurationEntry existingEditorConfigurationEntry = _persistence.fetchByPrimaryKey(newEditorConfigurationEntry.getPrimaryKey());

		Assert.assertNull(existingEditorConfigurationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addEditorConfigurationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EditorConfigurationEntry newEditorConfigurationEntry = _persistence.create(pk);

		newEditorConfigurationEntry.setUuid(RandomTestUtil.randomString());

		newEditorConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		newEditorConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		newEditorConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		newEditorConfigurationEntry.setPortletName(RandomTestUtil.randomString());

		newEditorConfigurationEntry.setEditorName(RandomTestUtil.randomString());

		newEditorConfigurationEntry.setEditorConfigKey(RandomTestUtil.randomString());

		newEditorConfigurationEntry.setConfiguration(RandomTestUtil.randomString());

		newEditorConfigurationEntry.setEnabled(RandomTestUtil.randomBoolean());

		_editorConfigurationEntries.add(_persistence.update(
				newEditorConfigurationEntry));

		EditorConfigurationEntry existingEditorConfigurationEntry = _persistence.findByPrimaryKey(newEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingEditorConfigurationEntry.getUuid(),
			newEditorConfigurationEntry.getUuid());
		Assert.assertEquals(existingEditorConfigurationEntry.getEditorConfigurationEntryId(),
			newEditorConfigurationEntry.getEditorConfigurationEntryId());
		Assert.assertEquals(existingEditorConfigurationEntry.getCompanyId(),
			newEditorConfigurationEntry.getCompanyId());
		Assert.assertEquals(Time.getShortTimestamp(
				existingEditorConfigurationEntry.getCreateDate()),
			Time.getShortTimestamp(newEditorConfigurationEntry.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingEditorConfigurationEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newEditorConfigurationEntry.getModifiedDate()));
		Assert.assertEquals(existingEditorConfigurationEntry.getPortletName(),
			newEditorConfigurationEntry.getPortletName());
		Assert.assertEquals(existingEditorConfigurationEntry.getEditorName(),
			newEditorConfigurationEntry.getEditorName());
		Assert.assertEquals(existingEditorConfigurationEntry.getEditorConfigKey(),
			newEditorConfigurationEntry.getEditorConfigKey());
		Assert.assertEquals(existingEditorConfigurationEntry.getConfiguration(),
			newEditorConfigurationEntry.getConfiguration());
		Assert.assertEquals(existingEditorConfigurationEntry.isEnabled(),
			newEditorConfigurationEntry.isEnabled());
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
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		EditorConfigurationEntry existingEditorConfigurationEntry = _persistence.findByPrimaryKey(newEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingEditorConfigurationEntry,
			newEditorConfigurationEntry);
	}

	@Test(expected = NoSuchEditorConfigurationEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<EditorConfigurationEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("EditorConfigurationEntry",
			"uuid", true, "editorConfigurationEntryId", true, "companyId",
			true, "createDate", true, "modifiedDate", true, "portletName",
			true, "editorName", true, "editorConfigKey", true, "configuration",
			true, "enabled", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		EditorConfigurationEntry existingEditorConfigurationEntry = _persistence.fetchByPrimaryKey(newEditorConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(existingEditorConfigurationEntry,
			newEditorConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EditorConfigurationEntry missingEditorConfigurationEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingEditorConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry1 = addEditorConfigurationEntry();
		EditorConfigurationEntry newEditorConfigurationEntry2 = addEditorConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEditorConfigurationEntry1.getPrimaryKey());
		primaryKeys.add(newEditorConfigurationEntry2.getPrimaryKey());

		Map<Serializable, EditorConfigurationEntry> editorConfigurationEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, editorConfigurationEntries.size());
		Assert.assertEquals(newEditorConfigurationEntry1,
			editorConfigurationEntries.get(
				newEditorConfigurationEntry1.getPrimaryKey()));
		Assert.assertEquals(newEditorConfigurationEntry2,
			editorConfigurationEntries.get(
				newEditorConfigurationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, EditorConfigurationEntry> editorConfigurationEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(editorConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEditorConfigurationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, EditorConfigurationEntry> editorConfigurationEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, editorConfigurationEntries.size());
		Assert.assertEquals(newEditorConfigurationEntry,
			editorConfigurationEntries.get(
				newEditorConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, EditorConfigurationEntry> editorConfigurationEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(editorConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEditorConfigurationEntry.getPrimaryKey());

		Map<Serializable, EditorConfigurationEntry> editorConfigurationEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, editorConfigurationEntries.size());
		Assert.assertEquals(newEditorConfigurationEntry,
			editorConfigurationEntries.get(
				newEditorConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = EditorConfigurationEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<EditorConfigurationEntry>() {
				@Override
				public void performAction(
					EditorConfigurationEntry editorConfigurationEntry) {
					Assert.assertNotNull(editorConfigurationEntry);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(EditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq(
				"editorConfigurationEntryId",
				newEditorConfigurationEntry.getEditorConfigurationEntryId()));

		List<EditorConfigurationEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		EditorConfigurationEntry existingEditorConfigurationEntry = result.get(0);

		Assert.assertEquals(existingEditorConfigurationEntry,
			newEditorConfigurationEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(EditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq(
				"editorConfigurationEntryId", RandomTestUtil.nextLong()));

		List<EditorConfigurationEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(EditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"editorConfigurationEntryId"));

		Object newEditorConfigurationEntryId = newEditorConfigurationEntry.getEditorConfigurationEntryId();

		dynamicQuery.add(RestrictionsFactoryUtil.in(
				"editorConfigurationEntryId",
				new Object[] { newEditorConfigurationEntryId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEditorConfigurationEntryId = result.get(0);

		Assert.assertEquals(existingEditorConfigurationEntryId,
			newEditorConfigurationEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(EditorConfigurationEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"editorConfigurationEntryId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in(
				"editorConfigurationEntryId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		EditorConfigurationEntry newEditorConfigurationEntry = addEditorConfigurationEntry();

		_persistence.clearCache();

		EditorConfigurationEntry existingEditorConfigurationEntry = _persistence.findByPrimaryKey(newEditorConfigurationEntry.getPrimaryKey());

		Assert.assertTrue(Objects.equals(
				existingEditorConfigurationEntry.getPortletName(),
				ReflectionTestUtil.invoke(existingEditorConfigurationEntry,
					"getOriginalPortletName", new Class<?>[0])));
		Assert.assertTrue(Objects.equals(
				existingEditorConfigurationEntry.getEditorName(),
				ReflectionTestUtil.invoke(existingEditorConfigurationEntry,
					"getOriginalEditorName", new Class<?>[0])));
		Assert.assertTrue(Objects.equals(
				existingEditorConfigurationEntry.getEditorConfigKey(),
				ReflectionTestUtil.invoke(existingEditorConfigurationEntry,
					"getOriginalEditorConfigKey", new Class<?>[0])));
	}

	protected EditorConfigurationEntry addEditorConfigurationEntry()
		throws Exception {
		long pk = RandomTestUtil.nextLong();

		EditorConfigurationEntry editorConfigurationEntry = _persistence.create(pk);

		editorConfigurationEntry.setUuid(RandomTestUtil.randomString());

		editorConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		editorConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		editorConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		editorConfigurationEntry.setPortletName(RandomTestUtil.randomString());

		editorConfigurationEntry.setEditorName(RandomTestUtil.randomString());

		editorConfigurationEntry.setEditorConfigKey(RandomTestUtil.randomString());

		editorConfigurationEntry.setConfiguration(RandomTestUtil.randomString());

		editorConfigurationEntry.setEnabled(RandomTestUtil.randomBoolean());

		_editorConfigurationEntries.add(_persistence.update(
				editorConfigurationEntry));

		return editorConfigurationEntry;
	}

	private List<EditorConfigurationEntry> _editorConfigurationEntries = new ArrayList<EditorConfigurationEntry>();
	private EditorConfigurationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}
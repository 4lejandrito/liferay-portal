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

package com.liferay.frontend.editor.configuration.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException;
import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;
import com.liferay.frontend.editor.configuration.model.impl.EditorConfigurationEntryImpl;
import com.liferay.frontend.editor.configuration.model.impl.EditorConfigurationEntryModelImpl;
import com.liferay.frontend.editor.configuration.service.persistence.EditorConfigurationEntryPersistence;

import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyProvider;
import com.liferay.portal.kernel.service.persistence.CompanyProviderWrapper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the editor configuration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntryPersistence
 * @see com.liferay.frontend.editor.configuration.service.persistence.EditorConfigurationEntryUtil
 * @generated
 */
@ProviderType
public class EditorConfigurationEntryPersistenceImpl extends BasePersistenceImpl<EditorConfigurationEntry>
	implements EditorConfigurationEntryPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link EditorConfigurationEntryUtil} to access the editor configuration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = EditorConfigurationEntryImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] { String.class.getName() },
			EditorConfigurationEntryModelImpl.UUID_COLUMN_BITMASK |
			EditorConfigurationEntryModelImpl.CREATEDATE_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_UUID = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] { String.class.getName() });

	/**
	 * Returns all the editor configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the editor configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @return the range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid(String uuid, int start,
		int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid(String uuid, int start,
		int end, OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid(String uuid, int start,
		int end, OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID;
			finderArgs = new Object[] { uuid };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID;
			finderArgs = new Object[] { uuid, start, end, orderByComparator };
		}

		List<EditorConfigurationEntry> list = null;

		if (retrieveFromCache) {
			list = (List<EditorConfigurationEntry>)finderCache.getResult(finderPath,
					finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (EditorConfigurationEntry editorConfigurationEntry : list) {
					if (!Objects.equals(uuid, editorConfigurationEntry.getUuid())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(3 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_UUID_1);
			}
			else if (uuid.equals("")) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(EditorConfigurationEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				if (!pagination) {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry findByUuid_First(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByUuid_First(uuid,
				orderByComparator);

		if (editorConfigurationEntry != null) {
			return editorConfigurationEntry;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append("}");

		throw new NoSuchEditorConfigurationEntryException(msg.toString());
	}

	/**
	 * Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByUuid_First(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		List<EditorConfigurationEntry> list = findByUuid(uuid, 0, 1,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry findByUuid_Last(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByUuid_Last(uuid,
				orderByComparator);

		if (editorConfigurationEntry != null) {
			return editorConfigurationEntry;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append("}");

		throw new NoSuchEditorConfigurationEntryException(msg.toString());
	}

	/**
	 * Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByUuid_Last(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<EditorConfigurationEntry> list = findByUuid(uuid, count - 1,
				count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the editor configuration entries before and after the current editor configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param editorConfigurationEntryId the primary key of the current editor configuration entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry[] findByUuid_PrevAndNext(
		long editorConfigurationEntryId, String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = findByPrimaryKey(editorConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			EditorConfigurationEntry[] array = new EditorConfigurationEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(session, editorConfigurationEntry,
					uuid, orderByComparator, true);

			array[1] = editorConfigurationEntry;

			array[2] = getByUuid_PrevAndNext(session, editorConfigurationEntry,
					uuid, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected EditorConfigurationEntry getByUuid_PrevAndNext(Session session,
		EditorConfigurationEntry editorConfigurationEntry, String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid == null) {
			query.append(_FINDER_COLUMN_UUID_UUID_1);
		}
		else if (uuid.equals("")) {
			query.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			query.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(EditorConfigurationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		if (bindUuid) {
			qPos.add(uuid);
		}

		if (orderByComparator != null) {
			Object[] values = orderByComparator.getOrderByConditionValues(editorConfigurationEntry);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<EditorConfigurationEntry> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the editor configuration entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (EditorConfigurationEntry editorConfigurationEntry : findByUuid(
				uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
			remove(editorConfigurationEntry);
		}
	}

	/**
	 * Returns the number of editor configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching editor configuration entries
	 */
	@Override
	public int countByUuid(String uuid) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID;

		Object[] finderArgs = new Object[] { uuid };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_UUID_1);
			}
			else if (uuid.equals("")) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_UUID_1 = "editorConfigurationEntry.uuid IS NULL";
	private static final String _FINDER_COLUMN_UUID_UUID_2 = "editorConfigurationEntry.uuid = ?";
	private static final String _FINDER_COLUMN_UUID_UUID_3 = "(editorConfigurationEntry.uuid IS NULL OR editorConfigurationEntry.uuid = '')";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID_C = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C =
		new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] { String.class.getName(), Long.class.getName() },
			EditorConfigurationEntryModelImpl.UUID_COLUMN_BITMASK |
			EditorConfigurationEntryModelImpl.COMPANYID_COLUMN_BITMASK |
			EditorConfigurationEntryModelImpl.CREATEDATE_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_UUID_C = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] { String.class.getName(), Long.class.getName() });

	/**
	 * Returns all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId) {
		return findByUuid_C(uuid, companyId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @return the range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end) {
		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return findByUuid_C(uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C;
			finderArgs = new Object[] { uuid, companyId };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID_C;
			finderArgs = new Object[] {
					uuid, companyId,
					
					start, end, orderByComparator
				};
		}

		List<EditorConfigurationEntry> list = null;

		if (retrieveFromCache) {
			list = (List<EditorConfigurationEntry>)finderCache.getResult(finderPath,
					finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (EditorConfigurationEntry editorConfigurationEntry : list) {
					if (!Objects.equals(uuid, editorConfigurationEntry.getUuid()) ||
							(companyId != editorConfigurationEntry.getCompanyId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(4 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_1);
			}
			else if (uuid.equals("")) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(EditorConfigurationEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(companyId);

				if (!pagination) {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry findByUuid_C_First(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByUuid_C_First(uuid,
				companyId, orderByComparator);

		if (editorConfigurationEntry != null) {
			return editorConfigurationEntry;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(", companyId=");
		msg.append(companyId);

		msg.append("}");

		throw new NoSuchEditorConfigurationEntryException(msg.toString());
	}

	/**
	 * Returns the first editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByUuid_C_First(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		List<EditorConfigurationEntry> list = findByUuid_C(uuid, companyId, 0,
				1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry findByUuid_C_Last(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByUuid_C_Last(uuid,
				companyId, orderByComparator);

		if (editorConfigurationEntry != null) {
			return editorConfigurationEntry;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(", companyId=");
		msg.append(companyId);

		msg.append("}");

		throw new NoSuchEditorConfigurationEntryException(msg.toString());
	}

	/**
	 * Returns the last editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByUuid_C_Last(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<EditorConfigurationEntry> list = findByUuid_C(uuid, companyId,
				count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the editor configuration entries before and after the current editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param editorConfigurationEntryId the primary key of the current editor configuration entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry[] findByUuid_C_PrevAndNext(
		long editorConfigurationEntryId, String uuid, long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = findByPrimaryKey(editorConfigurationEntryId);

		Session session = null;

		try {
			session = openSession();

			EditorConfigurationEntry[] array = new EditorConfigurationEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(session,
					editorConfigurationEntry, uuid, companyId,
					orderByComparator, true);

			array[1] = editorConfigurationEntry;

			array[2] = getByUuid_C_PrevAndNext(session,
					editorConfigurationEntry, uuid, companyId,
					orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected EditorConfigurationEntry getByUuid_C_PrevAndNext(
		Session session, EditorConfigurationEntry editorConfigurationEntry,
		String uuid, long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(5 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(4);
		}

		query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid == null) {
			query.append(_FINDER_COLUMN_UUID_C_UUID_1);
		}
		else if (uuid.equals("")) {
			query.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			query.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(EditorConfigurationEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		if (bindUuid) {
			qPos.add(uuid);
		}

		qPos.add(companyId);

		if (orderByComparator != null) {
			Object[] values = orderByComparator.getOrderByConditionValues(editorConfigurationEntry);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<EditorConfigurationEntry> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the editor configuration entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (EditorConfigurationEntry editorConfigurationEntry : findByUuid_C(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
			remove(editorConfigurationEntry);
		}
	}

	/**
	 * Returns the number of editor configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching editor configuration entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID_C;

		Object[] finderArgs = new Object[] { uuid, companyId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_1);
			}
			else if (uuid.equals("")) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(companyId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_1 = "editorConfigurationEntry.uuid IS NULL AND ";
	private static final String _FINDER_COLUMN_UUID_C_UUID_2 = "editorConfigurationEntry.uuid = ? AND ";
	private static final String _FINDER_COLUMN_UUID_C_UUID_3 = "(editorConfigurationEntry.uuid IS NULL OR editorConfigurationEntry.uuid = '') AND ";
	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 = "editorConfigurationEntry.companyId = ?";
	public static final FinderPath FINDER_PATH_FETCH_BY_P_E_E = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class, FINDER_CLASS_NAME_ENTITY,
			"fetchByP_E_E",
			new String[] {
				String.class.getName(), String.class.getName(),
				String.class.getName()
			},
			EditorConfigurationEntryModelImpl.PORTLETNAME_COLUMN_BITMASK |
			EditorConfigurationEntryModelImpl.EDITORNAME_COLUMN_BITMASK |
			EditorConfigurationEntryModelImpl.EDITORCONFIGKEY_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_P_E_E = new FinderPath(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_E_E",
			new String[] {
				String.class.getName(), String.class.getName(),
				String.class.getName()
			});

	/**
	 * Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	 *
	 * @param portletName the portlet name
	 * @param editorName the editor name
	 * @param editorConfigKey the editor config key
	 * @return the matching editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry findByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByP_E_E(portletName,
				editorName, editorConfigKey);

		if (editorConfigurationEntry == null) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("portletName=");
			msg.append(portletName);

			msg.append(", editorName=");
			msg.append(editorName);

			msg.append(", editorConfigKey=");
			msg.append(editorConfigKey);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchEditorConfigurationEntryException(msg.toString());
		}

		return editorConfigurationEntry;
	}

	/**
	 * Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portletName the portlet name
	 * @param editorName the editor name
	 * @param editorConfigKey the editor config key
	 * @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey) {
		return fetchByP_E_E(portletName, editorName, editorConfigKey, true);
	}

	/**
	 * Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portletName the portlet name
	 * @param editorName the editor name
	 * @param editorConfigKey the editor config key
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] {
				portletName, editorName, editorConfigKey
			};

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_P_E_E,
					finderArgs, this);
		}

		if (result instanceof EditorConfigurationEntry) {
			EditorConfigurationEntry editorConfigurationEntry = (EditorConfigurationEntry)result;

			if (!Objects.equals(portletName,
						editorConfigurationEntry.getPortletName()) ||
					!Objects.equals(editorName,
						editorConfigurationEntry.getEditorName()) ||
					!Objects.equals(editorConfigKey,
						editorConfigurationEntry.getEditorConfigKey())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(5);

			query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindPortletName = false;

			if (portletName == null) {
				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_1);
			}
			else if (portletName.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_3);
			}
			else {
				bindPortletName = true;

				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_2);
			}

			boolean bindEditorName = false;

			if (editorName == null) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_1);
			}
			else if (editorName.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_3);
			}
			else {
				bindEditorName = true;

				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_2);
			}

			boolean bindEditorConfigKey = false;

			if (editorConfigKey == null) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_1);
			}
			else if (editorConfigKey.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_3);
			}
			else {
				bindEditorConfigKey = true;

				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindPortletName) {
					qPos.add(portletName);
				}

				if (bindEditorName) {
					qPos.add(editorName);
				}

				if (bindEditorConfigKey) {
					qPos.add(editorConfigKey);
				}

				List<EditorConfigurationEntry> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_P_E_E,
						finderArgs, list);
				}
				else {
					EditorConfigurationEntry editorConfigurationEntry = list.get(0);

					result = editorConfigurationEntry;

					cacheResult(editorConfigurationEntry);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_P_E_E, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (EditorConfigurationEntry)result;
		}
	}

	/**
	 * Removes the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; from the database.
	 *
	 * @param portletName the portlet name
	 * @param editorName the editor name
	 * @param editorConfigKey the editor config key
	 * @return the editor configuration entry that was removed
	 */
	@Override
	public EditorConfigurationEntry removeByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = findByP_E_E(portletName,
				editorName, editorConfigKey);

		return remove(editorConfigurationEntry);
	}

	/**
	 * Returns the number of editor configuration entries where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63;.
	 *
	 * @param portletName the portlet name
	 * @param editorName the editor name
	 * @param editorConfigKey the editor config key
	 * @return the number of matching editor configuration entries
	 */
	@Override
	public int countByP_E_E(String portletName, String editorName,
		String editorConfigKey) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_P_E_E;

		Object[] finderArgs = new Object[] {
				portletName, editorName, editorConfigKey
			};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_COUNT_EDITORCONFIGURATIONENTRY_WHERE);

			boolean bindPortletName = false;

			if (portletName == null) {
				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_1);
			}
			else if (portletName.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_3);
			}
			else {
				bindPortletName = true;

				query.append(_FINDER_COLUMN_P_E_E_PORTLETNAME_2);
			}

			boolean bindEditorName = false;

			if (editorName == null) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_1);
			}
			else if (editorName.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_3);
			}
			else {
				bindEditorName = true;

				query.append(_FINDER_COLUMN_P_E_E_EDITORNAME_2);
			}

			boolean bindEditorConfigKey = false;

			if (editorConfigKey == null) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_1);
			}
			else if (editorConfigKey.equals("")) {
				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_3);
			}
			else {
				bindEditorConfigKey = true;

				query.append(_FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindPortletName) {
					qPos.add(portletName);
				}

				if (bindEditorName) {
					qPos.add(editorName);
				}

				if (bindEditorConfigKey) {
					qPos.add(editorConfigKey);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_P_E_E_PORTLETNAME_1 = "editorConfigurationEntry.portletName IS NULL AND ";
	private static final String _FINDER_COLUMN_P_E_E_PORTLETNAME_2 = "editorConfigurationEntry.portletName = ? AND ";
	private static final String _FINDER_COLUMN_P_E_E_PORTLETNAME_3 = "(editorConfigurationEntry.portletName IS NULL OR editorConfigurationEntry.portletName = '') AND ";
	private static final String _FINDER_COLUMN_P_E_E_EDITORNAME_1 = "editorConfigurationEntry.editorName IS NULL AND ";
	private static final String _FINDER_COLUMN_P_E_E_EDITORNAME_2 = "editorConfigurationEntry.editorName = ? AND ";
	private static final String _FINDER_COLUMN_P_E_E_EDITORNAME_3 = "(editorConfigurationEntry.editorName IS NULL OR editorConfigurationEntry.editorName = '') AND ";
	private static final String _FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_1 = "editorConfigurationEntry.editorConfigKey IS NULL";
	private static final String _FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_2 = "editorConfigurationEntry.editorConfigKey = ?";
	private static final String _FINDER_COLUMN_P_E_E_EDITORCONFIGKEY_3 = "(editorConfigurationEntry.editorConfigKey IS NULL OR editorConfigurationEntry.editorConfigKey = '')";

	public EditorConfigurationEntryPersistenceImpl() {
		setModelClass(EditorConfigurationEntry.class);

		try {
			Field field = BasePersistenceImpl.class.getDeclaredField(
					"_dbColumnNames");

			field.setAccessible(true);

			Map<String, String> dbColumnNames = new HashMap<String, String>();

			dbColumnNames.put("uuid", "uuid_");

			field.set(this, dbColumnNames);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}
	}

	/**
	 * Caches the editor configuration entry in the entity cache if it is enabled.
	 *
	 * @param editorConfigurationEntry the editor configuration entry
	 */
	@Override
	public void cacheResult(EditorConfigurationEntry editorConfigurationEntry) {
		entityCache.putResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			editorConfigurationEntry.getPrimaryKey(), editorConfigurationEntry);

		finderCache.putResult(FINDER_PATH_FETCH_BY_P_E_E,
			new Object[] {
				editorConfigurationEntry.getPortletName(),
				editorConfigurationEntry.getEditorName(),
				editorConfigurationEntry.getEditorConfigKey()
			}, editorConfigurationEntry);

		editorConfigurationEntry.resetOriginalValues();
	}

	/**
	 * Caches the editor configuration entries in the entity cache if it is enabled.
	 *
	 * @param editorConfigurationEntries the editor configuration entries
	 */
	@Override
	public void cacheResult(
		List<EditorConfigurationEntry> editorConfigurationEntries) {
		for (EditorConfigurationEntry editorConfigurationEntry : editorConfigurationEntries) {
			if (entityCache.getResult(
						EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
						EditorConfigurationEntryImpl.class,
						editorConfigurationEntry.getPrimaryKey()) == null) {
				cacheResult(editorConfigurationEntry);
			}
			else {
				editorConfigurationEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all editor configuration entries.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(EditorConfigurationEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the editor configuration entry.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(EditorConfigurationEntry editorConfigurationEntry) {
		entityCache.removeResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			editorConfigurationEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((EditorConfigurationEntryModelImpl)editorConfigurationEntry,
			true);
	}

	@Override
	public void clearCache(
		List<EditorConfigurationEntry> editorConfigurationEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (EditorConfigurationEntry editorConfigurationEntry : editorConfigurationEntries) {
			entityCache.removeResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
				EditorConfigurationEntryImpl.class,
				editorConfigurationEntry.getPrimaryKey());

			clearUniqueFindersCache((EditorConfigurationEntryModelImpl)editorConfigurationEntry,
				true);
		}
	}

	protected void cacheUniqueFindersCache(
		EditorConfigurationEntryModelImpl editorConfigurationEntryModelImpl) {
		Object[] args = new Object[] {
				editorConfigurationEntryModelImpl.getPortletName(),
				editorConfigurationEntryModelImpl.getEditorName(),
				editorConfigurationEntryModelImpl.getEditorConfigKey()
			};

		finderCache.putResult(FINDER_PATH_COUNT_BY_P_E_E, args,
			Long.valueOf(1), false);
		finderCache.putResult(FINDER_PATH_FETCH_BY_P_E_E, args,
			editorConfigurationEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		EditorConfigurationEntryModelImpl editorConfigurationEntryModelImpl,
		boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] {
					editorConfigurationEntryModelImpl.getPortletName(),
					editorConfigurationEntryModelImpl.getEditorName(),
					editorConfigurationEntryModelImpl.getEditorConfigKey()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_P_E_E, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_P_E_E, args);
		}

		if ((editorConfigurationEntryModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_P_E_E.getColumnBitmask()) != 0) {
			Object[] args = new Object[] {
					editorConfigurationEntryModelImpl.getOriginalPortletName(),
					editorConfigurationEntryModelImpl.getOriginalEditorName(),
					editorConfigurationEntryModelImpl.getOriginalEditorConfigKey()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_P_E_E, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_P_E_E, args);
		}
	}

	/**
	 * Creates a new editor configuration entry with the primary key. Does not add the editor configuration entry to the database.
	 *
	 * @param editorConfigurationEntryId the primary key for the new editor configuration entry
	 * @return the new editor configuration entry
	 */
	@Override
	public EditorConfigurationEntry create(long editorConfigurationEntryId) {
		EditorConfigurationEntry editorConfigurationEntry = new EditorConfigurationEntryImpl();

		editorConfigurationEntry.setNew(true);
		editorConfigurationEntry.setPrimaryKey(editorConfigurationEntryId);

		String uuid = PortalUUIDUtil.generate();

		editorConfigurationEntry.setUuid(uuid);

		editorConfigurationEntry.setCompanyId(companyProvider.getCompanyId());

		return editorConfigurationEntry;
	}

	/**
	 * Removes the editor configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param editorConfigurationEntryId the primary key of the editor configuration entry
	 * @return the editor configuration entry that was removed
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry remove(long editorConfigurationEntryId)
		throws NoSuchEditorConfigurationEntryException {
		return remove((Serializable)editorConfigurationEntryId);
	}

	/**
	 * Removes the editor configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the editor configuration entry
	 * @return the editor configuration entry that was removed
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry remove(Serializable primaryKey)
		throws NoSuchEditorConfigurationEntryException {
		Session session = null;

		try {
			session = openSession();

			EditorConfigurationEntry editorConfigurationEntry = (EditorConfigurationEntry)session.get(EditorConfigurationEntryImpl.class,
					primaryKey);

			if (editorConfigurationEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEditorConfigurationEntryException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(editorConfigurationEntry);
		}
		catch (NoSuchEditorConfigurationEntryException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected EditorConfigurationEntry removeImpl(
		EditorConfigurationEntry editorConfigurationEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(editorConfigurationEntry)) {
				editorConfigurationEntry = (EditorConfigurationEntry)session.get(EditorConfigurationEntryImpl.class,
						editorConfigurationEntry.getPrimaryKeyObj());
			}

			if (editorConfigurationEntry != null) {
				session.delete(editorConfigurationEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (editorConfigurationEntry != null) {
			clearCache(editorConfigurationEntry);
		}

		return editorConfigurationEntry;
	}

	@Override
	public EditorConfigurationEntry updateImpl(
		EditorConfigurationEntry editorConfigurationEntry) {
		boolean isNew = editorConfigurationEntry.isNew();

		if (!(editorConfigurationEntry instanceof EditorConfigurationEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(editorConfigurationEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(editorConfigurationEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in editorConfigurationEntry proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom EditorConfigurationEntry implementation " +
				editorConfigurationEntry.getClass());
		}

		EditorConfigurationEntryModelImpl editorConfigurationEntryModelImpl = (EditorConfigurationEntryModelImpl)editorConfigurationEntry;

		if (Validator.isNull(editorConfigurationEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			editorConfigurationEntry.setUuid(uuid);
		}

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (editorConfigurationEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				editorConfigurationEntry.setCreateDate(now);
			}
			else {
				editorConfigurationEntry.setCreateDate(serviceContext.getCreateDate(
						now));
			}
		}

		if (!editorConfigurationEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				editorConfigurationEntry.setModifiedDate(now);
			}
			else {
				editorConfigurationEntry.setModifiedDate(serviceContext.getModifiedDate(
						now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (editorConfigurationEntry.isNew()) {
				session.save(editorConfigurationEntry);

				editorConfigurationEntry.setNew(false);
			}
			else {
				editorConfigurationEntry = (EditorConfigurationEntry)session.merge(editorConfigurationEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!EditorConfigurationEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			Object[] args = new Object[] {
					editorConfigurationEntryModelImpl.getUuid()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
				args);

			args = new Object[] {
					editorConfigurationEntryModelImpl.getUuid(),
					editorConfigurationEntryModelImpl.getCompanyId()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
				args);

			finderCache.removeResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL,
				FINDER_ARGS_EMPTY);
		}

		else {
			if ((editorConfigurationEntryModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						editorConfigurationEntryModelImpl.getOriginalUuid()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);

				args = new Object[] { editorConfigurationEntryModelImpl.getUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);
			}

			if ((editorConfigurationEntryModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						editorConfigurationEntryModelImpl.getOriginalUuid(),
						editorConfigurationEntryModelImpl.getOriginalCompanyId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
					args);

				args = new Object[] {
						editorConfigurationEntryModelImpl.getUuid(),
						editorConfigurationEntryModelImpl.getCompanyId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
					args);
			}
		}

		entityCache.putResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
			EditorConfigurationEntryImpl.class,
			editorConfigurationEntry.getPrimaryKey(), editorConfigurationEntry,
			false);

		clearUniqueFindersCache(editorConfigurationEntryModelImpl, false);
		cacheUniqueFindersCache(editorConfigurationEntryModelImpl);

		editorConfigurationEntry.resetOriginalValues();

		return editorConfigurationEntry;
	}

	/**
	 * Returns the editor configuration entry with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the editor configuration entry
	 * @return the editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEditorConfigurationEntryException {
		EditorConfigurationEntry editorConfigurationEntry = fetchByPrimaryKey(primaryKey);

		if (editorConfigurationEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEditorConfigurationEntryException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return editorConfigurationEntry;
	}

	/**
	 * Returns the editor configuration entry with the primary key or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	 *
	 * @param editorConfigurationEntryId the primary key of the editor configuration entry
	 * @return the editor configuration entry
	 * @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry findByPrimaryKey(
		long editorConfigurationEntryId)
		throws NoSuchEditorConfigurationEntryException {
		return findByPrimaryKey((Serializable)editorConfigurationEntryId);
	}

	/**
	 * Returns the editor configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the editor configuration entry
	 * @return the editor configuration entry, or <code>null</code> if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
				EditorConfigurationEntryImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		EditorConfigurationEntry editorConfigurationEntry = (EditorConfigurationEntry)serializable;

		if (editorConfigurationEntry == null) {
			Session session = null;

			try {
				session = openSession();

				editorConfigurationEntry = (EditorConfigurationEntry)session.get(EditorConfigurationEntryImpl.class,
						primaryKey);

				if (editorConfigurationEntry != null) {
					cacheResult(editorConfigurationEntry);
				}
				else {
					entityCache.putResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
						EditorConfigurationEntryImpl.class, primaryKey,
						nullModel);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
					EditorConfigurationEntryImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return editorConfigurationEntry;
	}

	/**
	 * Returns the editor configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param editorConfigurationEntryId the primary key of the editor configuration entry
	 * @return the editor configuration entry, or <code>null</code> if a editor configuration entry with the primary key could not be found
	 */
	@Override
	public EditorConfigurationEntry fetchByPrimaryKey(
		long editorConfigurationEntryId) {
		return fetchByPrimaryKey((Serializable)editorConfigurationEntryId);
	}

	@Override
	public Map<Serializable, EditorConfigurationEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, EditorConfigurationEntry> map = new HashMap<Serializable, EditorConfigurationEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			EditorConfigurationEntry editorConfigurationEntry = fetchByPrimaryKey(primaryKey);

			if (editorConfigurationEntry != null) {
				map.put(primaryKey, editorConfigurationEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
					EditorConfigurationEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (EditorConfigurationEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
			query.append((long)primaryKey);

			query.append(",");
		}

		query.setIndex(query.index() - 1);

		query.append(")");

		String sql = query.toString();

		Session session = null;

		try {
			session = openSession();

			Query q = session.createQuery(sql);

			for (EditorConfigurationEntry editorConfigurationEntry : (List<EditorConfigurationEntry>)q.list()) {
				map.put(editorConfigurationEntry.getPrimaryKeyObj(),
					editorConfigurationEntry);

				cacheResult(editorConfigurationEntry);

				uncachedPrimaryKeys.remove(editorConfigurationEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(EditorConfigurationEntryModelImpl.ENTITY_CACHE_ENABLED,
					EditorConfigurationEntryImpl.class, primaryKey, nullModel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the editor configuration entries.
	 *
	 * @return the editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the editor configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @return the range of editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findAll(int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the editor configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of editor configuration entries
	 * @param end the upper bound of the range of editor configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of editor configuration entries
	 */
	@Override
	public List<EditorConfigurationEntry> findAll(int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_ALL;
			finderArgs = new Object[] { start, end, orderByComparator };
		}

		List<EditorConfigurationEntry> list = null;

		if (retrieveFromCache) {
			list = (List<EditorConfigurationEntry>)finderCache.getResult(finderPath,
					finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_EDITORCONFIGURATIONENTRY);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_EDITORCONFIGURATIONENTRY;

				if (pagination) {
					sql = sql.concat(EditorConfigurationEntryModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<EditorConfigurationEntry>)QueryUtil.list(q,
							getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the editor configuration entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (EditorConfigurationEntry editorConfigurationEntry : findAll()) {
			remove(editorConfigurationEntry);
		}
	}

	/**
	 * Returns the number of editor configuration entries.
	 *
	 * @return the number of editor configuration entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_EDITORCONFIGURATIONENTRY);

				count = (Long)q.uniqueResult();

				finderCache.putResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY,
					count);
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_COUNT_ALL,
					FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return EditorConfigurationEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the editor configuration entry persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(EditorConfigurationEntryImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = CompanyProviderWrapper.class)
	protected CompanyProvider companyProvider;
	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_EDITORCONFIGURATIONENTRY = "SELECT editorConfigurationEntry FROM EditorConfigurationEntry editorConfigurationEntry";
	private static final String _SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE_PKS_IN =
		"SELECT editorConfigurationEntry FROM EditorConfigurationEntry editorConfigurationEntry WHERE editorConfigurationEntryId IN (";
	private static final String _SQL_SELECT_EDITORCONFIGURATIONENTRY_WHERE = "SELECT editorConfigurationEntry FROM EditorConfigurationEntry editorConfigurationEntry WHERE ";
	private static final String _SQL_COUNT_EDITORCONFIGURATIONENTRY = "SELECT COUNT(editorConfigurationEntry) FROM EditorConfigurationEntry editorConfigurationEntry";
	private static final String _SQL_COUNT_EDITORCONFIGURATIONENTRY_WHERE = "SELECT COUNT(editorConfigurationEntry) FROM EditorConfigurationEntry editorConfigurationEntry WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "editorConfigurationEntry.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No EditorConfigurationEntry exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No EditorConfigurationEntry exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(EditorConfigurationEntryPersistenceImpl.class);
	private static final Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"uuid"
			});
}
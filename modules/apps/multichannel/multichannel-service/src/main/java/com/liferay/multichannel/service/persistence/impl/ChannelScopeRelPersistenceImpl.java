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

package com.liferay.multichannel.service.persistence.impl;

import com.liferay.multichannel.exception.NoSuchChannelScopeRelException;
import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.multichannel.model.impl.ChannelScopeRelImpl;
import com.liferay.multichannel.model.impl.ChannelScopeRelModelImpl;
import com.liferay.multichannel.service.persistence.ChannelScopeRelPersistence;
import com.liferay.multichannel.service.persistence.impl.constants.MultichannelPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the channel scope rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ChannelScopeRelPersistence.class)
@ProviderType
public class ChannelScopeRelPersistenceImpl
	extends BasePersistenceImpl<ChannelScopeRel>
	implements ChannelScopeRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ChannelScopeRelUtil</code> to access the channel scope rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ChannelScopeRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByChannelId;
	private FinderPath _finderPathWithoutPaginationFindByChannelId;
	private FinderPath _finderPathCountByChannelId;

	/**
	 * Returns all the channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the matching channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findByChannelId(long channelId) {
		return findByChannelId(
			channelId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the channel scope rels where channelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param channelId the channel ID
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @return the range of matching channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end) {

		return findByChannelId(channelId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the channel scope rels where channelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param channelId the channel ID
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator) {

		return findByChannelId(channelId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the channel scope rels where channelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param channelId the channel ID
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator,
		boolean retrieveFromCache) {

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindByChannelId;
			finderArgs = new Object[] {channelId};
		}
		else {
			finderPath = _finderPathWithPaginationFindByChannelId;
			finderArgs = new Object[] {
				channelId, start, end, orderByComparator
			};
		}

		List<ChannelScopeRel> list = null;

		if (retrieveFromCache) {
			list = (List<ChannelScopeRel>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ChannelScopeRel channelScopeRel : list) {
					if ((channelId != channelScopeRel.getChannelId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_CHANNELSCOPEREL_WHERE);

			query.append(_FINDER_COLUMN_CHANNELID_CHANNELID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else if (pagination) {
				query.append(ChannelScopeRelModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(channelId);

				if (!pagination) {
					list = (List<ChannelScopeRel>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<ChannelScopeRel>)QueryUtil.list(
						q, getDialect(), start, end);
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
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	@Override
	public ChannelScopeRel findByChannelId_First(
			long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws NoSuchChannelScopeRelException {

		ChannelScopeRel channelScopeRel = fetchByChannelId_First(
			channelId, orderByComparator);

		if (channelScopeRel != null) {
			return channelScopeRel;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("channelId=");
		msg.append(channelId);

		msg.append("}");

		throw new NoSuchChannelScopeRelException(msg.toString());
	}

	/**
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	@Override
	public ChannelScopeRel fetchByChannelId_First(
		long channelId, OrderByComparator<ChannelScopeRel> orderByComparator) {

		List<ChannelScopeRel> list = findByChannelId(
			channelId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	@Override
	public ChannelScopeRel findByChannelId_Last(
			long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws NoSuchChannelScopeRelException {

		ChannelScopeRel channelScopeRel = fetchByChannelId_Last(
			channelId, orderByComparator);

		if (channelScopeRel != null) {
			return channelScopeRel;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("channelId=");
		msg.append(channelId);

		msg.append("}");

		throw new NoSuchChannelScopeRelException(msg.toString());
	}

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	@Override
	public ChannelScopeRel fetchByChannelId_Last(
		long channelId, OrderByComparator<ChannelScopeRel> orderByComparator) {

		int count = countByChannelId(channelId);

		if (count == 0) {
			return null;
		}

		List<ChannelScopeRel> list = findByChannelId(
			channelId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the channel scope rels before and after the current channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param entryId the primary key of the current channel scope rel
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel[] findByChannelId_PrevAndNext(
			long entryId, long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws NoSuchChannelScopeRelException {

		ChannelScopeRel channelScopeRel = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			ChannelScopeRel[] array = new ChannelScopeRelImpl[3];

			array[0] = getByChannelId_PrevAndNext(
				session, channelScopeRel, channelId, orderByComparator, true);

			array[1] = channelScopeRel;

			array[2] = getByChannelId_PrevAndNext(
				session, channelScopeRel, channelId, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected ChannelScopeRel getByChannelId_PrevAndNext(
		Session session, ChannelScopeRel channelScopeRel, long channelId,
		OrderByComparator<ChannelScopeRel> orderByComparator,
		boolean previous) {

		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_CHANNELSCOPEREL_WHERE);

		query.append(_FINDER_COLUMN_CHANNELID_CHANNELID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

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
			query.append(ChannelScopeRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		qPos.add(channelId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						channelScopeRel)) {

				qPos.add(orderByConditionValue);
			}
		}

		List<ChannelScopeRel> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the channel scope rels where channelId = &#63; from the database.
	 *
	 * @param channelId the channel ID
	 */
	@Override
	public void removeByChannelId(long channelId) {
		for (ChannelScopeRel channelScopeRel :
				findByChannelId(
					channelId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(channelScopeRel);
		}
	}

	/**
	 * Returns the number of channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the number of matching channel scope rels
	 */
	@Override
	public int countByChannelId(long channelId) {
		FinderPath finderPath = _finderPathCountByChannelId;

		Object[] finderArgs = new Object[] {channelId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_CHANNELSCOPEREL_WHERE);

			query.append(_FINDER_COLUMN_CHANNELID_CHANNELID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(channelId);

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

	private static final String _FINDER_COLUMN_CHANNELID_CHANNELID_2 =
		"channelScopeRel.channelId = ?";

	public ChannelScopeRelPersistenceImpl() {
		setModelClass(ChannelScopeRel.class);

		setModelImplClass(ChannelScopeRelImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the channel scope rel in the entity cache if it is enabled.
	 *
	 * @param channelScopeRel the channel scope rel
	 */
	@Override
	public void cacheResult(ChannelScopeRel channelScopeRel) {
		entityCache.putResult(
			entityCacheEnabled, ChannelScopeRelImpl.class,
			channelScopeRel.getPrimaryKey(), channelScopeRel);

		channelScopeRel.resetOriginalValues();
	}

	/**
	 * Caches the channel scope rels in the entity cache if it is enabled.
	 *
	 * @param channelScopeRels the channel scope rels
	 */
	@Override
	public void cacheResult(List<ChannelScopeRel> channelScopeRels) {
		for (ChannelScopeRel channelScopeRel : channelScopeRels) {
			if (entityCache.getResult(
					entityCacheEnabled, ChannelScopeRelImpl.class,
					channelScopeRel.getPrimaryKey()) == null) {

				cacheResult(channelScopeRel);
			}
			else {
				channelScopeRel.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all channel scope rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ChannelScopeRelImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the channel scope rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ChannelScopeRel channelScopeRel) {
		entityCache.removeResult(
			entityCacheEnabled, ChannelScopeRelImpl.class,
			channelScopeRel.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<ChannelScopeRel> channelScopeRels) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (ChannelScopeRel channelScopeRel : channelScopeRels) {
			entityCache.removeResult(
				entityCacheEnabled, ChannelScopeRelImpl.class,
				channelScopeRel.getPrimaryKey());
		}
	}

	/**
	 * Creates a new channel scope rel with the primary key. Does not add the channel scope rel to the database.
	 *
	 * @param entryId the primary key for the new channel scope rel
	 * @return the new channel scope rel
	 */
	@Override
	public ChannelScopeRel create(long entryId) {
		ChannelScopeRel channelScopeRel = new ChannelScopeRelImpl();

		channelScopeRel.setNew(true);
		channelScopeRel.setPrimaryKey(entryId);

		channelScopeRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return channelScopeRel;
	}

	/**
	 * Removes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel remove(long entryId)
		throws NoSuchChannelScopeRelException {

		return remove((Serializable)entryId);
	}

	/**
	 * Removes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel remove(Serializable primaryKey)
		throws NoSuchChannelScopeRelException {

		Session session = null;

		try {
			session = openSession();

			ChannelScopeRel channelScopeRel = (ChannelScopeRel)session.get(
				ChannelScopeRelImpl.class, primaryKey);

			if (channelScopeRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchChannelScopeRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(channelScopeRel);
		}
		catch (NoSuchChannelScopeRelException nsee) {
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
	protected ChannelScopeRel removeImpl(ChannelScopeRel channelScopeRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(channelScopeRel)) {
				channelScopeRel = (ChannelScopeRel)session.get(
					ChannelScopeRelImpl.class,
					channelScopeRel.getPrimaryKeyObj());
			}

			if (channelScopeRel != null) {
				session.delete(channelScopeRel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (channelScopeRel != null) {
			clearCache(channelScopeRel);
		}

		return channelScopeRel;
	}

	@Override
	public ChannelScopeRel updateImpl(ChannelScopeRel channelScopeRel) {
		boolean isNew = channelScopeRel.isNew();

		if (!(channelScopeRel instanceof ChannelScopeRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(channelScopeRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					channelScopeRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in channelScopeRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ChannelScopeRel implementation " +
					channelScopeRel.getClass());
		}

		ChannelScopeRelModelImpl channelScopeRelModelImpl =
			(ChannelScopeRelModelImpl)channelScopeRel;

		Session session = null;

		try {
			session = openSession();

			if (channelScopeRel.isNew()) {
				session.save(channelScopeRel);

				channelScopeRel.setNew(false);
			}
			else {
				channelScopeRel = (ChannelScopeRel)session.merge(
					channelScopeRel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!_columnBitmaskEnabled) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {
				channelScopeRelModelImpl.getChannelId()
			};

			finderCache.removeResult(_finderPathCountByChannelId, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByChannelId, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((channelScopeRelModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByChannelId.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					channelScopeRelModelImpl.getOriginalChannelId()
				};

				finderCache.removeResult(_finderPathCountByChannelId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByChannelId, args);

				args = new Object[] {channelScopeRelModelImpl.getChannelId()};

				finderCache.removeResult(_finderPathCountByChannelId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByChannelId, args);
			}
		}

		entityCache.putResult(
			entityCacheEnabled, ChannelScopeRelImpl.class,
			channelScopeRel.getPrimaryKey(), channelScopeRel, false);

		channelScopeRel.resetOriginalValues();

		return channelScopeRel;
	}

	/**
	 * Returns the channel scope rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchChannelScopeRelException {

		ChannelScopeRel channelScopeRel = fetchByPrimaryKey(primaryKey);

		if (channelScopeRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchChannelScopeRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return channelScopeRel;
	}

	/**
	 * Returns the channel scope rel with the primary key or throws a <code>NoSuchChannelScopeRelException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel findByPrimaryKey(long entryId)
		throws NoSuchChannelScopeRelException {

		return findByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns the channel scope rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel, or <code>null</code> if a channel scope rel with the primary key could not be found
	 */
	@Override
	public ChannelScopeRel fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns all the channel scope rels.
	 *
	 * @return the channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the channel scope rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @return the range of channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the channel scope rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findAll(
		int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the channel scope rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of channel scope rels
	 */
	@Override
	public List<ChannelScopeRel> findAll(
		int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator,
		boolean retrieveFromCache) {

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindAll;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<ChannelScopeRel> list = null;

		if (retrieveFromCache) {
			list = (List<ChannelScopeRel>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_CHANNELSCOPEREL);

				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_CHANNELSCOPEREL;

				if (pagination) {
					sql = sql.concat(ChannelScopeRelModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<ChannelScopeRel>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<ChannelScopeRel>)QueryUtil.list(
						q, getDialect(), start, end);
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
	 * Removes all the channel scope rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ChannelScopeRel channelScopeRel : findAll()) {
			remove(channelScopeRel);
		}
	}

	/**
	 * Returns the number of channel scope rels.
	 *
	 * @return the number of channel scope rels
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_CHANNELSCOPEREL);

				count = (Long)q.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception e) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CHANNELSCOPEREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ChannelScopeRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the channel scope rel persistence.
	 */
	@Activate
	public void activate() {
		ChannelScopeRelModelImpl.setEntityCacheEnabled(entityCacheEnabled);
		ChannelScopeRelModelImpl.setFinderCacheEnabled(finderCacheEnabled);

		_finderPathWithPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ChannelScopeRelImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ChannelScopeRelImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByChannelId = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ChannelScopeRelImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByChannelId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByChannelId = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ChannelScopeRelImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByChannelId",
			new String[] {Long.class.getName()},
			ChannelScopeRelModelImpl.CHANNELID_COLUMN_BITMASK);

		_finderPathCountByChannelId = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByChannelId",
			new String[] {Long.class.getName()});
	}

	@Deactivate
	public void deactivate() {
		entityCache.removeCache(ChannelScopeRelImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	@Reference(
		target = MultichannelPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
		super.setConfiguration(configuration);

		_columnBitmaskEnabled = GetterUtil.getBoolean(
			configuration.get(
				"value.object.column.bitmask.enabled.com.liferay.multichannel.model.ChannelScopeRel"),
			true);
	}

	@Override
	@Reference(
		target = MultichannelPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MultichannelPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private boolean _columnBitmaskEnabled;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CHANNELSCOPEREL =
		"SELECT channelScopeRel FROM ChannelScopeRel channelScopeRel";

	private static final String _SQL_SELECT_CHANNELSCOPEREL_WHERE =
		"SELECT channelScopeRel FROM ChannelScopeRel channelScopeRel WHERE ";

	private static final String _SQL_COUNT_CHANNELSCOPEREL =
		"SELECT COUNT(channelScopeRel) FROM ChannelScopeRel channelScopeRel";

	private static final String _SQL_COUNT_CHANNELSCOPEREL_WHERE =
		"SELECT COUNT(channelScopeRel) FROM ChannelScopeRel channelScopeRel WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "channelScopeRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ChannelScopeRel exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ChannelScopeRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ChannelScopeRelPersistenceImpl.class);

}
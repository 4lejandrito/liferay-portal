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

package com.liferay.multichannel.service.persistence;

import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the channel scope rel service. This utility wraps <code>com.liferay.multichannel.service.persistence.impl.ChannelScopeRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ChannelScopeRelPersistence
 * @generated
 */
@ProviderType
public class ChannelScopeRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(ChannelScopeRel channelScopeRel) {
		getPersistence().clearCache(channelScopeRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, ChannelScopeRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ChannelScopeRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ChannelScopeRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ChannelScopeRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ChannelScopeRel update(ChannelScopeRel channelScopeRel) {
		return getPersistence().update(channelScopeRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ChannelScopeRel update(
		ChannelScopeRel channelScopeRel, ServiceContext serviceContext) {

		return getPersistence().update(channelScopeRel, serviceContext);
	}

	/**
	 * Returns all the channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the matching channel scope rels
	 */
	public static List<ChannelScopeRel> findByChannelId(long channelId) {
		return getPersistence().findByChannelId(channelId);
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
	public static List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end) {

		return getPersistence().findByChannelId(channelId, start, end);
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
	public static List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator) {

		return getPersistence().findByChannelId(
			channelId, start, end, orderByComparator);
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
	public static List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findByChannelId(
			channelId, start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	public static ChannelScopeRel findByChannelId_First(
			long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws com.liferay.multichannel.exception.
			NoSuchChannelScopeRelException {

		return getPersistence().findByChannelId_First(
			channelId, orderByComparator);
	}

	/**
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	public static ChannelScopeRel fetchByChannelId_First(
		long channelId, OrderByComparator<ChannelScopeRel> orderByComparator) {

		return getPersistence().fetchByChannelId_First(
			channelId, orderByComparator);
	}

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	public static ChannelScopeRel findByChannelId_Last(
			long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws com.liferay.multichannel.exception.
			NoSuchChannelScopeRelException {

		return getPersistence().findByChannelId_Last(
			channelId, orderByComparator);
	}

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	public static ChannelScopeRel fetchByChannelId_Last(
		long channelId, OrderByComparator<ChannelScopeRel> orderByComparator) {

		return getPersistence().fetchByChannelId_Last(
			channelId, orderByComparator);
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
	public static ChannelScopeRel[] findByChannelId_PrevAndNext(
			long entryId, long channelId,
			OrderByComparator<ChannelScopeRel> orderByComparator)
		throws com.liferay.multichannel.exception.
			NoSuchChannelScopeRelException {

		return getPersistence().findByChannelId_PrevAndNext(
			entryId, channelId, orderByComparator);
	}

	/**
	 * Removes all the channel scope rels where channelId = &#63; from the database.
	 *
	 * @param channelId the channel ID
	 */
	public static void removeByChannelId(long channelId) {
		getPersistence().removeByChannelId(channelId);
	}

	/**
	 * Returns the number of channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the number of matching channel scope rels
	 */
	public static int countByChannelId(long channelId) {
		return getPersistence().countByChannelId(channelId);
	}

	/**
	 * Caches the channel scope rel in the entity cache if it is enabled.
	 *
	 * @param channelScopeRel the channel scope rel
	 */
	public static void cacheResult(ChannelScopeRel channelScopeRel) {
		getPersistence().cacheResult(channelScopeRel);
	}

	/**
	 * Caches the channel scope rels in the entity cache if it is enabled.
	 *
	 * @param channelScopeRels the channel scope rels
	 */
	public static void cacheResult(List<ChannelScopeRel> channelScopeRels) {
		getPersistence().cacheResult(channelScopeRels);
	}

	/**
	 * Creates a new channel scope rel with the primary key. Does not add the channel scope rel to the database.
	 *
	 * @param entryId the primary key for the new channel scope rel
	 * @return the new channel scope rel
	 */
	public static ChannelScopeRel create(long entryId) {
		return getPersistence().create(entryId);
	}

	/**
	 * Removes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	public static ChannelScopeRel remove(long entryId)
		throws com.liferay.multichannel.exception.
			NoSuchChannelScopeRelException {

		return getPersistence().remove(entryId);
	}

	public static ChannelScopeRel updateImpl(ChannelScopeRel channelScopeRel) {
		return getPersistence().updateImpl(channelScopeRel);
	}

	/**
	 * Returns the channel scope rel with the primary key or throws a <code>NoSuchChannelScopeRelException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	public static ChannelScopeRel findByPrimaryKey(long entryId)
		throws com.liferay.multichannel.exception.
			NoSuchChannelScopeRelException {

		return getPersistence().findByPrimaryKey(entryId);
	}

	/**
	 * Returns the channel scope rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel, or <code>null</code> if a channel scope rel with the primary key could not be found
	 */
	public static ChannelScopeRel fetchByPrimaryKey(long entryId) {
		return getPersistence().fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns all the channel scope rels.
	 *
	 * @return the channel scope rels
	 */
	public static List<ChannelScopeRel> findAll() {
		return getPersistence().findAll();
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
	public static List<ChannelScopeRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<ChannelScopeRel> findAll(
		int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<ChannelScopeRel> findAll(
		int start, int end,
		OrderByComparator<ChannelScopeRel> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Removes all the channel scope rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of channel scope rels.
	 *
	 * @return the number of channel scope rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ChannelScopeRelPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<ChannelScopeRelPersistence, ChannelScopeRelPersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ChannelScopeRelPersistence.class);

		ServiceTracker<ChannelScopeRelPersistence, ChannelScopeRelPersistence>
			serviceTracker =
				new ServiceTracker
					<ChannelScopeRelPersistence, ChannelScopeRelPersistence>(
						bundle.getBundleContext(),
						ChannelScopeRelPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}
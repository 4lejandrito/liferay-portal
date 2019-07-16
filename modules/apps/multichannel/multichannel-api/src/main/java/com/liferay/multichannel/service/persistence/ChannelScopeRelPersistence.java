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

import com.liferay.multichannel.exception.NoSuchChannelScopeRelException;
import com.liferay.multichannel.model.ChannelScopeRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the channel scope rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ChannelScopeRelUtil
 * @generated
 */
@ProviderType
public interface ChannelScopeRelPersistence
	extends BasePersistence<ChannelScopeRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ChannelScopeRelUtil} to access the channel scope rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the matching channel scope rels
	 */
	public java.util.List<ChannelScopeRel> findByChannelId(long channelId);

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
	public java.util.List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end);

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
	public java.util.List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator);

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
	public java.util.List<ChannelScopeRel> findByChannelId(
		long channelId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	public ChannelScopeRel findByChannelId_First(
			long channelId,
			com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
				orderByComparator)
		throws NoSuchChannelScopeRelException;

	/**
	 * Returns the first channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	public ChannelScopeRel fetchByChannelId_First(
		long channelId,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator);

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel
	 * @throws NoSuchChannelScopeRelException if a matching channel scope rel could not be found
	 */
	public ChannelScopeRel findByChannelId_Last(
			long channelId,
			com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
				orderByComparator)
		throws NoSuchChannelScopeRelException;

	/**
	 * Returns the last channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching channel scope rel, or <code>null</code> if a matching channel scope rel could not be found
	 */
	public ChannelScopeRel fetchByChannelId_Last(
		long channelId,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator);

	/**
	 * Returns the channel scope rels before and after the current channel scope rel in the ordered set where channelId = &#63;.
	 *
	 * @param entryId the primary key of the current channel scope rel
	 * @param channelId the channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	public ChannelScopeRel[] findByChannelId_PrevAndNext(
			long entryId, long channelId,
			com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
				orderByComparator)
		throws NoSuchChannelScopeRelException;

	/**
	 * Removes all the channel scope rels where channelId = &#63; from the database.
	 *
	 * @param channelId the channel ID
	 */
	public void removeByChannelId(long channelId);

	/**
	 * Returns the number of channel scope rels where channelId = &#63;.
	 *
	 * @param channelId the channel ID
	 * @return the number of matching channel scope rels
	 */
	public int countByChannelId(long channelId);

	/**
	 * Caches the channel scope rel in the entity cache if it is enabled.
	 *
	 * @param channelScopeRel the channel scope rel
	 */
	public void cacheResult(ChannelScopeRel channelScopeRel);

	/**
	 * Caches the channel scope rels in the entity cache if it is enabled.
	 *
	 * @param channelScopeRels the channel scope rels
	 */
	public void cacheResult(java.util.List<ChannelScopeRel> channelScopeRels);

	/**
	 * Creates a new channel scope rel with the primary key. Does not add the channel scope rel to the database.
	 *
	 * @param entryId the primary key for the new channel scope rel
	 * @return the new channel scope rel
	 */
	public ChannelScopeRel create(long entryId);

	/**
	 * Removes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	public ChannelScopeRel remove(long entryId)
		throws NoSuchChannelScopeRelException;

	public ChannelScopeRel updateImpl(ChannelScopeRel channelScopeRel);

	/**
	 * Returns the channel scope rel with the primary key or throws a <code>NoSuchChannelScopeRelException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws NoSuchChannelScopeRelException if a channel scope rel with the primary key could not be found
	 */
	public ChannelScopeRel findByPrimaryKey(long entryId)
		throws NoSuchChannelScopeRelException;

	/**
	 * Returns the channel scope rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel, or <code>null</code> if a channel scope rel with the primary key could not be found
	 */
	public ChannelScopeRel fetchByPrimaryKey(long entryId);

	/**
	 * Returns all the channel scope rels.
	 *
	 * @return the channel scope rels
	 */
	public java.util.List<ChannelScopeRel> findAll();

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
	public java.util.List<ChannelScopeRel> findAll(int start, int end);

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
	public java.util.List<ChannelScopeRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator);

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
	public java.util.List<ChannelScopeRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ChannelScopeRel>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the channel scope rels from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of channel scope rels.
	 *
	 * @return the number of channel scope rels
	 */
	public int countAll();

}
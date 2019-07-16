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

package com.liferay.multichannel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a wrapper for {@link ChannelScopeRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ChannelScopeRelLocalService
 * @generated
 */
@ProviderType
public class ChannelScopeRelLocalServiceWrapper
	implements ChannelScopeRelLocalService,
			   ServiceWrapper<ChannelScopeRelLocalService> {

	public ChannelScopeRelLocalServiceWrapper(
		ChannelScopeRelLocalService channelScopeRelLocalService) {

		_channelScopeRelLocalService = channelScopeRelLocalService;
	}

	/**
	 * Adds the channel scope rel to the database. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was added
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel addChannelScopeRel(
		com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return _channelScopeRelLocalService.addChannelScopeRel(channelScopeRel);
	}

	/**
	 * Creates a new channel scope rel with the primary key. Does not add the channel scope rel to the database.
	 *
	 * @param entryId the primary key for the new channel scope rel
	 * @return the new channel scope rel
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel createChannelScopeRel(
		long entryId) {

		return _channelScopeRelLocalService.createChannelScopeRel(entryId);
	}

	/**
	 * Deletes the channel scope rel from the database. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was removed
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel deleteChannelScopeRel(
		com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return _channelScopeRelLocalService.deleteChannelScopeRel(
			channelScopeRel);
	}

	/**
	 * Deletes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws PortalException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel deleteChannelScopeRel(
			long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _channelScopeRelLocalService.deleteChannelScopeRel(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _channelScopeRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _channelScopeRelLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _channelScopeRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multichannel.model.impl.ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _channelScopeRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multichannel.model.impl.ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _channelScopeRelLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _channelScopeRelLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _channelScopeRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.multichannel.model.ChannelScopeRel fetchChannelScopeRel(
		long entryId) {

		return _channelScopeRelLocalService.fetchChannelScopeRel(entryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _channelScopeRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Group>
			getChannelGroups(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _channelScopeRelLocalService.getChannelGroups(groupId);
	}

	/**
	 * Returns the channel scope rel with the primary key.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws PortalException if a channel scope rel with the primary key could not be found
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel getChannelScopeRel(
			long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _channelScopeRelLocalService.getChannelScopeRel(entryId);
	}

	/**
	 * Returns a range of all the channel scope rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multichannel.model.impl.ChannelScopeRelModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of channel scope rels
	 * @param end the upper bound of the range of channel scope rels (not inclusive)
	 * @return the range of channel scope rels
	 */
	@Override
	public java.util.List<com.liferay.multichannel.model.ChannelScopeRel>
		getChannelScopeRels(int start, int end) {

		return _channelScopeRelLocalService.getChannelScopeRels(start, end);
	}

	/**
	 * Returns the number of channel scope rels.
	 *
	 * @return the number of channel scope rels
	 */
	@Override
	public int getChannelScopeRelsCount() {
		return _channelScopeRelLocalService.getChannelScopeRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _channelScopeRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _channelScopeRelLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _channelScopeRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the channel scope rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was updated
	 */
	@Override
	public com.liferay.multichannel.model.ChannelScopeRel updateChannelScopeRel(
		com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return _channelScopeRelLocalService.updateChannelScopeRel(
			channelScopeRel);
	}

	@Override
	public ChannelScopeRelLocalService getWrappedService() {
		return _channelScopeRelLocalService;
	}

	@Override
	public void setWrappedService(
		ChannelScopeRelLocalService channelScopeRelLocalService) {

		_channelScopeRelLocalService = channelScopeRelLocalService;
	}

	private ChannelScopeRelLocalService _channelScopeRelLocalService;

}
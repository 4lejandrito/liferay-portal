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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for ChannelScopeRel. This utility wraps
 * <code>com.liferay.multichannel.service.impl.ChannelScopeRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ChannelScopeRelLocalService
 * @generated
 */
@ProviderType
public class ChannelScopeRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.multichannel.service.impl.ChannelScopeRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the channel scope rel to the database. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was added
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
		addChannelScopeRel(
			com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return getService().addChannelScopeRel(channelScopeRel);
	}

	/**
	 * Creates a new channel scope rel with the primary key. Does not add the channel scope rel to the database.
	 *
	 * @param entryId the primary key for the new channel scope rel
	 * @return the new channel scope rel
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
		createChannelScopeRel(long entryId) {

		return getService().createChannelScopeRel(entryId);
	}

	/**
	 * Deletes the channel scope rel from the database. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was removed
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
		deleteChannelScopeRel(
			com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return getService().deleteChannelScopeRel(channelScopeRel);
	}

	/**
	 * Deletes the channel scope rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel that was removed
	 * @throws PortalException if a channel scope rel with the primary key could not be found
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
			deleteChannelScopeRel(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteChannelScopeRel(entryId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.multichannel.model.ChannelScopeRel
		fetchChannelScopeRel(long entryId) {

		return getService().fetchChannelScopeRel(entryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
			getChannelGroups(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getChannelGroups(groupId);
	}

	/**
	 * Returns the channel scope rel with the primary key.
	 *
	 * @param entryId the primary key of the channel scope rel
	 * @return the channel scope rel
	 * @throws PortalException if a channel scope rel with the primary key could not be found
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
			getChannelScopeRel(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getChannelScopeRel(entryId);
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
	public static java.util.List<com.liferay.multichannel.model.ChannelScopeRel>
		getChannelScopeRels(int start, int end) {

		return getService().getChannelScopeRels(start, end);
	}

	/**
	 * Returns the number of channel scope rels.
	 *
	 * @return the number of channel scope rels
	 */
	public static int getChannelScopeRelsCount() {
		return getService().getChannelScopeRelsCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the channel scope rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param channelScopeRel the channel scope rel
	 * @return the channel scope rel that was updated
	 */
	public static com.liferay.multichannel.model.ChannelScopeRel
		updateChannelScopeRel(
			com.liferay.multichannel.model.ChannelScopeRel channelScopeRel) {

		return getService().updateChannelScopeRel(channelScopeRel);
	}

	public static ChannelScopeRelLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<ChannelScopeRelLocalService, ChannelScopeRelLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ChannelScopeRelLocalService.class);

		ServiceTracker<ChannelScopeRelLocalService, ChannelScopeRelLocalService>
			serviceTracker =
				new ServiceTracker
					<ChannelScopeRelLocalService, ChannelScopeRelLocalService>(
						bundle.getBundleContext(),
						ChannelScopeRelLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}
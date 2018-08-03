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

package com.liferay.frontend.editor.configuration.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the editor configuration entry service. This utility wraps {@link com.liferay.frontend.editor.configuration.service.persistence.impl.EditorConfigurationEntryPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntryPersistence
 * @see com.liferay.frontend.editor.configuration.service.persistence.impl.EditorConfigurationEntryPersistenceImpl
 * @generated
 */
@ProviderType
public class EditorConfigurationEntryUtil {
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
	public static void clearCache(
		EditorConfigurationEntry editorConfigurationEntry) {
		getPersistence().clearCache(editorConfigurationEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<EditorConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<EditorConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<EditorConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static EditorConfigurationEntry update(
		EditorConfigurationEntry editorConfigurationEntry) {
		return getPersistence().update(editorConfigurationEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static EditorConfigurationEntry update(
		EditorConfigurationEntry editorConfigurationEntry,
		ServiceContext serviceContext) {
		return getPersistence().update(editorConfigurationEntry, serviceContext);
	}

	/**
	* Returns all the editor configuration entries where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching editor configuration entries
	*/
	public static List<EditorConfigurationEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
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
	public static List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
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
	public static List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
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
	public static List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByUuid(uuid, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry findByUuid_First(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry fetchByUuid_First(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry findByUuid_Last(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry fetchByUuid_Last(String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
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
	public static EditorConfigurationEntry[] findByUuid_PrevAndNext(
		long editorConfigurationEntryId, String uuid,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .findByUuid_PrevAndNext(editorConfigurationEntryId, uuid,
			orderByComparator);
	}

	/**
	* Removes all the editor configuration entries where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	* Returns the number of editor configuration entries where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching editor configuration entries
	*/
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	* Returns all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the matching editor configuration entries
	*/
	public static List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
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
	public static List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end) {
		return getPersistence().findByUuid_C(uuid, companyId, start, end);
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
	public static List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence()
				   .findByUuid_C(uuid, companyId, start, end, orderByComparator);
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
	public static List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByUuid_C(uuid, companyId, start, end,
			orderByComparator, retrieveFromCache);
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
	public static EditorConfigurationEntry findByUuid_C_First(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .findByUuid_C_First(uuid, companyId, orderByComparator);
	}

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry fetchByUuid_C_First(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence()
				   .fetchByUuid_C_First(uuid, companyId, orderByComparator);
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
	public static EditorConfigurationEntry findByUuid_C_Last(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .findByUuid_C_Last(uuid, companyId, orderByComparator);
	}

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry fetchByUuid_C_Last(String uuid,
		long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence()
				   .fetchByUuid_C_Last(uuid, companyId, orderByComparator);
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
	public static EditorConfigurationEntry[] findByUuid_C_PrevAndNext(
		long editorConfigurationEntryId, String uuid, long companyId,
		OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .findByUuid_C_PrevAndNext(editorConfigurationEntryId, uuid,
			companyId, orderByComparator);
	}

	/**
	* Removes all the editor configuration entries where uuid = &#63; and companyId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	*/
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	* Returns the number of editor configuration entries where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the number of matching editor configuration entries
	*/
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	* Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry findByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .findByP_E_E(portletName, editorName, editorConfigKey);
	}

	/**
	* Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public static EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey) {
		return getPersistence()
				   .fetchByP_E_E(portletName, editorName, editorConfigKey);
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
	public static EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey, boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByP_E_E(portletName, editorName, editorConfigKey,
			retrieveFromCache);
	}

	/**
	* Removes the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; from the database.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the editor configuration entry that was removed
	*/
	public static EditorConfigurationEntry removeByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence()
				   .removeByP_E_E(portletName, editorName, editorConfigKey);
	}

	/**
	* Returns the number of editor configuration entries where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63;.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the number of matching editor configuration entries
	*/
	public static int countByP_E_E(String portletName, String editorName,
		String editorConfigKey) {
		return getPersistence()
				   .countByP_E_E(portletName, editorName, editorConfigKey);
	}

	/**
	* Caches the editor configuration entry in the entity cache if it is enabled.
	*
	* @param editorConfigurationEntry the editor configuration entry
	*/
	public static void cacheResult(
		EditorConfigurationEntry editorConfigurationEntry) {
		getPersistence().cacheResult(editorConfigurationEntry);
	}

	/**
	* Caches the editor configuration entries in the entity cache if it is enabled.
	*
	* @param editorConfigurationEntries the editor configuration entries
	*/
	public static void cacheResult(
		List<EditorConfigurationEntry> editorConfigurationEntries) {
		getPersistence().cacheResult(editorConfigurationEntries);
	}

	/**
	* Creates a new editor configuration entry with the primary key. Does not add the editor configuration entry to the database.
	*
	* @param editorConfigurationEntryId the primary key for the new editor configuration entry
	* @return the new editor configuration entry
	*/
	public static EditorConfigurationEntry create(
		long editorConfigurationEntryId) {
		return getPersistence().create(editorConfigurationEntryId);
	}

	/**
	* Removes the editor configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry that was removed
	* @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	*/
	public static EditorConfigurationEntry remove(
		long editorConfigurationEntryId)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence().remove(editorConfigurationEntryId);
	}

	public static EditorConfigurationEntry updateImpl(
		EditorConfigurationEntry editorConfigurationEntry) {
		return getPersistence().updateImpl(editorConfigurationEntry);
	}

	/**
	* Returns the editor configuration entry with the primary key or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	*/
	public static EditorConfigurationEntry findByPrimaryKey(
		long editorConfigurationEntryId)
		throws com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException {
		return getPersistence().findByPrimaryKey(editorConfigurationEntryId);
	}

	/**
	* Returns the editor configuration entry with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry, or <code>null</code> if a editor configuration entry with the primary key could not be found
	*/
	public static EditorConfigurationEntry fetchByPrimaryKey(
		long editorConfigurationEntryId) {
		return getPersistence().fetchByPrimaryKey(editorConfigurationEntryId);
	}

	public static java.util.Map<java.io.Serializable, EditorConfigurationEntry> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the editor configuration entries.
	*
	* @return the editor configuration entries
	*/
	public static List<EditorConfigurationEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<EditorConfigurationEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<EditorConfigurationEntry> findAll(int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<EditorConfigurationEntry> findAll(int start, int end,
		OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the editor configuration entries from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of editor configuration entries.
	*
	* @return the number of editor configuration entries
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static java.util.Set<String> getBadColumnNames() {
		return getPersistence().getBadColumnNames();
	}

	public static EditorConfigurationEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<EditorConfigurationEntryPersistence, EditorConfigurationEntryPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(EditorConfigurationEntryPersistence.class);

		ServiceTracker<EditorConfigurationEntryPersistence, EditorConfigurationEntryPersistence> serviceTracker =
			new ServiceTracker<EditorConfigurationEntryPersistence, EditorConfigurationEntryPersistence>(bundle.getBundleContext(),
				EditorConfigurationEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}
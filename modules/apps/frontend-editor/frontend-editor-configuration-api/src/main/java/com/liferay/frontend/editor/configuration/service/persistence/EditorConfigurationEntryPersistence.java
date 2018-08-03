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

import com.liferay.frontend.editor.configuration.exception.NoSuchEditorConfigurationEntryException;
import com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * The persistence interface for the editor configuration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.frontend.editor.configuration.service.persistence.impl.EditorConfigurationEntryPersistenceImpl
 * @see EditorConfigurationEntryUtil
 * @generated
 */
@ProviderType
public interface EditorConfigurationEntryPersistence extends BasePersistence<EditorConfigurationEntry> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link EditorConfigurationEntryUtil} to access the editor configuration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the editor configuration entries where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching editor configuration entries
	*/
	public java.util.List<EditorConfigurationEntry> findByUuid(String uuid);

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
	public java.util.List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end);

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
	public java.util.List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

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
	public java.util.List<EditorConfigurationEntry> findByUuid(String uuid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry findByUuid_First(String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByUuid_First(String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry findByUuid_Last(String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByUuid_Last(String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

	/**
	* Returns the editor configuration entries before and after the current editor configuration entry in the ordered set where uuid = &#63;.
	*
	* @param editorConfigurationEntryId the primary key of the current editor configuration entry
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	*/
	public EditorConfigurationEntry[] findByUuid_PrevAndNext(
		long editorConfigurationEntryId, String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Removes all the editor configuration entries where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public void removeByUuid(String uuid);

	/**
	* Returns the number of editor configuration entries where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching editor configuration entries
	*/
	public int countByUuid(String uuid);

	/**
	* Returns all the editor configuration entries where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the matching editor configuration entries
	*/
	public java.util.List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId);

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
	public java.util.List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end);

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
	public java.util.List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

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
	public java.util.List<EditorConfigurationEntry> findByUuid_C(String uuid,
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry findByUuid_C_First(String uuid,
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the first editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByUuid_C_First(String uuid,
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry findByUuid_C_Last(String uuid,
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the last editor configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByUuid_C_Last(String uuid,
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

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
	public EditorConfigurationEntry[] findByUuid_C_PrevAndNext(
		long editorConfigurationEntryId, String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Removes all the editor configuration entries where uuid = &#63; and companyId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	*/
	public void removeByUuid_C(String uuid, long companyId);

	/**
	* Returns the number of editor configuration entries where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the number of matching editor configuration entries
	*/
	public int countByUuid_C(String uuid, long companyId);

	/**
	* Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the matching editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry findByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey);

	/**
	* Returns the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	public EditorConfigurationEntry fetchByP_E_E(String portletName,
		String editorName, String editorConfigKey, boolean retrieveFromCache);

	/**
	* Removes the editor configuration entry where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63; from the database.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the editor configuration entry that was removed
	*/
	public EditorConfigurationEntry removeByP_E_E(String portletName,
		String editorName, String editorConfigKey)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the number of editor configuration entries where portletName = &#63; and editorName = &#63; and editorConfigKey = &#63;.
	*
	* @param portletName the portlet name
	* @param editorName the editor name
	* @param editorConfigKey the editor config key
	* @return the number of matching editor configuration entries
	*/
	public int countByP_E_E(String portletName, String editorName,
		String editorConfigKey);

	/**
	* Caches the editor configuration entry in the entity cache if it is enabled.
	*
	* @param editorConfigurationEntry the editor configuration entry
	*/
	public void cacheResult(EditorConfigurationEntry editorConfigurationEntry);

	/**
	* Caches the editor configuration entries in the entity cache if it is enabled.
	*
	* @param editorConfigurationEntries the editor configuration entries
	*/
	public void cacheResult(
		java.util.List<EditorConfigurationEntry> editorConfigurationEntries);

	/**
	* Creates a new editor configuration entry with the primary key. Does not add the editor configuration entry to the database.
	*
	* @param editorConfigurationEntryId the primary key for the new editor configuration entry
	* @return the new editor configuration entry
	*/
	public EditorConfigurationEntry create(long editorConfigurationEntryId);

	/**
	* Removes the editor configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry that was removed
	* @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	*/
	public EditorConfigurationEntry remove(long editorConfigurationEntryId)
		throws NoSuchEditorConfigurationEntryException;

	public EditorConfigurationEntry updateImpl(
		EditorConfigurationEntry editorConfigurationEntry);

	/**
	* Returns the editor configuration entry with the primary key or throws a {@link NoSuchEditorConfigurationEntryException} if it could not be found.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry
	* @throws NoSuchEditorConfigurationEntryException if a editor configuration entry with the primary key could not be found
	*/
	public EditorConfigurationEntry findByPrimaryKey(
		long editorConfigurationEntryId)
		throws NoSuchEditorConfigurationEntryException;

	/**
	* Returns the editor configuration entry with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry, or <code>null</code> if a editor configuration entry with the primary key could not be found
	*/
	public EditorConfigurationEntry fetchByPrimaryKey(
		long editorConfigurationEntryId);

	@Override
	public java.util.Map<java.io.Serializable, EditorConfigurationEntry> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys);

	/**
	* Returns all the editor configuration entries.
	*
	* @return the editor configuration entries
	*/
	public java.util.List<EditorConfigurationEntry> findAll();

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
	public java.util.List<EditorConfigurationEntry> findAll(int start, int end);

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
	public java.util.List<EditorConfigurationEntry> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator);

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
	public java.util.List<EditorConfigurationEntry> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EditorConfigurationEntry> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the editor configuration entries from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of editor configuration entries.
	*
	* @return the number of editor configuration entries
	*/
	public int countAll();

	@Override
	public java.util.Set<String> getBadColumnNames();
}
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

package com.liferay.frontend.editor.configuration.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link EditorConfigurationEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see EditorConfigurationEntryLocalService
 * @generated
 */
@ProviderType
public class EditorConfigurationEntryLocalServiceWrapper
	implements EditorConfigurationEntryLocalService,
		ServiceWrapper<EditorConfigurationEntryLocalService> {
	public EditorConfigurationEntryLocalServiceWrapper(
		EditorConfigurationEntryLocalService editorConfigurationEntryLocalService) {
		_editorConfigurationEntryLocalService = editorConfigurationEntryLocalService;
	}

	/**
	* Adds the editor configuration entry to the database. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntry the editor configuration entry
	* @return the editor configuration entry that was added
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry addEditorConfigurationEntry(
		com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry editorConfigurationEntry) {
		return _editorConfigurationEntryLocalService.addEditorConfigurationEntry(editorConfigurationEntry);
	}

	/**
	* Creates a new editor configuration entry with the primary key. Does not add the editor configuration entry to the database.
	*
	* @param editorConfigurationEntryId the primary key for the new editor configuration entry
	* @return the new editor configuration entry
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry createEditorConfigurationEntry(
		long editorConfigurationEntryId) {
		return _editorConfigurationEntryLocalService.createEditorConfigurationEntry(editorConfigurationEntryId);
	}

	/**
	* Deletes the editor configuration entry from the database. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntry the editor configuration entry
	* @return the editor configuration entry that was removed
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry deleteEditorConfigurationEntry(
		com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry editorConfigurationEntry) {
		return _editorConfigurationEntryLocalService.deleteEditorConfigurationEntry(editorConfigurationEntry);
	}

	/**
	* Deletes the editor configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry that was removed
	* @throws PortalException if a editor configuration entry with the primary key could not be found
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry deleteEditorConfigurationEntry(
		long editorConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _editorConfigurationEntryLocalService.deleteEditorConfigurationEntry(editorConfigurationEntryId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _editorConfigurationEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _editorConfigurationEntryLocalService.dynamicQuery();
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
		return _editorConfigurationEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.frontend.editor.configuration.model.impl.EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _editorConfigurationEntryLocalService.dynamicQuery(dynamicQuery,
			start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.frontend.editor.configuration.model.impl.EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _editorConfigurationEntryLocalService.dynamicQuery(dynamicQuery,
			start, end, orderByComparator);
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
		return _editorConfigurationEntryLocalService.dynamicQueryCount(dynamicQuery);
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
		return _editorConfigurationEntryLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry fetchEditorConfigurationEntry(
		long editorConfigurationEntryId) {
		return _editorConfigurationEntryLocalService.fetchEditorConfigurationEntry(editorConfigurationEntryId);
	}

	/**
	* Returns the editor configuration entry with the matching UUID and company.
	*
	* @param uuid the editor configuration entry's UUID
	* @param companyId the primary key of the company
	* @return the matching editor configuration entry, or <code>null</code> if a matching editor configuration entry could not be found
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry fetchEditorConfigurationEntryByUuidAndCompanyId(
		String uuid, long companyId) {
		return _editorConfigurationEntryLocalService.fetchEditorConfigurationEntryByUuidAndCompanyId(uuid,
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _editorConfigurationEntryLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns a range of all the editor configuration entries.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.frontend.editor.configuration.model.impl.EditorConfigurationEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of editor configuration entries
	* @param end the upper bound of the range of editor configuration entries (not inclusive)
	* @return the range of editor configuration entries
	*/
	@Override
	public java.util.List<com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry> getEditorConfigurationEntries(
		int start, int end) {
		return _editorConfigurationEntryLocalService.getEditorConfigurationEntries(start,
			end);
	}

	/**
	* Returns the number of editor configuration entries.
	*
	* @return the number of editor configuration entries
	*/
	@Override
	public int getEditorConfigurationEntriesCount() {
		return _editorConfigurationEntryLocalService.getEditorConfigurationEntriesCount();
	}

	/**
	* Returns the editor configuration entry with the primary key.
	*
	* @param editorConfigurationEntryId the primary key of the editor configuration entry
	* @return the editor configuration entry
	* @throws PortalException if a editor configuration entry with the primary key could not be found
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry getEditorConfigurationEntry(
		long editorConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _editorConfigurationEntryLocalService.getEditorConfigurationEntry(editorConfigurationEntryId);
	}

	/**
	* Returns the editor configuration entry with the matching UUID and company.
	*
	* @param uuid the editor configuration entry's UUID
	* @param companyId the primary key of the company
	* @return the matching editor configuration entry
	* @throws PortalException if a matching editor configuration entry could not be found
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry getEditorConfigurationEntryByUuidAndCompanyId(
		String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _editorConfigurationEntryLocalService.getEditorConfigurationEntryByUuidAndCompanyId(uuid,
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery getExportActionableDynamicQuery(
		com.liferay.exportimport.kernel.lar.PortletDataContext portletDataContext) {
		return _editorConfigurationEntryLocalService.getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _editorConfigurationEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _editorConfigurationEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _editorConfigurationEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the editor configuration entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param editorConfigurationEntry the editor configuration entry
	* @return the editor configuration entry that was updated
	*/
	@Override
	public com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry updateEditorConfigurationEntry(
		com.liferay.frontend.editor.configuration.model.EditorConfigurationEntry editorConfigurationEntry) {
		return _editorConfigurationEntryLocalService.updateEditorConfigurationEntry(editorConfigurationEntry);
	}

	@Override
	public EditorConfigurationEntryLocalService getWrappedService() {
		return _editorConfigurationEntryLocalService;
	}

	@Override
	public void setWrappedService(
		EditorConfigurationEntryLocalService editorConfigurationEntryLocalService) {
		_editorConfigurationEntryLocalService = editorConfigurationEntryLocalService;
	}

	private EditorConfigurationEntryLocalService _editorConfigurationEntryLocalService;
}
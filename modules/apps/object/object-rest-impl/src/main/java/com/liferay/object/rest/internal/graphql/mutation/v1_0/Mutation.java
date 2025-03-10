/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.graphql.mutation.v1_0;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setObjectEntryResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectEntryResource>
			objectEntryResourceComponentServiceObjects) {

		_objectEntryResourceComponentServiceObjects =
			objectEntryResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createObjectEntriesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.postObjectEntriesPageExportBatch(
					search,
					_filterBiFunction.apply(objectEntryResource, filterString),
					_sortsBiFunction.apply(objectEntryResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ObjectEntry createObjectEntry(
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.postObjectEntry(
				objectEntry));
	}

	@GraphQLField
	public Response createObjectEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.postObjectEntryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ObjectEntry
			updateByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@GraphQLName("currentExternalReferenceCode") String
					currentExternalReferenceCode,
				@GraphQLName("objectRelationshipName") String
					objectRelationshipName,
				@GraphQLName("relatedExternalReferenceCode") String
					relatedExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.
					putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
						currentExternalReferenceCode, objectRelationshipName,
						relatedExternalReferenceCode));
	}

	@GraphQLField
	public boolean deleteByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.deleteByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public ObjectEntry patchByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.patchByExternalReferenceCode(
					externalReferenceCode, objectEntry));
	}

	@GraphQLField
	public ObjectEntry updateByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.putByExternalReferenceCode(
					externalReferenceCode, objectEntry));
	}

	@GraphQLField
	public boolean
			updateByExternalReferenceCodeObjectEntryExternalReferenceCodeObjectActionObjectActionName(
				@GraphQLName("objectEntryExternalReferenceCode") String
					objectEntryExternalReferenceCode,
				@GraphQLName("objectActionName") String objectActionName)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.
					putByExternalReferenceCodeObjectEntryExternalReferenceCodeObjectActionObjectActionName(
						objectEntryExternalReferenceCode, objectActionName));

		return true;
	}

	@GraphQLField
	public boolean deleteScopeScopeKeyByExternalReferenceCode(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.deleteScopeScopeKeyByExternalReferenceCode(
					scopeKey, externalReferenceCode));

		return true;
	}

	@GraphQLField
	public ObjectEntry patchScopeScopeKeyByExternalReferenceCode(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.patchScopeScopeKeyByExternalReferenceCode(
					scopeKey, externalReferenceCode, objectEntry));
	}

	@GraphQLField
	public ObjectEntry updateScopeScopeKeyByExternalReferenceCode(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.putScopeScopeKeyByExternalReferenceCode(
					scopeKey, externalReferenceCode, objectEntry));
	}

	@GraphQLField
	public boolean
			updateScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectActionName") String objectActionName)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.
					putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
						scopeKey, externalReferenceCode, objectActionName));

		return true;
	}

	@GraphQLField
	public boolean deleteObjectEntry(
			@GraphQLName("objectEntryId") Long objectEntryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.deleteObjectEntry(
				objectEntryId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.deleteObjectEntryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ObjectEntry patchObjectEntry(
			@GraphQLName("objectEntryId") Long objectEntryId,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.patchObjectEntry(
				objectEntryId, objectEntry));
	}

	@GraphQLField
	public ObjectEntry updateObjectEntry(
			@GraphQLName("objectEntryId") Long objectEntryId,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.putObjectEntry(
				objectEntryId, objectEntry));
	}

	@GraphQLField
	public Response updateObjectEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.putObjectEntryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean updateObjectEntryObjectActionObjectActionName(
			@GraphQLName("objectEntryId") Long objectEntryId,
			@GraphQLName("objectActionName") String objectActionName)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.putObjectEntryObjectActionObjectActionName(
					objectEntryId, objectActionName));

		return true;
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateObjectEntryPermissionsPage(
				@GraphQLName("objectEntryId") Long objectEntryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> {
				Page paginationPage =
					objectEntryResource.putObjectEntryPermissionsPage(
						objectEntryId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public ObjectEntry createScopeScopeKey(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("objectEntry") ObjectEntry objectEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.postScopeScopeKey(
				scopeKey, objectEntry));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			ObjectEntryResource objectEntryResource)
		throws Exception {

		objectEntryResource.setContextAcceptLanguage(_acceptLanguage);
		objectEntryResource.setContextCompany(_company);
		objectEntryResource.setContextHttpServletRequest(_httpServletRequest);
		objectEntryResource.setContextHttpServletResponse(_httpServletResponse);
		objectEntryResource.setContextUriInfo(_uriInfo);
		objectEntryResource.setContextUser(_user);
		objectEntryResource.setGroupLocalService(_groupLocalService);
		objectEntryResource.setRoleLocalService(_roleLocalService);

		objectEntryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectEntryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<ObjectEntryResource>
		_objectEntryResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}
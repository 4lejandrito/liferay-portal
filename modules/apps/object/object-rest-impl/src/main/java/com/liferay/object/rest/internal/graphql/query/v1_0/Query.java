/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.graphql.query.v1_0;

import com.liferay.object.rest.dto.v1_0.Folder2;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.resource.v1_0.Folder2Resource;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setFolder2ResourceComponentServiceObjects(
		ComponentServiceObjects<Folder2Resource>
			folder2ResourceComponentServiceObjects) {

		_folder2ResourceComponentServiceObjects =
			folder2ResourceComponentServiceObjects;
	}

	public static void setObjectEntryResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectEntryResource>
			objectEntryResourceComponentServiceObjects) {

		_objectEntryResourceComponentServiceObjects =
			objectEntryResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectFolders(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Folder2Page objectFolders(
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_folder2ResourceComponentServiceObjects,
			this::_populateResourceContext,
			folder2Resource -> new Folder2Page(
				folder2Resource.getObjectFoldersPage(
					flatten, search,
					_aggregationBiFunction.apply(folder2Resource, aggregations),
					_filterBiFunction.apply(folder2Resource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(folder2Resource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntries(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryPage objectEntries(
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> new ObjectEntryPage(
				objectEntryResource.getObjectEntriesPage(
					flatten, search,
					_aggregationBiFunction.apply(
						objectEntryResource, aggregations),
					_filterBiFunction.apply(objectEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(objectEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {byExternalReferenceCode(externalReferenceCode: ___){actions, auditEvents, creator, dateCreated, dateModified, defaultLanguageId, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, keywords, permissions, properties, scopeKey, status, taxonomyCategoryBriefs, taxonomyCategoryIds}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntry byExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.getByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKeyByExternalReferenceCode(externalReferenceCode: ___, scopeKey: ___){actions, auditEvents, creator, dateCreated, dateModified, defaultLanguageId, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, keywords, permissions, properties, scopeKey, status, taxonomyCategoryBriefs, taxonomyCategoryIds}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntry scopeScopeKeyByExternalReferenceCode(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource ->
				objectEntryResource.getScopeScopeKeyByExternalReferenceCode(
					scopeKey, externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntry(objectEntryId: ___){actions, auditEvents, creator, dateCreated, dateModified, defaultLanguageId, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, keywords, permissions, properties, scopeKey, status, taxonomyCategoryBriefs, taxonomyCategoryIds}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntry objectEntry(
			@GraphQLName("objectEntryId") Long objectEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> objectEntryResource.getObjectEntry(
				objectEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntryPermissions(objectEntryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryPage objectEntryPermissions(
			@GraphQLName("objectEntryId") Long objectEntryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> new ObjectEntryPage(
				objectEntryResource.getObjectEntryPermissionsPage(
					objectEntryId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKey(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, scopeKey: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryPage scopeScopeKey(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryResource -> new ObjectEntryPage(
				objectEntryResource.getScopeScopeKeyPage(
					scopeKey, flatten, search,
					_aggregationBiFunction.apply(
						objectEntryResource, aggregations),
					_filterBiFunction.apply(objectEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(objectEntryResource, sortsString))));
	}

	@GraphQLName("Folder2Page")
	public class Folder2Page {

		public Folder2Page(Page folder2Page) {
			actions = folder2Page.getActions();

			facets = folder2Page.getFacets();

			items = folder2Page.getItems();
			lastPage = folder2Page.getLastPage();
			page = folder2Page.getPage();
			pageSize = folder2Page.getPageSize();
			totalCount = folder2Page.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Folder2> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectEntryPage")
	public class ObjectEntryPage {

		public ObjectEntryPage(Page objectEntryPage) {
			actions = objectEntryPage.getActions();

			facets = objectEntryPage.getFacets();

			items = objectEntryPage.getItems();
			lastPage = objectEntryPage.getLastPage();
			page = objectEntryPage.getPage();
			pageSize = objectEntryPage.getPageSize();
			totalCount = objectEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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

	private void _populateResourceContext(Folder2Resource folder2Resource)
		throws Exception {

		folder2Resource.setContextAcceptLanguage(_acceptLanguage);
		folder2Resource.setContextCompany(_company);
		folder2Resource.setContextHttpServletRequest(_httpServletRequest);
		folder2Resource.setContextHttpServletResponse(_httpServletResponse);
		folder2Resource.setContextUriInfo(_uriInfo);
		folder2Resource.setContextUser(_user);
		folder2Resource.setGroupLocalService(_groupLocalService);
		folder2Resource.setRoleLocalService(_roleLocalService);
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
	}

	private static ComponentServiceObjects<Folder2Resource>
		_folder2ResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectEntryResource>
		_objectEntryResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}
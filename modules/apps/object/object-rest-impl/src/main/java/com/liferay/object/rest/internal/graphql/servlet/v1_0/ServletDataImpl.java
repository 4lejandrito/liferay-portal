/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.graphql.servlet.v1_0;

import com.liferay.object.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.object.rest.internal.graphql.query.v1_0.Query;
import com.liferay.object.rest.internal.resource.v1_0.Folder2ResourceImpl;
import com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl;
import com.liferay.object.rest.resource.v1_0.Folder2Resource;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setObjectEntryResourceComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects);

		Query.setFolder2ResourceComponentServiceObjects(
			_folder2ResourceComponentServiceObjects);
		Query.setObjectEntryResourceComponentServiceObjects(
			_objectEntryResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Object";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/object-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createObjectEntriesPageExportBatch",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"postObjectEntriesPageExportBatch"));
					put(
						"mutation#createObjectEntry",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class, "postObjectEntry"));
					put(
						"mutation#createObjectEntryBatch",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"postObjectEntryBatch"));
					put(
						"mutation#updateByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"));
					put(
						"mutation#deleteByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"deleteByExternalReferenceCode"));
					put(
						"mutation#patchByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"patchByExternalReferenceCode"));
					put(
						"mutation#updateByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putByExternalReferenceCode"));
					put(
						"mutation#updateByExternalReferenceCodeObjectEntryExternalReferenceCodeObjectActionObjectActionName",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putByExternalReferenceCodeObjectEntryExternalReferenceCodeObjectActionObjectActionName"));
					put(
						"mutation#deleteScopeScopeKeyByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"deleteScopeScopeKeyByExternalReferenceCode"));
					put(
						"mutation#patchScopeScopeKeyByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"patchScopeScopeKeyByExternalReferenceCode"));
					put(
						"mutation#updateScopeScopeKeyByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putScopeScopeKeyByExternalReferenceCode"));
					put(
						"mutation#updateScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName"));
					put(
						"mutation#deleteObjectEntry",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"deleteObjectEntry"));
					put(
						"mutation#deleteObjectEntryBatch",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"deleteObjectEntryBatch"));
					put(
						"mutation#patchObjectEntry",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class, "patchObjectEntry"));
					put(
						"mutation#updateObjectEntry",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class, "putObjectEntry"));
					put(
						"mutation#updateObjectEntryBatch",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putObjectEntryBatch"));
					put(
						"mutation#updateObjectEntryObjectActionObjectActionName",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putObjectEntryObjectActionObjectActionName"));
					put(
						"mutation#updateObjectEntryPermissionsPage",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"putObjectEntryPermissionsPage"));
					put(
						"mutation#createScopeScopeKey",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"postScopeScopeKey"));

					put(
						"query#objectFolders",
						new ObjectValuePair<>(
							Folder2ResourceImpl.class, "getObjectFoldersPage"));
					put(
						"query#objectEntries",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"getObjectEntriesPage"));
					put(
						"query#byExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"getByExternalReferenceCode"));
					put(
						"query#scopeScopeKeyByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"getScopeScopeKeyByExternalReferenceCode"));
					put(
						"query#objectEntry",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class, "getObjectEntry"));
					put(
						"query#objectEntryPermissions",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"getObjectEntryPermissionsPage"));
					put(
						"query#scopeScopeKey",
						new ObjectValuePair<>(
							ObjectEntryResourceImpl.class,
							"getScopeScopeKeyPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectEntryResource>
		_objectEntryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<Folder2Resource>
		_folder2ResourceComponentServiceObjects;

}
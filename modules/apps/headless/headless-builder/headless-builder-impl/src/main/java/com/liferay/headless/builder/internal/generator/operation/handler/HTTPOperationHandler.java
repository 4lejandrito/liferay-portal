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

package com.liferay.headless.builder.internal.generator.operation.handler;

import com.liferay.headless.builder.internal.generator.application.Operation;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author Luis Miguel Barcos
 */
public abstract class HTTPOperationHandler {

	public HTTPOperationHandler(
		AcceptLanguage contextAcceptLanguage, Company contextCompany,
		HttpServletRequest contextHttpServletRequest, UriInfo contextUriInfo,
		User contextUser, DTOConverterRegistry dtoConverterRegistry,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService) {

		this.contextAcceptLanguage = contextAcceptLanguage;
		this.contextCompany = contextCompany;
		this.contextHttpServletRequest = contextHttpServletRequest;
		this.contextUriInfo = contextUriInfo;
		this.contextUser = contextUser;
		this.dtoConverterRegistry = dtoConverterRegistry;
		this.objectDefinitionLocalService = objectDefinitionLocalService;
		this.objectFieldLocalService = objectFieldLocalService;
	}

	public abstract Response getResponse(Operation operation) throws Exception;

	protected DefaultDTOConverterContext getDTOConverterContext(
		Long objectEntryId) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			dtoConverterRegistry, contextHttpServletRequest, objectEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	protected final AcceptLanguage contextAcceptLanguage;
	protected final Company contextCompany;
	protected final HttpServletRequest contextHttpServletRequest;
	protected final UriInfo contextUriInfo;
	protected final User contextUser;
	protected final DTOConverterRegistry dtoConverterRegistry;
	protected final ObjectDefinitionLocalService objectDefinitionLocalService;
	protected final ObjectFieldLocalService objectFieldLocalService;

}
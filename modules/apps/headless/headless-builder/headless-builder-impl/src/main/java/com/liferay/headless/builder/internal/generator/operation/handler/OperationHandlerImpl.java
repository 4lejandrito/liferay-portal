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
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(service = OperationHandler.class)
public class OperationHandlerImpl implements OperationHandler {

	@Override
	public Response handle(
			AcceptLanguage contextAcceptLanguage, Company contextCompany,
			HttpServletRequest contextHttpServletRequest,
			UriInfo contextUriInfo, User contextUser, Operation operation)
		throws Exception {

		HTTPOperationHandler httpOperationHandler;

		if (Objects.requireNonNull(operation.getMethod()) == Http.Method.GET) {
			httpOperationHandler = new GetHTTPOperationHandlerImpl(
				contextAcceptLanguage, contextCompany,
				contextHttpServletRequest, contextUriInfo, contextUser,
				_dtoConverterRegistry, _objectDefinitionLocalService,
				_objectEntryManagerRegistry, _objectFieldLocalService);
		}
		else if ((operation.getMethod() == Http.Method.DELETE) ||
				 (operation.getMethod() == Http.Method.HEAD) ||
				 (operation.getMethod() == Http.Method.PATCH) ||
				 (operation.getMethod() == Http.Method.POST) ||
				 (operation.getMethod() == Http.Method.PUT)) {

			throw new UnsupportedOperationException("Operation not supported");
		}
		else {
			return Response.noContent(
			).build();
		}

		return httpOperationHandler.getResponse(operation);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}
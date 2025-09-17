/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.filter.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.vulcan.filter.provider.FilterProvider;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = FilterProvider.class)
public class FilterProviderImpl implements FilterProvider {

	@Override
	public Filter getFilter(
			EntityModel entityModel, String filterString, Locale locale)
		throws PortalException {

		if ((entityModel == null) || (filterString == null)) {
			return null;
		}

		FilterParser filterParser = _filterParserProvider.provide(entityModel);

		if (_log.isDebugEnabled()) {
			_log.debug("OData filter parser: " + filterParser);
		}

		try {
			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			if (_log.isDebugEnabled()) {
				_log.debug("OData filter: " + oDataFilter);
			}

			Filter filter = _expressionConvert.convert(
				oDataFilter.getExpression(), locale, entityModel);

			if (_log.isDebugEnabled()) {
				_log.debug("Search filter: " + filter);
			}

			return filter;
		}
		catch (ExpressionVisitException expressionVisitException) {
			throw new PortalException(expressionVisitException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FilterProviderImpl.class);

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private FilterParserProvider _filterParserProvider;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.internal.fields.NestedFieldsSetterUtil;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjector;
import com.liferay.portal.vulcan.util.NestedFieldsContextUtil;

import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class EmbeddedNestedFieldsSerializer
	extends JsonSerializer
		<EmbeddedNestedFieldsSerializer.EmbeddedNestedFields> {

	@Override
	public void serialize(
		EmbeddedNestedFields embeddedNestedFields, JsonGenerator jsonGenerator,
		SerializerProvider serializerProvider) {

		try {
			Object item = embeddedNestedFields._item;

			if (item == null) {
				jsonGenerator.writeNull();

				return;
			}

			NestedFieldsContext nestedFieldsContext =
				NestedFieldsContextThreadLocal.getAndSetNestedFieldsContext(
					new NestedFieldsContext(
						NestedFieldsContextUtil.limitDepth(
							embeddedNestedFields._nestedFieldsDepth),
						embeddedNestedFields._nestedFields,
						embeddedNestedFields._apiVersion));

			try {
				NestedFieldsSetterUtil.setNestedFields(
					item, embeddedNestedFields._contextDataInjector);

				JsonSerializer<Object> jsonSerializer =
					serializerProvider.findValueSerializer(
						item.getClass(), null);

				jsonSerializer.serialize(
					item, jsonGenerator, serializerProvider);
			}
			finally {
				NestedFieldsContextThreadLocal.setNestedFieldsContext(
					nestedFieldsContext);
			}
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@JsonSerialize(using = EmbeddedNestedFieldsSerializer.class)
	public static class EmbeddedNestedFields {

		public EmbeddedNestedFields(
			String apiVersion, ContextDataInjector contextDataInjector,
			Object item, List<String> nestedFields, int nestedFieldsDepth) {

			_apiVersion = apiVersion;
			_contextDataInjector = contextDataInjector;
			_item = item;
			_nestedFields = nestedFields;
			_nestedFieldsDepth = nestedFieldsDepth;
		}

		private final String _apiVersion;
		private final ContextDataInjector _contextDataInjector;
		private final Object _item;
		private final List<String> _nestedFields;
		private final int _nestedFieldsDepth;

	}

}
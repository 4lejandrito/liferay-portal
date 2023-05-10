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

package com.liferay.portal.vulcan.internal.jaxrs.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

/**
 * @author Alejandro Tardín
 */
public class JSONStringStdDeserializer extends StdDeserializer<String> {

	public JSONStringStdDeserializer(Class<String> clazz) {
		super(clazz);
	}

	@Override
	public String deserialize(
			JsonParser jsonParser,
			DeserializationContext deserializationContext)
		throws IOException {

		if (jsonParser.hasToken(JsonToken.VALUE_STRING)) {
			return jsonParser.getText();
		}

		if (StringUtil.endsWith(
				StringUtil.toLowerCase(jsonParser.getCurrentName()), "json")) {

			TreeNode treeNode = jsonParser.readValueAsTree();

			return treeNode.toString();
		}

		return (String)deserializationContext.handleUnexpectedToken(
			String.class, jsonParser);
	}

}
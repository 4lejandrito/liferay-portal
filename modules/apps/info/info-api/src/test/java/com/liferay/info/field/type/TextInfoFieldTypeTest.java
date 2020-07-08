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

package com.liferay.info.field.type;

import com.liferay.info.field.InfoField;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class TextInfoFieldTypeTest {

	@Test
	public void testMultilineAttributeCanBeSetToFalse() {
		InfoField<TextInfoFieldType> infoField = InfoField.builder(
			TextInfoFieldType.INSTANCE
		).attr(
			TextInfoFieldType.MULTILINE, false
		).build();

		Assert.assertFalse(infoField.attr(TextInfoFieldType.MULTILINE));
	}

	@Test
	public void testMultilineAttributeCanBeSetToTrue() {
		InfoField<TextInfoFieldType> infoField = InfoField.builder(
			TextInfoFieldType.INSTANCE
		).attr(
			TextInfoFieldType.MULTILINE, true
		).build();

		Assert.assertTrue(infoField.attr(TextInfoFieldType.MULTILINE));
	}

	@Test
	public void testMultilineAttributeIsNullByDefault() {
		InfoField<TextInfoFieldType> infoField = InfoField.builder(
			TextInfoFieldType.INSTANCE
		).build();

		Assert.assertNull(infoField.attr(TextInfoFieldType.MULTILINE));
	}

}
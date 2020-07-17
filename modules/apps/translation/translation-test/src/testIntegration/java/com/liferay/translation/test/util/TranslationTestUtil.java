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

package com.liferay.translation.test.util;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Alejandro Tardín
 */
public class TranslationTestUtil {

	public static ByteArrayInputStream getChangeIdByteArrayInputStream(
			String fileName, String id, long resourcePrimKey)
		throws Exception {

		String fileContent = StringUtil.replace(
			readFileToString(fileName), id, String.valueOf(resourcePrimKey));

		return new ByteArrayInputStream(fileContent.getBytes());
	}

	public static InputStream readFileToInputStream(String fileName)
		throws Exception {

		return new ByteArrayInputStream(_getBytes(fileName));
	}

	public static String readFileToString(String fileName) throws Exception {
		return new String(_getBytes(fileName));
	}

	private static byte[] _getBytes(String fileName) throws Exception {
		return FileUtil.getBytes(
			TranslationTestUtil.class,
			"/com/liferay/translation/dependencies/" + fileName);
	}

}
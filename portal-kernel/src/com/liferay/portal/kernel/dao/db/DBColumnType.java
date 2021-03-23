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

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author Alejandro Tardín
 */
public final class DBColumnType {

	public static DBColumnType booleanType() {
		return new DBColumnType(TypeName.BOOLEAN);
	}

	public static DBColumnType dateType(String defaultValue) {
		return new DBColumnType(TypeName.DATE, defaultValue);
	}

	public static DBColumnType doubleType() {
		return new DBColumnType(TypeName.DOUBLE);
	}

	public static DBColumnType integerType() {
		return new DBColumnType(TypeName.INTEGER);
	}

	public static DBColumnType longType() {
		return new DBColumnType(TypeName.LONG);
	}

	public static DBColumnType stringType(String defaultValue) {
		return new DBColumnType(TypeName.STRING, defaultValue);
	}

	public static DBColumnType varcharType(long length) {
		return varcharType(length, StringPool.BLANK);
	}

	public static DBColumnType varcharType(long length, String defaultValue) {
		return new DBColumnType(
			TypeName.STRING,
			StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, length,
				StringPool.CLOSE_PARENTHESIS, StringPool.SPACE, defaultValue));
	}

	public String getSQL() {
		return _typeName.toString() + StringPool.SPACE + _suffix;
	}

	public enum TypeName {

		BLOB, BOOLEAN, DATE, DOUBLE, INTEGER, LONG, SBLOB, STRING, TEXT, VARCHAR

	}

	private DBColumnType(TypeName typeName) {
		_typeName = typeName;
	}

	private DBColumnType(TypeName typeName, String suffix) {
		this(typeName);

		_suffix = suffix;
	}

	private String _suffix;
	private TypeName _typeName;

}
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

package com.liferay.info.field;

import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
public class InfoField implements InfoFieldSetEntry {

	public static InfoField.Builder builder(InfoFieldType infoFieldType) {
		return new InfoField.Builder(infoFieldType);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public InfoField(
		InfoFieldType infoFieldType,
		InfoLocalizedValue<String> labelInfoLocalizedValue, boolean localizable,
		String name) {

		this(
			builder(
				infoFieldType
			).labelInfoLocalizedValue(
				labelInfoLocalizedValue
			).name(
				name
			).localizable(
				localizable
			));
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public InfoField(
		InfoFieldType infoFieldType,
		InfoLocalizedValue<String> labelInfoLocalizedValue, String name) {

		this(
			builder(
				infoFieldType
			).labelInfoLocalizedValue(
				labelInfoLocalizedValue
			).name(
				name
			));
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InfoField)) {
			return false;
		}

		InfoField infoDisplayField = (InfoField)object;

		if (Objects.equals(
				_builder._infoFieldType,
				infoDisplayField._builder._infoFieldType) &&
			Objects.equals(
				_builder._labelInfoLocalizedValue,
				infoDisplayField._builder._labelInfoLocalizedValue) &&
			Objects.equals(_builder._name, infoDisplayField._builder._name)) {

			return true;
		}

		return false;
	}

	public InfoFieldType getInfoFieldType() {
		return _builder._infoFieldType;
	}

	@Override
	public String getLabel(Locale locale) {
		return _builder._labelInfoLocalizedValue.getValue(locale);
	}

	@Override
	public InfoLocalizedValue<String> getLabelInfoLocalizedValue() {
		return _builder._labelInfoLocalizedValue;
	}

	@Override
	public String getName() {
		return _builder._name;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _builder._infoFieldType);

		hash = HashUtil.hash(hash, _builder._labelInfoLocalizedValue);

		return HashUtil.hash(hash, _builder._name);
	}

	public boolean isLocalizable() {
		return _builder._localizable;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{name: ");
		sb.append(_builder._name);
		sb.append(", type: ");
		sb.append(_builder._infoFieldType.getName());
		sb.append("}");

		return sb.toString();
	}

	public static class Builder {

		public InfoField build() {
			return new InfoField(this);
		}

		public InfoField.Builder labelInfoLocalizedValue(
			InfoLocalizedValue<String> labelInfoLocalizedValue) {

			_labelInfoLocalizedValue = labelInfoLocalizedValue;

			return this;
		}

		public InfoField.Builder localizable(boolean localizable) {
			_localizable = localizable;

			return this;
		}

		public InfoField.Builder name(String name) {
			_name = name;

			return this;
		}

		private Builder(InfoFieldType infoFieldType) {
			_infoFieldType = infoFieldType;
		}

		private final InfoFieldType _infoFieldType;
		private InfoLocalizedValue<String> _labelInfoLocalizedValue;
		private boolean _localizable;
		private String _name;

	}

	private InfoField(InfoField.Builder builder) {
		_builder = builder;
	}

	private Builder _builder;

}
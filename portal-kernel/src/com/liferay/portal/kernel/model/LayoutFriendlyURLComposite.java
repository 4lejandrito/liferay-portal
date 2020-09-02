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

package com.liferay.portal.kernel.model;

import java.util.Locale;
import java.util.Map;

/**
 * @author Sergio González
 */
public class LayoutFriendlyURLComposite {

	public LayoutFriendlyURLComposite(Layout layout, String friendlyURL) {
		_layout = layout;
		_friendlyURL = friendlyURL;
	}

	public LayoutFriendlyURLComposite(
		Layout layout, String friendlyURL, Map<Locale, String> alternateURLs) {

		_layout = layout;
		_friendlyURL = friendlyURL;
		_alternateURLs = alternateURLs;
	}

	public Map<Locale, String> getAlternateURLs() {
		return _alternateURLs;
	}

	public String getFriendlyURL() {
		return _friendlyURL;
	}

	public Layout getLayout() {
		return _layout;
	}

	public void setAlternateURLs(Map<Locale, String> alternateURLs) {
		_alternateURLs = alternateURLs;
	}

	public void setFriendlyURL(String friendlyURL) {
		_friendlyURL = friendlyURL;
	}

	public void setLayout(Layout layout) {
		_layout = layout;
	}

	private Map<Locale, String> _alternateURLs;
	private String _friendlyURL;
	private Layout _layout;

}
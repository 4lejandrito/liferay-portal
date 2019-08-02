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

package com.liferay.item.selector.taglib.servlet.taglib.clay;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.soy.ImageCard;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;

/**
 * @author Jorge González
 */
public class ItemSelectorItemImageCard implements ImageCard {

	public ItemSelectorItemImageCard(
		FileEntry fileEntry, ThemeDisplay themeDisplay) {

		_fileEntry = fileEntry;
		_themeDisplay = themeDisplay;
	}

	@Override
	public String getHref() {
		try {
			return DLURLHelperUtil.getImagePreviewURL(
				_fileEntry, _themeDisplay);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String getImageSrc() {
		try {
			return DLURLHelperUtil.getThumbnailSrc(_fileEntry, _themeDisplay);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@Override
	public String getTitle() {
		return DLUtil.getTitleWithExtension(_fileEntry);
	}

	private final FileEntry _fileEntry;
	private final ThemeDisplay _themeDisplay;

}
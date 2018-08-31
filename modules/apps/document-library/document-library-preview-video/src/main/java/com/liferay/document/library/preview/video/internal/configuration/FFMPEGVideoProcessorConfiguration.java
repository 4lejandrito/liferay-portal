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

package com.liferay.document.library.preview.video.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media",
	descriptionArguments = "https://ffmpeg.org/"
)
@Meta.OCD(
	description = "ffmpeg-video-processor-configuration-description",
	id = "com.liferay.document.library.preview.video.internal.configuration.FFMPEGVideoProcessorConfiguration",
	localization = "content/Language",
	name = "ffmpeg-video-processor-configuration-name"
)
public interface FFMPEGVideoProcessorConfiguration {

	/**
	 * Sets the video preview bit rate
	 */
	@Meta.AD(deflt = "250000", name = "video-bit-rate", required = false)
	public long videoBitRate();

	/**
	 * Sets the video preview audio bit rate
	 */
	@Meta.AD(deflt = "128000", name = "audio-bit-rate", required = false)
	public long audioBitRate();

	/**
	 * Sets the video preview width
	 */
	@Meta.AD(deflt = "640", name = "video-width", required = false)
	public long videoWidth();

	/**
	 * Sets the video preview height
	 */
	@Meta.AD(deflt = "360", name = "video-height", required = false)
	public long videoHeight();

	/**
	 * Sets the video preview frame rate
	 */
	@Meta.AD(deflt = "30", name = "video-frame-rate", required = false)
	public long videoFrameRate();

}
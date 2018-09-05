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

package com.liferay.document.library.preview.video.internal;

import com.liferay.document.library.kernel.util.DLProcessor;
import com.liferay.document.library.preview.video.internal.configuration.FFMPEGVideoProcessorConfiguration;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.util.PortalClassPathUtil;
import com.liferay.portlet.documentlibrary.util.VideoProcessorImpl;

import java.io.File;
import java.io.InputStream;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.preview.video.internal.configuration.FFMPEGVideoProcessorConfiguration",
	immediate = true, property = "service.ranking:Integer=100",
	service = DLProcessor.class
)
public class FFMPEGVideoProcessor extends VideoProcessorImpl {

	@Override
	public void generateVideo(
			FileVersion sourceFileVersion, FileVersion destinationFileVersion)
		throws Exception {

		try (InputStream inputStream =
				destinationFileVersion.getContentStream(false)) {

			File videoTempFile = FileUtil.createTempFile(
				destinationFileVersion.getExtension());

			FileUtil.write(videoTempFile, inputStream);

			for (String previewType : getPreviewTypes()) {
				_generatePreview(
					videoTempFile, destinationFileVersion, previewType);
			}

			_generateThumbnail(videoTempFile, destinationFileVersion);

			List<Long> fileVersionIds = getFileVersionIds();

			fileVersionIds.remove(destinationFileVersion.getFileVersionId());
		}
	}

	@Override
	public boolean isSupported(String mimeType) {
		Set<String> videoMimeTypes = getVideoMimeTypes();

		return videoMimeTypes.contains(mimeType);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_processConfig = PortalClassPathUtil.createProcessConfig(
			Bundle.class, FFMPEGProcessCallable.class);

		modified(properties);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_ffmpegVideoProcessorConfiguration =
			ConfigurableUtil.createConfigurable(
				FFMPEGVideoProcessorConfiguration.class, properties);
	}

	private void _generatePreview(
			File videoFile, FileVersion fileVersion, String previewType)
		throws Exception {

		File destinationFile = FileUtil.createTempFile(previewType);

		ProcessChannel<String> processChannel = _processExecutor.execute(
			_processConfig,
			new FFMPEGProcessCallable(
				String.format(
					"ffmpeg -y -i %s -ab %dk -b:v %dk -vf scale=%d:%d -r %d %s",
					videoFile.getAbsolutePath(),
					_ffmpegVideoProcessorConfiguration.audioBitRate() / 1000,
					_ffmpegVideoProcessorConfiguration.videoBitRate() / 1000,
					_ffmpegVideoProcessorConfiguration.videoWidth(),
					_ffmpegVideoProcessorConfiguration.videoHeight(),
					_ffmpegVideoProcessorConfiguration.videoFrameRate(),
					destinationFile.getAbsolutePath())));

		Future<String> future = processChannel.getProcessNoticeableFuture();

		String output = future.get();

		if (_log.isInfoEnabled()) {
			_log.info(output);
		}

		addFileToStore(
			fileVersion.getCompanyId(), PREVIEW_PATH,
			getPreviewFilePath(fileVersion, previewType), destinationFile);
	}

	private void _generateThumbnail(File videoFile, FileVersion fileVersion)
		throws Exception {

		File destinationFile = FileUtil.createTempFile(getThumbnailType());

		String cmd = String.format(
			"ffmpeg -y -i %s -vf thumbnail,scale=w=min(%s\\,iw):h=-1 " +
				"-frames:v 1 %s",
			videoFile.getAbsolutePath(),
			_ffmpegVideoProcessorConfiguration.videoWidth(),
			destinationFile.getAbsolutePath());

		ProcessChannel<String> processChannel = _processExecutor.execute(
			_processConfig, new FFMPEGProcessCallable(cmd));

		Future<String> future = processChannel.getProcessNoticeableFuture();

		String output = future.get();

		if (_log.isInfoEnabled()) {
			_log.info(output);
		}

		addFileToStore(
			fileVersion.getCompanyId(), THUMBNAIL_PATH,
			getThumbnailFilePath(fileVersion, THUMBNAIL_INDEX_DEFAULT),
			destinationFile);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FFMPEGVideoProcessor.class);

	private volatile FFMPEGVideoProcessorConfiguration
		_ffmpegVideoProcessorConfiguration;
	private ProcessConfig _processConfig;

	@Reference
	private ProcessExecutor _processExecutor;

}
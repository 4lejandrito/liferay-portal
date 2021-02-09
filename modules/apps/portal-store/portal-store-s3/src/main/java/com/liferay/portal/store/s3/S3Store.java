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

package com.liferay.portal.store.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.store.s3.repository.AmazonS3Repository;
import com.liferay.portal.store.s3.repository.S3RepositoryFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Sten Martinez
 * @author Edward C. Han
 * @author Vilmos Papp
 * @author Máté Thurzó
 * @author Manuel de la Peña
 * @author Daniel Sanz
 */
@Component(
	configurationPid = "com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	property = "store.type=com.liferay.portal.store.s3.S3Store",
	service = Store.class
)
public class S3Store implements Store {

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			deleteFile(companyId, repositoryId, fileName, versionLabel);
		}

		File file = null;

		try {
			file = FileUtil.createTempFile(inputStream);

			String key = _s3KeyTransformer.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			_amazonS3Repository.putS3Object(key, file);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
		finally {
			FileUtil.delete(file);
		}
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		String key = _s3KeyTransformer.getDirectoryKey(
			companyId, repositoryId, dirName);

		_amazonS3Repository.deleteS3Objects(key);
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		String key = _s3KeyTransformer.getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		_amazonS3Repository.deleteS3Object(key);
	}

	public String getBucketName() {
		return _amazonS3Repository.getBucketName();
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		try {
			_s3FileCache.cleanUpCacheFiles();

			if (Validator.isNull(versionLabel)) {
				versionLabel = getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = _s3KeyTransformer.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			S3Object s3Object = _amazonS3Repository.getS3Object(key);

			if (s3Object == null) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName, versionLabel);
			}

			ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

			return _s3FileCache.getCacheFileInputStream(
				objectMetadata.getLastModified(), fileName,
				s3Object::getObjectContent);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		String key = null;

		if (Validator.isNull(dirName)) {
			key = _s3KeyTransformer.getRepositoryKey(companyId, repositoryId);
		}
		else {
			key = _s3KeyTransformer.getDirectoryKey(
				companyId, repositoryId, dirName);
		}

		List<String> s3ObjectKeys = _amazonS3Repository.getS3ObjectKeys(key);

		Iterator<String> iterator = s3ObjectKeys.iterator();

		String[] fileNames = new String[s3ObjectKeys.size()];

		for (int i = 0; i < fileNames.length; i++) {
			String s3ObjectKey = iterator.next();

			fileNames[i] = _s3KeyTransformer.getFileName(s3ObjectKey);
		}

		return fileNames;
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		String key = _s3KeyTransformer.getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		Long contentLength = _amazonS3Repository.getS3ObjectContentLength(key);

		if (contentLength == null) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		return contentLength;
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		String key = _s3KeyTransformer.getFileKey(
			companyId, repositoryId, fileName);

		List<String> s3ObjectKeys = _amazonS3Repository.getS3ObjectKeys(key);

		if (s3ObjectKeys.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = new String[s3ObjectKeys.size()];

		for (int i = 0; i < s3ObjectKeys.size(); i++) {
			String versionKey = s3ObjectKeys.get(i);

			versions[i] = versionKey.substring(
				versionKey.lastIndexOf(CharPool.SLASH) + 1);
		}

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	public TransferManager getTransferManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			if (Validator.isNull(versionLabel)) {
				versionLabel = getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = _s3KeyTransformer.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			return _amazonS3Repository.doesS3ObjectExist(key);
		}
		catch (NoSuchFileException noSuchFileException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException, noSuchFileException);
			}

			return false;
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_amazonS3Repository = _s3RepositoryFactory.getAmazonS3Repository();
	}

	@Deactivate
	protected void deactivate() {
		_amazonS3Repository = null;
	}

	protected String getHeadVersionLabel(
			long companyId, long repositoryId, String fileName)
		throws NoSuchFileException {

		String key = _s3KeyTransformer.getFileKey(
			companyId, repositoryId, fileName);

		List<String> s3ObjectKeys = _amazonS3Repository.getS3ObjectKeys(key);

		Iterator<String> iterator = s3ObjectKeys.iterator();

		String[] keys = new String[s3ObjectKeys.size()];

		for (int i = 0; i < keys.length; i++) {
			keys[i] = iterator.next();
		}

		if (keys.length > 0) {
			Arrays.sort(keys);

			String headVersionKey = keys[keys.length - 1];

			int x = headVersionKey.lastIndexOf(CharPool.SLASH);

			return headVersionKey.substring(x + 1);
		}

		throw new NoSuchFileException(companyId, repositoryId, fileName);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		deactivate();

		activate(properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(S3Store.class);

	private AmazonS3Repository _amazonS3Repository;

	@Reference
	private S3FileCache _s3FileCache;

	@Reference
	private S3KeyTransformer _s3KeyTransformer;

	@Reference
	private S3RepositoryFactory _s3RepositoryFactory;

}
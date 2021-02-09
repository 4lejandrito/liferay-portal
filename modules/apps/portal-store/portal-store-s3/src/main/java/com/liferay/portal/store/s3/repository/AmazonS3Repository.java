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

package com.liferay.portal.store.s3.repository;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import com.liferay.document.library.kernel.exception.AccessDeniedException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class AmazonS3Repository {

	public AmazonS3Repository(
		AmazonS3 amazonS3, String bucketName, StorageClass storageClass,
		TransferManager transferManager) {

		_amazonS3 = amazonS3;
		_bucketName = bucketName;
		_storageClass = storageClass;
		_transferManager = transferManager;
	}

	public void deleteS3Object(String key) {
		try {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				_bucketName, key);

			_amazonS3.deleteObject(deleteObjectRequest);
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
	}

	public void deleteS3Objects(String prefix) {
		try {
			String[] keys = new String[_DELETE_MAX];

			List<String> s3ObjectKeys = getS3ObjectKeys(prefix);

			Iterator<String> iterator = s3ObjectKeys.iterator();

			while (iterator.hasNext()) {
				for (int i = 0; i < keys.length; i++) {
					if (iterator.hasNext()) {
						keys[i] = iterator.next();
					}
					else {
						keys = Arrays.copyOfRange(keys, 0, i);

						break;
					}
				}

				deleteObjects(keys);
			}
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
	}

	public void deleteS3Objects(String[] keys) {
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(
			_bucketName);

		deleteObjectsRequest.withKeys(keys);

		_amazonS3.deleteObjects(deleteObjectsRequest);
	}

	public boolean doesS3ObjectExist(String key) {
		try {
			return _amazonS3.doesObjectExist(_bucketName, key);
		}
		catch (AmazonClientException amazonClientException) {
			if (_isFileNotFound(amazonClientException)) {
				return false;
			}

			throw _transform(amazonClientException);
		}
	}

	public String getBucketName() {
		return _bucketName;
	}

	public S3Object getS3Object(String key) {
		try {
			GetObjectRequest getObjectRequest = new GetObjectRequest(
				_bucketName, key);

			return _amazonS3.getObject(getObjectRequest);
		}
		catch (AmazonClientException amazonClientException) {
			if (_isFileNotFound(amazonClientException)) {
				return null;
			}

			throw _transform(amazonClientException);
		}
	}

	public Long getS3ObjectContentLength(String key) {
		GetObjectMetadataRequest getObjectMetadataRequest =
			new GetObjectMetadataRequest(_bucketName, key);

		ObjectMetadata objectMetadata = _amazonS3.getObjectMetadata(
			getObjectMetadataRequest);

		if (objectMetadata == null) {
			return null;
		}

		return objectMetadata.getContentLength();
	}

	public List<String> getS3ObjectKeys(String prefix) {
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();

			listObjectsRequest.withBucketName(_bucketName);
			listObjectsRequest.withPrefix(prefix);

			ObjectListing objectListing = _amazonS3.listObjects(
				listObjectsRequest);

			List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>(
				objectListing.getMaxKeys());

			while (true) {
				s3ObjectSummaries.addAll(objectListing.getObjectSummaries());

				if (objectListing.isTruncated()) {
					objectListing = _amazonS3.listNextBatchOfObjects(
						objectListing);
				}
				else {
					break;
				}
			}

			return ListUtil.toList(s3ObjectSummaries, S3ObjectSummary::getKey);
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
	}

	public void putS3Object(String key, File file) {
		Upload upload = null;

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
				_bucketName, key, file);

			putObjectRequest.withStorageClass(_storageClass);

			upload = _transferManager.upload(putObjectRequest);

			upload.waitForCompletion();
		}
		catch (AmazonClientException amazonClientException) {
			throw _transform(amazonClientException);
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(interruptedException, interruptedException);
			}

			upload.abort();

			Thread thread = Thread.currentThread();

			thread.interrupt();
		}
	}

	protected void deleteObjects(String[] keys) {
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(
			_bucketName);

		deleteObjectsRequest.withKeys(keys);

		_amazonS3.deleteObjects(deleteObjectsRequest);
	}

	private boolean _isFileNotFound(
		AmazonClientException amazonClientException) {

		if (amazonClientException instanceof AmazonServiceException) {
			AmazonServiceException amazonServiceException =
				(AmazonServiceException)amazonClientException;

			String errorCode = amazonServiceException.getErrorCode();

			if (errorCode.equals(_ERROR_CODE_FILE_NOT_FOUND) &&
				(amazonServiceException.getStatusCode() ==
					_STATUS_CODE_FILE_NOT_FOUND)) {

				return true;
			}
		}

		return false;
	}

	private SystemException _transform(
		AmazonClientException amazonClientException) {

		if (amazonClientException instanceof AmazonServiceException) {
			AmazonServiceException amazonServiceException =
				(AmazonServiceException)amazonClientException;

			StringBundler sb = new StringBundler(11);

			sb.append("{errorCode=");

			String errorCode = amazonServiceException.getErrorCode();

			sb.append(errorCode);

			sb.append(", errorType=");
			sb.append(amazonServiceException.getErrorType());
			sb.append(", message=");
			sb.append(amazonServiceException.getMessage());
			sb.append(", requestId=");
			sb.append(amazonServiceException.getRequestId());
			sb.append(", statusCode=");
			sb.append(amazonServiceException.getStatusCode());
			sb.append("}");

			if (errorCode.equals("AccessDenied")) {
				return new AccessDeniedException(sb.toString());
			}

			return new SystemException(sb.toString());
		}

		return new SystemException(
			amazonClientException.getMessage(), amazonClientException);
	}

	private static final int _DELETE_MAX = 1000;

	private static final String _ERROR_CODE_FILE_NOT_FOUND = "NoSuchKey";

	private static final int _STATUS_CODE_FILE_NOT_FOUND = 404;

	private static final Log _log = LogFactoryUtil.getLog(
		AmazonS3Repository.class);

	private final AmazonS3 _amazonS3;
	private final String _bucketName;
	private final StorageClass _storageClass;
	private final TransferManager _transferManager;

}
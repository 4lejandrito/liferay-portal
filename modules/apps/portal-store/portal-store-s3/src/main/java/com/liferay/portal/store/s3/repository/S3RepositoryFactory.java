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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.store.s3.configuration.S3StoreConfiguration;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	service = {}
)
public class S3RepositoryFactory {

	public AmazonS3Repository getAmazonS3Repository() {
		AmazonS3 amazonS3 = _getAmazonS3(_getAWSCredentialsProvider());

		StorageClass storageClass;

		try {
			storageClass = StorageClass.fromValue(
				_s3StoreConfiguration.s3StorageClass());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			storageClass = StorageClass.Standard;

			if (_log.isWarnEnabled()) {
				_log.warn(
					_s3StoreConfiguration.s3StorageClass() +
						" is not a valid value for the storage class",
					illegalArgumentException);
			}
		}

		return new AmazonS3Repository(
			amazonS3, _s3StoreConfiguration.bucketName(), storageClass,
			_getTransferManager(amazonS3));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_s3StoreConfiguration = ConfigurableUtil.createConfigurable(
			S3StoreConfiguration.class, properties);
	}

	private void _configureConnectionProtocol(
		ClientConfiguration clientConfiguration) {

		String connectionProtocol = _s3StoreConfiguration.connectionProtocol();

		if (Validator.isNull(connectionProtocol) ||
			connectionProtocol.equals("DEFAULT")) {

			return;
		}

		if (connectionProtocol.equals("HTTP")) {
			clientConfiguration.setProtocol(Protocol.HTTP);
		}
		else {
			clientConfiguration.setProtocol(Protocol.HTTPS);
		}
	}

	private void _configureProxySettings(
		ClientConfiguration clientConfiguration) {

		String proxyHost = _s3StoreConfiguration.proxyHost();

		if (Validator.isNull(proxyHost)) {
			return;
		}

		clientConfiguration.setProxyHost(proxyHost);
		clientConfiguration.setProxyPort(_s3StoreConfiguration.proxyPort());

		String proxyAuthType = _s3StoreConfiguration.proxyAuthType();

		if (proxyAuthType.equals("ntlm") ||
			proxyAuthType.equals("username-password")) {

			clientConfiguration.setProxyPassword(
				_s3StoreConfiguration.proxyPassword());
			clientConfiguration.setProxyUsername(
				_s3StoreConfiguration.proxyUsername());

			if (proxyAuthType.equals("ntlm")) {
				clientConfiguration.setProxyDomain(
					_s3StoreConfiguration.ntlmProxyDomain());
				clientConfiguration.setProxyWorkstation(
					_s3StoreConfiguration.ntlmProxyWorkstation());
			}
		}
	}

	private void _configureS3Endpoint(AmazonS3 amazonS3) {
		String s3Endpoint = _s3StoreConfiguration.s3Endpoint();

		if (Validator.isNull(s3Endpoint)) {
			return;
		}

		amazonS3.setEndpoint(s3Endpoint);
	}

	private void _configureS3PathStyle(AmazonS3 amazonS3) {
		boolean s3PathStyle = _s3StoreConfiguration.s3PathStyle();

		if (!s3PathStyle) {
			return;
		}

		S3ClientOptions s3ClientOptions = new S3ClientOptions();

		s3ClientOptions.setPathStyleAccess(true);

		amazonS3.setS3ClientOptions(s3ClientOptions);
	}

	private void _configureSignerOverride(
		ClientConfiguration clientConfiguration) {

		String signerOverride = _s3StoreConfiguration.signerOverride();

		if (Validator.isNull(signerOverride)) {
			return;
		}

		clientConfiguration.setSignerOverride(signerOverride);
	}

	private AmazonS3 _getAmazonS3(
		AWSCredentialsProvider awsCredentialsProvider) {

		AmazonS3 amazonS3 = new AmazonS3Client(
			awsCredentialsProvider, _getClientConfiguration());

		Region region = Region.getRegion(
			Regions.fromName(_s3StoreConfiguration.s3Region()));

		amazonS3.setRegion(region);

		_configureS3Endpoint(amazonS3);
		_configureS3PathStyle(amazonS3);

		return amazonS3;
	}

	private AWSCredentialsProvider _getAWSCredentialsProvider() {
		if (Validator.isNotNull(_s3StoreConfiguration.accessKey()) &&
			Validator.isNotNull(_s3StoreConfiguration.secretKey())) {

			AWSCredentials awsCredentials = new BasicAWSCredentials(
				_s3StoreConfiguration.accessKey(),
				_s3StoreConfiguration.secretKey());

			return new StaticCredentialsProvider(awsCredentials);
		}

		return new DefaultAWSCredentialsProviderChain();
	}

	private ClientConfiguration _getClientConfiguration() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();

		clientConfiguration.setConnectionTimeout(
			_s3StoreConfiguration.connectionTimeout());

		clientConfiguration.setMaxErrorRetry(
			_s3StoreConfiguration.httpClientMaxErrorRetry());
		clientConfiguration.setMaxConnections(
			_s3StoreConfiguration.httpClientMaxConnections());

		_configureConnectionProtocol(clientConfiguration);
		_configureProxySettings(clientConfiguration);
		_configureSignerOverride(clientConfiguration);

		return clientConfiguration;
	}

	private TransferManager _getTransferManager(AmazonS3 amazonS3) {
		ExecutorService executorService = new ThreadPoolExecutor(
			_s3StoreConfiguration.corePoolSize(),
			_s3StoreConfiguration.maxPoolSize());

		TransferManager transferManager = new TransferManager(
			amazonS3, executorService, false);

		TransferManagerConfiguration transferManagerConfiguration =
			new TransferManagerConfiguration();

		transferManagerConfiguration.setMinimumUploadPartSize(
			_s3StoreConfiguration.minimumUploadPartSize());
		transferManagerConfiguration.setMultipartUploadThreshold(
			_s3StoreConfiguration.multipartUploadThreshold());

		transferManager.setConfiguration(transferManagerConfiguration);

		return transferManager;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		S3RepositoryFactory.class);

	private S3StoreConfiguration _s3StoreConfiguration;

}
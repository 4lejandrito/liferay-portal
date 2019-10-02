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

package com.liferay.asset.auto.tagger.opennlp.internal.upgrade.v1_0_0;

import com.liferay.asset.auto.tagger.opennlp.internal.configuration.OpenNLPDocumentAssetAutoTaggerCompanyConfiguration;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Alejandro Tardín
 */
public class UpgradeConfiguration extends UpgradeProcess {

	public UpgradeConfiguration(
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider) {

		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeConfigurations(
			"com.liferay.document.library.asset.auto.tagger.opennlp.internal." +
				"configuration." +
					"OpenNLPDocumentAssetAutoTagProviderCompanyConfiguration",
			DLFileEntryConstants.getClassName());

		_upgradeConfigurations(
			"com.liferay.journal.asset.auto.tagger.opennlp.internal." +
				"configuration." +
					"OpenNLPDocumentAssetAutoTagProviderCompanyConfiguration",
			"com.liferay.journal.model.JournalArticle");
	}

	private void _addClassNameToConfiguration(String className, long companyId)
		throws Exception {

		Configuration[] configurations;

		if (companyId != -1) {
			configurations = _configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(&(", Constants.SERVICE_PID, "=",
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class.
						getName(),
					".scoped)(companyId=", companyId, "))"));

			if ((configurations == null) || (configurations.length == 0)) {
				Dictionary properties = new HashMapDictionary();

				properties.put("enabledClassNames", new String[] {className});

				_configurationProvider.saveCompanyConfiguration(
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class,
					companyId, properties);

				return;
			}
		}
		else {
			configurations = _configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(", Constants.SERVICE_PID, "=",
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class.
						getName(),
					")"));

			if ((configurations == null) || (configurations.length == 0)) {
				Dictionary properties = new HashMapDictionary();

				properties.put("enabledClassNames", new String[] {className});

				_configurationProvider.saveSystemConfiguration(
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class,
					properties);

				return;
			}
		}

		for (Configuration configuration : configurations) {
			Dictionary properties = configuration.getProperties();

			String[] enabledClassNames = (String[])properties.get(
				"enabledClassNames");

			if (!ArrayUtil.contains(enabledClassNames, className)) {
				enabledClassNames = ArrayUtil.append(
					enabledClassNames, className);

				properties.put("enabledClassNames", enabledClassNames);

				configuration.update(properties);
			}
		}
	}

	private void _upgradeConfiguration(String className, String filterString)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (properties == null) {
				return;
			}

			boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));

			if (enabled) {
				long companyId = GetterUtil.getLong(
					properties.get("companyId"), -1);

				_addClassNameToConfiguration(className, companyId);
			}
		}
	}

	private void _upgradeConfigurations(String servicePid, String className)
		throws Exception {

		_upgradeConfiguration(
			className,
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=", servicePid, ")"));

		_upgradeConfiguration(
			className,
			StringBundler.concat(
				"(service.factoryPid=", servicePid, ".scoped)"));
	}

	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}
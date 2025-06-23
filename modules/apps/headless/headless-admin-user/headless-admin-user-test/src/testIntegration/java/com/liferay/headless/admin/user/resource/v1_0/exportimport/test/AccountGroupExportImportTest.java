/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.exportimport.test;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountGroupResource;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-35914"), @FeatureFlag("LPD-47858")}
)
@RunWith(Arquillian.class)
public class AccountGroupExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, true, "LPD-35914");
	}

	@AfterClass
	public static void tearDownClass() {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, false, "LPD-35914");
	}

	@Before
	public void setUp() throws Exception {
		Group testGroup = GroupTestUtil.addGroup();

		Company testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		_accountResource = AccountResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		_accountGroupResource = AccountGroupResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Test
	public void test() throws Exception {
		Page<AccountGroup> accountGroupsPage =
			_accountGroupResource.getAccountGroupsPage(null, null, null, null);

		long totalCount = accountGroupsPage.getTotalCount();

		Account account = _accountResource.postAccount(
			new Account() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					name = RandomTestUtil.randomString();
					type = Type.BUSINESS;
				}
			});

		AccountGroup accountGroup1 = _accountGroupResource.postAccountGroup(
			new AccountGroup() {
				{
					accountBriefs = new AccountBrief[] {
						new AccountBrief() {
							{
								externalReferenceCode =
									account.getExternalReferenceCode();
								name = account.getName();
								type = account.getTypeAsString();
							}
						}
					};
					externalReferenceCode = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
					id = RandomTestUtil.randomLong();
					name = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
				}
			});
		AccountGroup accountGroup2 = _accountGroupResource.postAccountGroup(
			new AccountGroup() {
				{
					externalReferenceCode = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
					id = RandomTestUtil.randomLong();
					name = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
				}
			});

		accountGroupsPage = _accountGroupResource.getAccountGroupsPage(
			null, null, null, null);

		Assert.assertEquals(totalCount + 2, accountGroupsPage.getTotalCount());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									AccountPortletKeys.ACCOUNT_GROUPS_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_accountResource.deleteAccountByExternalReferenceCode(
			account.getExternalReferenceCode());
		_accountGroupResource.deleteAccountGroupByExternalReferenceCode(
			accountGroup1.getExternalReferenceCode());
		_accountGroupResource.deleteAccountGroupByExternalReferenceCode(
			accountGroup2.getExternalReferenceCode());

		accountGroupsPage = _accountGroupResource.getAccountGroupsPage(
			null, null, null, null);

		Assert.assertEquals(totalCount, accountGroupsPage.getTotalCount());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).build()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		accountGroupsPage = _accountGroupResource.getAccountGroupsPage(
			null, null, null, null);

		Assert.assertEquals(totalCount + 2, accountGroupsPage.getTotalCount());

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntries.get(0);

		Assert.assertEquals(
			account.getExternalReferenceCode(),
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_INCOMPLETE,
			exportImportReportEntry.getType());
	}

	private AccountGroupResource _accountGroupResource;
	private AccountResource _accountResource;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}
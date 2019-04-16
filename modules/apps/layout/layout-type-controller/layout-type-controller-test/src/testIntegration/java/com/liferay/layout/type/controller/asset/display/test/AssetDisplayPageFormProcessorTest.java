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

package com.liferay.layout.type.controller.asset.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.asset.util.test.AssetTestUtil;

import java.security.Principal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.WindowState;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageFormProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessWithNotValidAsset() throws Exception {
		long assetDisplayPageId = RandomTestUtil.randomLong();

		_assetDisplayPageEntryFormProcessor.process(
			RandomTestUtil.randomString(), 0,
			new MockPortletRequest(assetDisplayPageId));

		List<AssetDisplayPageEntry> assetDisplayPageEntries =
			_assetDisplayPageEntryLocalService.
				getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
					assetDisplayPageId);

		Assert.assertEquals(
			String.valueOf(assetDisplayPageEntries), 0,
			assetDisplayPageEntries.size());
	}

	@Test
	public void testProcessWithValidAsset() throws Exception {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		long assetDisplayPageId = RandomTestUtil.randomLong();

		_assetDisplayPageEntryFormProcessor.process(
			assetEntry.getClassName(), assetEntry.getClassPK(),
			new MockPortletRequest(assetDisplayPageId));

		List<AssetDisplayPageEntry> assetDisplayPageEntries =
			_assetDisplayPageEntryLocalService.
				getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
					assetDisplayPageId);

		Assert.assertEquals(
			String.valueOf(assetDisplayPageEntries), 1,
			assetDisplayPageEntries.size());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		themeDisplay.setUser(TestPropsValues.getUser());

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		return themeDisplay;
	}

	@Inject
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private class MockPortletRequest implements PortletRequest {

		public MockPortletRequest(long assetDisplayPageId) throws Exception {
			_attributes = new HashMap<String, Object>() {
				{
					put(WebKeys.THEME_DISPLAY, _getThemeDisplay());
					put(WebKeys.CURRENT_URL, RandomTestUtil.randomString());
					put(
						PortletServlet.PORTLET_SERVLET_REQUEST,
						new MockHttpServletRequest());
				}
			};

			_parameters = new HashMap<String, String[]>() {
				{
					put(
						"displayPageType",
						new String[] {
							String.valueOf(
								AssetDisplayPageConstants.TYPE_DEFAULT)
						});
					put(
						"assetDisplayPageId",
						new String[] {String.valueOf(assetDisplayPageId)});
					put(
						Constants.CMD,
						new String[] {RandomTestUtil.randomString()});
					put(
						"formDate",
						new String[] {
							String.valueOf(RandomTestUtil.randomLong())
						});
					put(
						"assetEntryVisible",
						new String[] {String.valueOf(Boolean.TRUE)});
				}
			};
		}

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(_attributes.keySet());
		}

		@Override
		public String getAuthType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContextPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Cookie[] getCookies() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Locale getLocale() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<Locale> getLocales() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getParameter(String name) {
			String[] parameters = _parameters.get(name);

			if (ArrayUtil.isEmpty(parameters)) {
				return null;
			}

			if (parameters.length > 1) {
				throw new AssertionError(
					"Unexpected value for: " + name + " values: " + parameters);
			}

			return parameters[0];
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return _parameters;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String[] getParameterValues(String name) {
			return _parameters.get(name);
		}

		@Override
		public PortalContext getPortalContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletContext getPortletContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletMode getPortletMode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletSession getPortletSession() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletSession getPortletSession(boolean create) {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletPreferences getPreferences() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, String[]> getPrivateParameterMap() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getProperties(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getPropertyNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, String[]> getPublicParameterMap() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRemoteUser() {
			throw new UnsupportedOperationException();
		}

		@Override
		public RenderParameters getRenderParameters() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRequestedSessionId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getResponseContentType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getResponseContentTypes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getScheme() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getServerName() {
			return RandomTestUtil.randomString();
		}

		@Override
		public int getServerPort() {
			return RandomTestUtil.randomInt();
		}

		@Override
		public String getUserAgent() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Principal getUserPrincipal() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getWindowID() {
			throw new UnsupportedOperationException();
		}

		@Override
		public WindowState getWindowState() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isPortletModeAllowed(PortletMode portletMode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public boolean isUserInRole(String s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isWindowStateAllowed(WindowState windowState) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeAttribute(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setAttribute(String name, Object value) {
			throw new UnsupportedOperationException();
		}

		private final Map<String, Object> _attributes;
		private final Map<String, String[]> _parameters;

	}

}
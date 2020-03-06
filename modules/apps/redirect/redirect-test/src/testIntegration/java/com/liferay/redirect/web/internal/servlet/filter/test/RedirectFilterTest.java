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

package com.liferay.redirect.web.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;

import javax.servlet.Filter;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class RedirectFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDefaultsToTheFilterChainIfNoMatchingRedirectEntry()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_redirectFilter.doFilter(
			new MockHttpServletRequest("GET", "/web/guest/path"),
			mockHttpServletResponse, new MockFilterChain());

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testSendsPermanentRedirectIfMatchingRedirectEntry()
		throws Exception {

		RedirectEntry redirectEntry =
			_redirectEntryLocalService.addRedirectEntry(
				TestPropsValues.getGroupId(), "http://www.liferay.com", "path",
				false, ServiceContextTestUtil.getServiceContext());

		try {
			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_redirectFilter.doFilter(
				new MockHttpServletRequest("GET", "/web/guest/path"),
				mockHttpServletResponse, new MockFilterChain());

			Assert.assertEquals(301, mockHttpServletResponse.getStatus());
			Assert.assertEquals(
				"http://www.liferay.com",
				mockHttpServletResponse.getHeader("Location"));
		}
		finally {
			_redirectEntryLocalService.deleteRedirectEntry(redirectEntry);
		}
	}

	@Test
	public void testSendsTemporaryRedirectIfMatchingRedirectEntry()
		throws Exception {

		RedirectEntry redirectEntry =
			_redirectEntryLocalService.addRedirectEntry(
				TestPropsValues.getGroupId(), "http://www.liferay.com", "path",
				true, ServiceContextTestUtil.getServiceContext());

		try {
			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_redirectFilter.doFilter(
				new MockHttpServletRequest("GET", "/web/guest/path"),
				mockHttpServletResponse, new MockFilterChain());

			Assert.assertEquals(302, mockHttpServletResponse.getStatus());
			Assert.assertEquals(
				"http://www.liferay.com",
				mockHttpServletResponse.getHeader("Location"));
		}
		finally {
			_redirectEntryLocalService.deleteRedirectEntry(redirectEntry);
		}
	}

	@Inject
	private RedirectEntryLocalService _redirectEntryLocalService;

	@Inject(filter = "(&(component.name=*.RedirectFilter)(url-pattern=/*))")
	private Filter _redirectFilter;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class FeatureFlagTestUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();

		_key = "FAKE-" + RandomTestUtil.randomString();

		_keyTestFeatureFlagListener = new TestFeatureFlagListener(_key);

		_wildcardTestFeatureFlagListener = new TestFeatureFlagListener("*");
	}

	@After
	public void tearDown() {
		_keyTestFeatureFlagListener.close();
		_wildcardTestFeatureFlagListener.close();
	}

	@Test
	public void testSetFeatureFlagsWithSafeCloseable() {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, _key)) {

			_keyTestFeatureFlagListener.assertInvocations();
			_wildcardTestFeatureFlagListener.assertInvocations(
				_getInvocationString(CompanyConstants.SYSTEM, true),
				_getInvocationString(_companyId, true));

			try (SafeCloseable nestedSafeCloseable =
					FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
						true, _key)) {

				_wildcardTestFeatureFlagListener.assertInvocations();
			}

			_wildcardTestFeatureFlagListener.assertInvocations();
		}

		_keyTestFeatureFlagListener.assertInvocations();
		_wildcardTestFeatureFlagListener.assertInvocations(
			_getInvocationString(CompanyConstants.SYSTEM, false),
			_getInvocationString(_companyId, false));
	}

	private String _getInvocationString(long companyId, boolean enabled) {
		return StringBundler.concat(companyId, " ", _key, " ", enabled);
	}

	private long _companyId;
	private String _key;
	private TestFeatureFlagListener _keyTestFeatureFlagListener;
	private TestFeatureFlagListener _wildcardTestFeatureFlagListener;

	private class TestFeatureFlagListener
		implements FeatureFlagListener, SafeCloseable {

		public TestFeatureFlagListener(String featureFlagKey) {
			Bundle bundle = FrameworkUtil.getBundle(
				FeatureFlagTestUtilTest.class);

			BundleContext bundleContext = bundle.getBundleContext();

			_serviceRegistration = bundleContext.registerService(
				FeatureFlagListener.class, this,
				HashMapDictionaryBuilder.<String, Object>put(
					"feature.flag.key", featureFlagKey
				).build());
		}

		public void assertInvocations(String... expectedInvocationStrings) {
			Arrays.sort(expectedInvocationStrings);

			_invocationStrings.sort(null);

			Assert.assertEquals(
				Arrays.asList(expectedInvocationStrings), _invocationStrings);

			_invocationStrings.clear();
		}

		@Override
		public void close() {
			_serviceRegistration.unregister();
		}

		@Override
		public void onValue(
			long companyId, String featureFlagKey, boolean enabled) {

			if (((companyId == CompanyConstants.SYSTEM) ||
				 (companyId == _companyId)) &&
				featureFlagKey.equals(_key)) {

				_invocationStrings.add(
					_getInvocationString(companyId, enabled));
			}
		}

		private final List<String> _invocationStrings = new ArrayList<>();
		private final ServiceRegistration<FeatureFlagListener>
			_serviceRegistration;

	}

}
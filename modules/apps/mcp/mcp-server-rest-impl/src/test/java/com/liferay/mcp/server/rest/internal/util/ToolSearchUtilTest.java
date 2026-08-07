/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class ToolSearchUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSearchExcludesTheToolsThatMatchNothing() {
		Assert.assertEquals(
			Collections.emptyList(),
			_search(
				"taxonomy",
				_toolSummary(
					"Creates a blog post", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchMatchesTheCamelCasedToolName() {
		Assert.assertEquals(
			Arrays.asList("postSiteBlogPosting"),
			_search(
				"blog posting",
				_toolSummary(
					"Nothing relevant here", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchMatchesThePluralAgainstTheSingular() {
		Assert.assertEquals(
			Arrays.asList("postSiteBlogPosting"),
			_search(
				"blog posts",
				_toolSummary(
					"Nothing relevant here", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
		Assert.assertEquals(
			Arrays.asList("getSiteBlogPostingsPage"),
			_search(
				"blog posting",
				_toolSummary(
					"Nothing relevant here", "getSiteBlogPostingsPage",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchOrdersTheEqualScoresDeterministically() {
		List<ToolSummary> toolSummaries = Arrays.asList(
			_toolSummary("Blog", "zzzBlog", "headless-delivery-v1.0"),
			_toolSummary("Blog", "aaaBlog", "headless-delivery-v1.0"),
			_toolSummary("Blog", "mmmBlogLonger", "headless-delivery-v1.0"));

		Assert.assertEquals(
			Arrays.asList("aaaBlog", "zzzBlog", "mmmBlogLonger"),
			_search("blog", toolSummaries));
	}

	@Test
	public void testSearchRanksTheNameAboveTheDescription() {
		Assert.assertEquals(
			Arrays.asList("postSiteBlogPosting", "postSiteDocument"),
			_search(
				"blog",
				_toolSummary(
					"Creates a document on a blog", "postSiteDocument",
					"headless-delivery-v1.0"),
				_toolSummary(
					"Creates something", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchRanksTheToolMatchingEveryTokenFirst() {
		Assert.assertEquals(
			Arrays.asList("postSiteBlogPosting", "postSiteBlogPostingImage"),
			_search(
				"blog posting",
				_toolSummary(
					"Creates a blog posting image", "postSiteBlogPostingImage",
					"headless-delivery-v1.0"),
				_toolSummary(
					"Creates a blog posting", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchReturnsNothingWhenTheSearchHasOnlyStopWords() {
		Assert.assertEquals(
			Collections.emptyList(),
			_search(
				"of the and",
				_toolSummary(
					"Creates a blog post", "postSiteBlogPosting",
					"headless-delivery-v1.0")));
	}

	@Test
	public void testSearchUsesTheToolSetAsATieBreaker() {
		Assert.assertEquals(
			Arrays.asList("postThing", "postOther"),
			_search(
				"taxonomy thing",
				_toolSummary("Creates a thing", "postOther", "unrelated-v1.0"),
				_toolSummary(
					"Creates a thing", "postThing",
					"headless-admin-taxonomy-v1.0")));
	}

	private List<String> _search(
		String search, List<ToolSummary> toolSummaries) {

		Map<String, String> toolSetDescriptions = HashMapBuilder.put(
			"headless-admin-taxonomy-v1.0", "Manage taxonomy vocabularies"
		).put(
			"headless-delivery-v1.0", "Deliver site content"
		).put(
			"unrelated-v1.0", "Something else entirely"
		).build();

		List<String> names = new ArrayList<>();

		for (ToolSummary toolSummary :
				ToolSearchUtil.search(
					search, toolSetDescriptions, toolSummaries)) {

			names.add(toolSummary.getName());
		}

		return names;
	}

	private List<String> _search(String search, ToolSummary... toolSummaries) {
		return _search(search, Arrays.asList(toolSummaries));
	}

	private ToolSummary _toolSummary(
		String description, String name, String toolSetName) {

		ToolSummary toolSummary = new ToolSummary();

		toolSummary.setDescription(description);
		toolSummary.setName(name);
		toolSummary.setToolSetName(toolSetName);

		return toolSummary;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ranks tool summaries against free-text search terms.
 *
 * @author Alejandro Tardín
 */
public class ToolSearchUtil {

	/**
	 * Returns every tool summary that matches the search terms, ranked best
	 * first. The caller is responsible for capping the result.
	 */
	public static List<ToolSummary> search(
		String search, Map<String, String> toolSetDescriptions,
		List<ToolSummary> toolSummaries) {

		List<ToolSummary> results = new ArrayList<>();

		Set<String> searchTokens = _tokenize(search);

		if (searchTokens.isEmpty()) {
			return results;
		}

		Map<String, Set<String>> toolSetTokens = new HashMap<>();

		for (Map.Entry<String, String> entry : toolSetDescriptions.entrySet()) {
			toolSetTokens.put(
				entry.getKey(),
				_tokenize(
					_splitCamelCase(entry.getKey()) + StringPool.SPACE +
						entry.getValue()));
		}

		List<ScoredToolSummary> scoredToolSummaries = new ArrayList<>();

		for (ToolSummary toolSummary : toolSummaries) {
			int score = _getScore(
				searchTokens, toolSetTokens.get(toolSummary.getToolSetName()),
				toolSummary);

			if (score > 0) {
				scoredToolSummaries.add(
					new ScoredToolSummary(score, toolSummary));
			}
		}

		scoredToolSummaries.sort(
			Comparator.comparingInt(
				(ScoredToolSummary scoredToolSummary) ->
					scoredToolSummary._score
			).reversed(
			).thenComparingInt(
				scoredToolSummary -> scoredToolSummary._name.length()
			).thenComparing(
				scoredToolSummary -> scoredToolSummary._name
			));

		for (ScoredToolSummary scoredToolSummary : scoredToolSummaries) {
			results.add(scoredToolSummary._toolSummary);
		}

		return results;
	}

	private static String _getName(ToolSummary toolSummary) {
		if (Validator.isNull(toolSummary.getName())) {
			return StringPool.BLANK;
		}

		return StringUtil.toLowerCase(toolSummary.getName());
	}

	private static int _getScore(
		Set<String> searchTokens, Set<String> toolSetTokens,
		ToolSummary toolSummary) {

		int score = 0;

		Set<String> descriptionTokens = _tokenize(toolSummary.getDescription());
		String name = _getName(toolSummary);
		Set<String> nameTokens = _tokenize(
			_splitCamelCase(toolSummary.getName()));

		int matchedTokenCount = 0;

		for (String searchToken : searchTokens) {
			int tokenScore = 0;

			if (nameTokens.contains(searchToken)) {
				tokenScore += _SCORE_NAME_WORD;
			}
			else if (name.contains(searchToken)) {
				tokenScore += _SCORE_NAME_SUBSTRING;
			}

			if (descriptionTokens.contains(searchToken)) {
				tokenScore += _SCORE_DESCRIPTION_WORD;
			}

			if ((toolSetTokens != null) &&
				toolSetTokens.contains(searchToken)) {

				tokenScore += _SCORE_TOOL_SET_WORD;
			}

			if (tokenScore > 0) {
				matchedTokenCount++;
			}

			score += tokenScore;
		}

		if ((score > 0) && (matchedTokenCount == searchTokens.size())) {
			score += _SCORE_COVERAGE;
		}

		return score;
	}

	private static String _splitCamelCase(String value) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		return _camelCasePattern.matcher(
			value
		).replaceAll(
			StringPool.SPACE
		);
	}

	private static Set<String> _tokenize(String value) {
		Set<String> tokens = new HashSet<>();

		if (Validator.isNull(value)) {
			return tokens;
		}

		for (String token :
				_separatorPattern.split(StringUtil.toLowerCase(value))) {

			if ((token.length() <= 1) || _stopWords.contains(token)) {
				continue;
			}

			tokens.add(token);

			// Fold the plural into the singular on both the search terms and
			// the tool metadata, so that "blog posts" matches
			// "postSiteBlogPosting" and "profile" matches "profiles"

			if ((token.length() > 3) && token.endsWith("s")) {
				tokens.add(token.substring(0, token.length() - 1));
			}
		}

		return tokens;
	}

	private static final int _SCORE_COVERAGE = 5;

	private static final int _SCORE_DESCRIPTION_WORD = 2;

	private static final int _SCORE_NAME_SUBSTRING = 4;

	private static final int _SCORE_NAME_WORD = 8;

	private static final int _SCORE_TOOL_SET_WORD = 1;

	private static final Pattern _camelCasePattern = Pattern.compile(
		"(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
	private static final Pattern _separatorPattern = Pattern.compile(
		"[^a-z0-9]+");
	private static final Set<String> _stopWords = Set.of(
		"an", "and", "are", "as", "at", "be", "but", "by", "for", "from", "how",
		"in", "into", "is", "it", "of", "on", "or", "that", "the", "to",
		"with");

	private static class ScoredToolSummary {

		private ScoredToolSummary(int score, ToolSummary toolSummary) {
			_score = score;
			_toolSummary = toolSummary;

			_name = _getName(toolSummary);
		}

		private final String _name;
		private final int _score;
		private final ToolSummary _toolSummary;

	}

}
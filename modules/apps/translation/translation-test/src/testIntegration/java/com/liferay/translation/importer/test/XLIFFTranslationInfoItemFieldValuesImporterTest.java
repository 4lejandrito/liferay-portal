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

package com.liferay.translation.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemClassPKReference;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.importer.TranslationInfoItemFieldValuesImporter;
import com.liferay.translation.test.util.TranslationTestUtil;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class XLIFFTranslationInfoItemFieldValuesImporterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule testRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test(expected = XLIFFFileException.MustHaveValidId.class)
	public void testImportXLIFF12FailsFileInvalidId() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"example-1_2-simple.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(), RandomTestUtil.randomLong()),
			byteArrayInputStream);
	}

	@Test(expected = XLIFFFileException.MustBeWellFormed.class)
	public void testImportXLIFF12FailsFileInvalidVersion() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"example-1_2-bad-formed.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			byteArrayInputStream);
	}

	@Test
	public void testImportXLIFF12VersionDocument() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"example-1_2-oasis.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemClassPKReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					byteArrayInputStream);

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	@Test
	public void testImportXLIFF12VersionSimpleDocument() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"example-1_2-simple.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemClassPKReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					byteArrayInputStream);

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	public void testImportXLIFF20FailsFileInvalidGroupLanguage()
		throws Exception {

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), _locales, LocaleUtil.US);
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"test-journal-article-v20-ja-JP.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			byteArrayInputStream);
	}

	@Test(expected = XLIFFFileException.MustHaveValidId.class)
	public void testImportXLIFF20FailsFileInvalidId() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"test-journal-article-v20.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(), RandomTestUtil.randomLong()),
			byteArrayInputStream);
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	public void testImportXLIFF20FailsFileInvalidLanguage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"test-journal-article-v20-pt-PT.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			byteArrayInputStream);
	}

	@Test(expected = XLIFFFileException.MustBeWellFormed.class)
	public void testImportXLIFF20FailsFileNoTarget() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"test-journal-article-no-target.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemClassPKReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			byteArrayInputStream);
	}

	@Test
	public void testImportXLIFF20VersionDocument() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ByteArrayInputStream byteArrayInputStream =
			TranslationTestUtil.getChangeIdByteArrayInputStream(
				"test-journal-article-v20.xlf", "$ARTICLE_ID",
				journalArticle.getResourcePrimKey());

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemClassPKReference(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					byteArrayInputStream);

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	private static final Set<Locale> _locales = new HashSet<>(
		Arrays.asList(
			LocaleUtil.BRAZIL, LocaleUtil.HUNGARY, LocaleUtil.SPAIN,
			LocaleUtil.US));

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "content.type=application/xliff+xml")
	private TranslationInfoItemFieldValuesImporter
		_xliffTranslationInfoItemFieldValuesImporter;

}
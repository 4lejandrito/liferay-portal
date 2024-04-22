/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../fixtures/commercePagesTest';
import {loginTest} from '../../fixtures/loginTest';

export const test = mergeTests(apiHelpersTest, commercePagesTest, loginTest());

test('LPD-13559 bulk actions for product relations', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
	page,
}) => {
	await page.goto('/');

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});
	const product3 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	await Promise.all([
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product2.productId}
		),
		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product3.productId}
		),
	]);

	try {
		await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

		await commerceAdminProductDetailsPage.goToProductRelations();

		await expect(
			(
				await commerceAdminProductDetailsProductRelationsPage.tableRow(
					2,
					product2.name['en_US'],
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await commerceAdminProductDetailsProductRelationsPage.tableRow(
					2,
					product3.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		await commerceAdminProductDetailsProductRelationsPage.selectItemsInput.check();

		await expect(
			commerceAdminProductDetailsProductRelationsPage.deleteBulkButton
		).toBeVisible();

		await commerceAdminProductDetailsProductRelationsPage.deleteBulkButton.click();

		await expect(
			commerceAdminProductDetailsProductRelationsPage.emptyTableMessage
		).toBeVisible();
	}
	finally {
		await Promise.all([
			apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
				product1.productId
			),
			apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
				product2.productId
			),
			apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
				product3.productId
			),
		]);

		await apiHelpers.headlessCommerceAdminCatalog.deleteCatalog(catalog.id);
	}
});

test('LPD-22886 Update published status on product relations', async ({
																apiHelpers,
																commerceAdminProductDetailsPage,
																commerceAdminProductPage,
																page,
															}) => {
	await page.goto('/');

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productStatus: 2,
	});

	try {
		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.productRelationsTab.click();

		// Your selector was not matching for me locally since it did not have qa-id
		await expect(
			page.locator('.workflow-status', {hasText: 'Draft'})
		).toBeVisible();

		// This makes it work, I suspect JS need to be fully loaded before clicking publish, find a better way to do it (ideally button should be disabled and only enabled when the js has fully loaded)
		await new Promise(resolve => setTimeout(resolve, 5000));

		await commerceAdminProductDetailsPage.headerActionButton('Publish').click();

		// Your selector was not matching for me locally since it did not have qa-id
		await expect(
			page.locator('.workflow-status', {hasText: 'Approved'})
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
			product.productId
		);
		await apiHelpers.headlessCommerceAdminCatalog.deleteCatalog(catalog.id);
	}
});
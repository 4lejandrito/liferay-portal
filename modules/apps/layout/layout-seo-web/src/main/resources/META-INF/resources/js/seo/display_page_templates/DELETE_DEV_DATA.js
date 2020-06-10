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

export default {
	fields: [
		{
			key: 'title',
			label: 'Title',
		},
		{
			key: 'description',
			label: 'Description',
		},
		{
			key: 'smallImage',
			label: 'Small Image',
		},
		{
			key: 'authorName',
			label: 'Author Name',
		},
		{
			key: 'authorProfileImage',
			label: 'Author Profile Image',
		},
		{
			key: 'lastEditorName',
			label: 'Last Editor Name',
		},
		{
			key: 'lastEditorProfileImage',
			label: 'Last Editor Profile Image',
		},
		{
			key: 'publishDate',
			label: 'Publish Date',
		},
		{
			key: 'displayPageURL',
			label: 'Display Page URL',
		},
		{
			key: 'categories',
			label: 'Categories',
		},
		{
			key: 'tagNames',
			label: 'Tags',
		},
	],
	openGraphDescription: 'description',
	openGraphImage: 'smallImage',
	openGraphImageAlt: 'description',
	openGraphTitle: 'title',
	portletNamespace: 'com_liferay_layout_admin_web_portlet_GroupPagesPortlet',
	selectedSource: {
		className: 'com.liferay.journal.model.JournalArticle',
		classNameLabel: 'Web Content Article',
		classTypeId: '36146',
		classTypeLabel: 'Basic Web Content',
	},
};

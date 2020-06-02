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
			key: 'categories',
			label: 'Categories',
			type: 'text',
		},

		{
			key: 'tagNames',
			label: 'Tags',
			type: 'text',
		},

		{
			key: 'displayPageURL',
			label: 'Display Page URL',
			type: 'text',
		},

		{
			key: 'description',
			label: 'Description',
			type: 'text',
		},

		{
			key: 'publishDate',
			label: 'Publish Date',
			type: 'text',
		},

		{
			key: 'smallImage',
			label: 'Small Image',
			type: 'image',
		},

		{
			key: 'summary',
			label: 'Summary',
			type: 'text',
		},

		{
			key: 'title',
			label: 'Title',
			type: 'text',
		},

		{
			key: 'lastEditorName',
			label: 'Last Editor Name',
			type: 'text',
		},

		{
			key: 'lastEditorProfileImage',
			label: 'Last Editor Profile Image',
			type: 'image',
		},

		{
			key: 'authorName',
			label: 'Author Name',
			type: 'text',
		},

		{
			key: 'authorProfileImage',
			label: 'Author Profile Image',
			type: 'image',
		},

		{
			key: 'content',
			label: 'Content',
			type: 'text',
		},

		{
			key: 'ddmTemplate_BASIC_WEB_CONTENT',
			label: 'Basic Web Content *',
			type: 'text',
		},
	],
	selectedField: {
		key: 'description',
		label: 'Description',
	},
	selectedSource: {key: 'structure', label: 'Basic Web Content'},
};

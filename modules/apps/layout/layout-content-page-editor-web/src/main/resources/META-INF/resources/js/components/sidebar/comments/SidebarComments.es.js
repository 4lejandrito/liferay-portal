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

import React from 'react';

import {NoCommentsMessage} from './NoCommentsMessage.es';
import {FragmentComments} from './FragmentComments.es';
import {FRAGMENTS_EDITOR_ITEM_TYPES} from '../../../utils/constants';
import {getItemPath} from '../../../utils/FragmentsEditorGetUtils.es';
import useSelector from '../../../store/hooks/selector.es';

const SidebarComments = () => {
	const activeItemId = useSelector(state => state.activeItemId);
	const activeItemType = useSelector(state => state.activeItemType);
	const structure = useSelector(state => state.layoutData.structure);

	const activeFragmentEntryLink = getItemPath(
		activeItemId,
		activeItemType,
		structure
	).find(
		activeItem =>
			activeItem.itemType === FRAGMENTS_EDITOR_ITEM_TYPES.fragment
	);

	return activeFragmentEntryLink ? (
		<FragmentComments
			fragmentEntryLinkId={activeFragmentEntryLink.itemId}
		/>
	) : (
		<NoCommentsMessage />
	);
};

export {SidebarComments};
export default SidebarComments;

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

/* eslint no-unused-vars: "warn" */

import PropTypes from 'prop-types';
import React, {useState} from 'react';

import Button from '../../common/Button.es';
import UserIcon from '../../common/UserIcon.es';
import {deleteFragmentEntryLinkComment} from '../../../utils/FragmentsEditorFetchUtils.es';

const FragmentComment = props => {
	const [deletingComment, setDeletingComment] = useState(false);
	const _handleDeleteButtonClick = () => {
		setDeletingComment(true);

		deleteFragmentEntryLinkComment(props.commentId).then(() => {
			setDeletingComment(false);
		});
	};
	return (
		<article className="fragments-editor__fragment-comment small">
			<div className="d-flex mb-2">
				<UserIcon {...props.author} />

				<div className="pl-2">
					<p className="m-0 text-truncate">
						<strong>{props.author.fullName}</strong>
					</p>

					<p className="m-0 text-secondary">
						{props.dateDescription}
					</p>
				</div>
			</div>
			<p
				className="text-secondary"
				dangerouslySetInnerHTML={{__html: props.body}}
			/>
			<Button
				displayType="link"
				loading={deletingComment}
				onClick={_handleDeleteButtonClick}
				small
				type="button"
			>
				{Liferay.Language.get('delete')}
			</Button>
		</article>
	);
};

FragmentComment.propTypes = {
	author: PropTypes.shape({
		fullName: PropTypes.string,
		portraitURL: PropTypes.string
	}),

	body: PropTypes.string,
	dateDescription: PropTypes.string
};

export {FragmentComment};
export default FragmentComment;

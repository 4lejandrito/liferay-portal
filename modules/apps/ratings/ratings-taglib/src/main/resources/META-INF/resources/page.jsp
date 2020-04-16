<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
Map<String, Object> data = (Map<String, Object>)request.getAttribute("liferay-ratings:ratings:data");
String type = GetterUtil.getString((String)request.getAttribute("liferay-ratings:ratings:type"));
%>

<liferay-util:html-top
	outputKey="com.liferay.ratings.taglib.servlet.taglib#/page.jsp"
>
	<link href="<%= PortalUtil.getStaticResourceURL(request, application.getContextPath() + "/css/main.css") %>" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div>
	<c:choose>
		<c:when test="<%= type.equals(RatingsType.LIKE.getValue()) %>">
			<clay:button
				disabled="<%= true %>"
				elementClasses="btn-outline-borderless btn-outline-secondary btn-sm"
				icon="heart"
			/>
		</c:when>
		<c:when test="<%= type.equals(RatingsType.THUMBS.getValue()) %>">
			<clay:button
				disabled="<%= true %>"
				elementClasses="btn-outline-borderless btn-outline-secondary btn-sm"
				icon="thumbs-up"
			/>

			<clay:button
				disabled="<%= true %>"
				elementClasses="btn-outline-borderless btn-outline-secondary btn-sm"
				icon="thumbs-down"
			/>
		</c:when>
		<c:when test="<%= type.equals(RatingsType.STACKED_STARS.getValue()) || type.equals(RatingsType.STARS.getValue()) %>">
			<div class="autofit-row autofit-row-center ratings ratings-stars">
				<div class="autofit-col">
					<div class="dropdown">
						<clay:button
							disabled="<%= true %>"
							elementClasses="btn btn-outline-borderless btn-outline-secondary btn-sm dropdown-toggle"
							icon="star-o"
							label="-"
						/>
					</div>
				</div>

				<div class="autofit-col">
					<clay:icon
						elementClasses="ratings-stars-average-icon"
						symbol="star"
					/>
				</div>
			</div>
		</c:when>
	</c:choose>

	<react:component
		data="<%= data %>"
		module="js/components/Ratings"
	/>
</div>
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
String backURL = ParamUtil.getString(request, "redirect");

DepotEntry depotEntry = (DepotEntry)request.getAttribute("depotEntry");

Group group = GroupServiceUtil.getGroup(depotEntry.getGroupId());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(HtmlUtil.escape(group.getDescriptiveName(locale)));
%>

<liferay-ui:success key='<%= DepotPortletKeys.DEPOT_ADMIN + "requestProcessed" %>' message="repository-was-added" />

<liferay-frontend:edit-form
	action="<%= DepotEntryURLUtil.getEditDepotEntryActionURL(liferayPortletResponse) %>"
	method="post"
	name="fm"
	onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveGroup();" %>'
>
	<aui:input name="redirect" type="hidden" value="<%= backURL %>" />
	<aui:input name="depotEntryId" type="hidden" value="<%= depotEntry.getDepotEntryId() %>" />

	<liferay-ui:error-marker
		key="<%= WebKeys.ERROR_SECTION %>"
		value="display-settings"
	/>

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset-group>
			<liferay-frontend:fieldset
				collapsible="false"
				label='<%= LanguageUtil.get(request, "details") %>'
			>
				<aui:model-context bean="<%= group %>" model="<%= Group.class %>" />

				<aui:input name="repositoryId" type="resource" value="<%= String.valueOf(depotEntry.getDepotEntryId()) %>" />

				<aui:input name="name" placeholder="name" required="<%= true %>" value="<%= String.valueOf(group.getName(locale)) %>" />

				<aui:input name="description" placeholder="description" />
			</liferay-frontend:fieldset>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<%@ include file="/languages.jspf" %>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button href="<%= backURL %>" type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<script>
	function <portlet:namespace />saveGroup(forceDisable) {
		<c:if test="<%= (group != null) && !group.isCompany() %>">
		<portlet:namespace />saveLocales();
		</c:if>

		submitForm(document.<portlet:namespace />fm);
	}
</script>
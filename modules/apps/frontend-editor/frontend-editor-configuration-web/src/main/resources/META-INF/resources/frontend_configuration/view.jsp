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
List<Portlet> portlets = (List<Portlet>)request.getAttribute("portlets");
List<String> editorNames = (List<String>)request.getAttribute("editorNames");
%>

<aui:form action="THE URL" cssClass="container-fluid container-fluid-max-xl container-form-lg" method="post" name="fm">
	<div class="sheet sheet-xl">
		<aui:row>
			<aui:col width="<%= 33 %>">
				<aui:select id="portletName" label="portlet-name" name="portletName" title="portlet-name">

					<%
					for (Portlet portlet : portlets) {
					%>

						<aui:option label="<%= portlet.getDisplayName() %>" value="<%= portlet.getPortletName() %>" />

					<%
					}
					%>

				</aui:select>
			</aui:col>

			<aui:col width="<%= 33 %>">
				<aui:select id="editorName" label="editor-name" name="editorName" title="editor-name">

					<%
					for (String editorName : editorNames) {
					%>

						<aui:option label="<%= editorName %>" value="<%= editorName %>" />

					<%
					}
					%>

				</aui:select>
			</aui:col>

			<aui:col width="<%= 33 %>">
				<aui:input label="editor-config-key" name="editorConfigKey" />
			</aui:col>
		</aui:row>

		<aui:input label="use-custom-configuration" name="useCustomConfiguration" type="checkbox" />

		<aui:row>
			<aui:col width="<%= 50 %>">
				<aui:input disabled="<%= true %>" label="default-configuration" name="defaultConfiguration" type="textarea" />
			</aui:col>

			<aui:col width="<%= 50 %>">
				<aui:input label="custom-configuration" name="customConfiguration" type="textarea" />
			</aui:col>
		</aui:row>
	</div>
</aui:form>

<liferay-portlet:resourceURL id="/editor_configuration/configuration" varImpl="editorConfigurationURL" />

<aui:script sandbox="<%= true %>" use="escape">
	var editorNameSelect = document.getElementById('<portlet:namespace/>editorName');
	var portletNameSelect = document.getElementById('<portlet:namespace/>portletName');
	var editorConfigKey = document.getElementById('<portlet:namespace/>editorConfigKey');
	var defaultConfiguration = document.getElementById('<portlet:namespace/>defaultConfiguration');

	editorNameSelect.addEventListener('change', fetchEditorConfiguration);
	portletNameSelect.addEventListener('change', fetchEditorConfiguration);
	editorConfigKey.addEventListener('change', fetchEditorConfiguration);

	function fetchEditorConfiguration() {
		$.ajax(
			'<%= editorConfigurationURL.toString() %>',
			{
				data: {
					<portlet:namespace />portletName: portletNameSelect.value,
					<portlet:namespace />editorName: editorNameSelect.value,
					<portlet:namespace />editorConfigKey: editorConfigKey.value
				},
				success: function(responseData) {
					defaultConfiguration.value = JSON.stringify(responseData, undefined, 2);
				}
			}
		);
	}

	fetchEditorConfiguration();
</aui:script>
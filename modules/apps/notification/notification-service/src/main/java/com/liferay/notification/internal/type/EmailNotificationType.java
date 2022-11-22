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

package com.liferay.notification.internal.type;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.constants.NotificationTermEvaluatorConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.exception.NotificationTemplateFromException;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationQueueEntryAttachment;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.model.NotificationTemplateAttachment;
import com.liferay.notification.service.NotificationQueueEntryAttachmentLocalService;
import com.liferay.notification.service.NotificationTemplateAttachmentLocalService;
import com.liferay.notification.type.BaseNotificationType;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.util.LocalizedMapUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ConcurrentHashMapBuilder;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.transformer.TemplateNodeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	immediate = true,
	property = "notification.type.key=" + NotificationConstants.TYPE_EMAIL,
	service = NotificationType.class
)
public class EmailNotificationType extends BaseNotificationType {

	@Override
	public List<NotificationRecipientSetting>
		createNotificationRecipientSettings(
			long notificationRecipientId, Object[] recipients, User user) {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		Map<String, Object> recipientsMap = (Map<String, Object>)recipients[0];

		for (Map.Entry<String, Object> entry : recipientsMap.entrySet()) {
			NotificationRecipientSetting notificationRecipientSetting =
				notificationRecipientSettingLocalService.
					createNotificationRecipientSetting(0L);

			notificationRecipientSetting.setCompanyId(user.getCompanyId());
			notificationRecipientSetting.setUserId(user.getUserId());
			notificationRecipientSetting.setUserName(user.getFullName());
			notificationRecipientSetting.setNotificationRecipientId(
				notificationRecipientId);
			notificationRecipientSetting.setName(entry.getKey());

			if (entry.getValue() instanceof String) {
				notificationRecipientSetting.setValue(
					String.valueOf(entry.getValue()));
			}
			else {
				notificationRecipientSetting.setValueMap(
					LocalizedMapUtil.getLocalizedMap(
						(LinkedHashMap)entry.getValue()));
			}

			notificationRecipientSettings.add(notificationRecipientSetting);
		}

		return notificationRecipientSettings;
	}

	@Override
	public String getFromName(NotificationQueueEntry notificationQueueEntry) {
		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.toMap(
				notificationRecipient.getNotificationRecipientSettings());

		return String.valueOf(notificationRecipientSettingsMap.get("fromName"));
	}

	@Override
	public String getRecipientSummary(
		NotificationQueueEntry notificationQueueEntry) {

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.toMap(
				notificationRecipient.getNotificationRecipientSettings());

		return String.valueOf(notificationRecipientSettingsMap.get("to"));
	}

	@Override
	public String getType() {
		return NotificationConstants.TYPE_EMAIL;
	}

	@Override
	public String getTypeLanguageKey() {
		return "email";
	}

	@Override
	public void sendNotification(NotificationContext notificationContext)
		throws PortalException {

		User user = userLocalService.getUser(notificationContext.getUserId());

		siteDefaultLocale = portal.getSiteDefaultLocale(user.getGroupId());
		userLocale = user.getLocale();

		notificationContext.setFileEntryIds(
			_getFileEntryIds(user.getCompanyId(), notificationContext));

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		String body = _formatBody(
			notificationTemplate.getBodyMap(), notificationContext);
		NotificationRecipient notificationRecipient =
			notificationTemplate.getNotificationRecipient();
		String subject = formatLocalizedContent(
			notificationTemplate.getSubjectMap(), notificationContext);

		Map<String, String> notificationRecipientSettingsEvaluatedMap =
			HashMapBuilder.put(
				"bcc",
				formatContent(
					"bcc", notificationContext,
					notificationRecipient.getNotificationRecipientId())
			).put(
				"cc",
				formatContent(
					"cc", notificationContext,
					notificationRecipient.getNotificationRecipientId())
			).put(
				"from",
				formatContent(
					"from", notificationContext,
					notificationRecipient.getNotificationRecipientId())
			).put(
				"fromName",
				() -> {
					NotificationRecipientSetting notificationRecipientSetting =
						notificationRecipientSettingLocalService.
							getNotificationRecipientSetting(
								notificationRecipient.
									getNotificationRecipientId(),
								"fromName");

					return formatLocalizedContent(
						notificationRecipientSetting.getValueMap(),
						notificationContext);
				}
			).put(
				"to",
				() -> {
					NotificationRecipientSetting notificationRecipientSetting =
						notificationRecipientSettingLocalService.
							getNotificationRecipientSetting(
								notificationRecipient.
									getNotificationRecipientId(),
								"to");

					String to = _formatTo(
						notificationRecipientSetting.getValue(user.getLocale()),
						notificationContext);

					if (Validator.isNotNull(to)) {
						return to;
					}

					return formatLocalizedContent(
						notificationRecipientSetting.getValue(
							siteDefaultLocale),
						NotificationTermEvaluatorConstants.RECIPIENT,
						notificationContext);
				}
			).build();

		for (String emailAddressOrUserId :
				StringUtil.split(
					notificationRecipientSettingsEvaluatedMap.get("to"))) {

			User toUser = userLocalService.fetchUser(
				GetterUtil.getLong(emailAddressOrUserId));

			EmailAddressValidator emailAddressValidator =
				EmailAddressValidatorFactory.getInstance();

			if ((toUser == null) &&
				emailAddressValidator.validate(
					user.getCompanyId(), emailAddressOrUserId)) {

				toUser = userLocalService.fetchUserByEmailAddress(
					user.getCompanyId(), emailAddressOrUserId);

				if (toUser == null) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"No user exists with email address " +
								emailAddressOrUserId);
					}

					prepareNotificationContext(
						userLocalService.getDefaultUser(
							CompanyThreadLocal.getCompanyId()),
						body, notificationContext,
						notificationRecipientSettingsEvaluatedMap, subject);

					notificationQueueEntryLocalService.
						addNotificationQueueEntry(notificationContext);

					continue;
				}
			}

			prepareNotificationContext(
				user, body, notificationContext,
				notificationRecipientSettingsEvaluatedMap, subject);

			notificationQueueEntryLocalService.addNotificationQueueEntry(
				notificationContext);
		}
	}

	@Override
	public void sendUnsentNotifications() {
		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntryLocalService.getUnsentNotificationEntries(
					NotificationConstants.TYPE_EMAIL)) {

			NotificationRecipient notificationRecipient =
				notificationQueueEntry.getNotificationRecipient();

			Map<String, Object> notificationRecipientSettingsMap =
				NotificationRecipientSettingUtil.toMap(
					notificationRecipient.getNotificationRecipientSettings());

			try {
				MailMessage mailMessage = new MailMessage(
					new InternetAddress(
						String.valueOf(
							notificationRecipientSettingsMap.get("from")),
						String.valueOf(
							notificationRecipientSettingsMap.get("fromName"))),
					new InternetAddress(
						String.valueOf(
							notificationRecipientSettingsMap.get("to")),
						String.valueOf(
							notificationRecipientSettingsMap.get("toName"))),
					notificationQueueEntry.getSubject(),
					notificationQueueEntry.getBody(), true);

				_addFileAttachments(
					mailMessage,
					notificationQueueEntry.getNotificationQueueEntryId());

				mailMessage.setBCC(
					_toInternetAddresses(
						String.valueOf(
							notificationRecipientSettingsMap.get("bcc"))));
				mailMessage.setCC(
					_toInternetAddresses(
						String.valueOf(
							notificationRecipientSettingsMap.get("cc"))));

				_mailService.sendEmail(mailMessage);

				notificationQueueEntryLocalService.updateStatus(
					notificationQueueEntry.getNotificationQueueEntryId(),
					NotificationQueueEntryConstants.STATUS_SENT);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				notificationQueueEntry.setStatus(
					NotificationQueueEntryConstants.STATUS_FAILED);

				notificationQueueEntryLocalService.updateNotificationQueueEntry(
					notificationQueueEntry);
			}
		}
	}

	@Override
	public Object[] toRecipients(
		List<NotificationRecipientSetting> notificationRecipientSettings) {

		return new Object[] {
			NotificationRecipientSettingUtil.toMap(
				notificationRecipientSettings)
		};
	}

	@Override
	public void validateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		super.validateNotificationTemplate(notificationContext);

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.toMap(
				notificationContext.getNotificationRecipientSettings());

		if (Validator.isNull(notificationRecipientSettingsMap.get("from"))) {
			throw new NotificationTemplateFromException("From is null");
		}
	}

	private void _addFileAttachments(
		MailMessage mailMessage, long notificationQueueEntryId) {

		for (NotificationQueueEntryAttachment notificationQueueEntryAttachment :
				_notificationQueueEntryAttachmentLocalService.
					getNotificationQueueEntryNotificationQueueEntryAttachments(
						notificationQueueEntryId)) {

			try {
				FileEntry fileEntry =
					_portletFileRepository.getPortletFileEntry(
						notificationQueueEntryAttachment.getFileEntryId());

				mailMessage.addFileAttachment(
					FileUtil.createTempFile(fileEntry.getContentStream()),
					fileEntry.getFileName());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
	}

	private String _formatBody(
			Map<Locale, String> bodyMap,
			NotificationContext notificationContext)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		if (Objects.equals(
				NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
				notificationTemplate.getEditorType())) {

			return formatLocalizedContent(bodyMap, notificationContext);
		}

		if (!GetterUtil.getBoolean(PropsUtil.get("feature.flag.LPS-162598"))) {
			return StringPool.BLANK;
		}

		String body = notificationTemplate.getBody(userLocale);

		if (Validator.isNull(body)) {
			body = notificationTemplate.getBody(siteDefaultLocale);
		}

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			new StringTemplateResource(
				NotificationTemplate.class.getName() + StringPool.POUND +
					notificationTemplate.getNotificationTemplateId(),
				body),
			true);

		_setRestClient(template, notificationContext);

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				notificationContext.getClassName());

		PersistedModelLocalService persistedModelLocalService =
			_persistedModelLocalServiceRegistry.getPersistedModelLocalService(
				notificationContext.getClassName());

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(
				persistedModelLocalService.getPersistedModel(
					notificationContext.getClassPK()));

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField<?> infoField = infoFieldValue.getInfoField();

			if (StringUtil.startsWith(
					infoField.getName(),
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

				continue;
			}

			TemplateNode templateNode = _templateNodeFactory.createTemplateNode(
				infoFieldValue, new ThemeDisplay());

			template.put(infoField.getName(), templateNode);
			template.put(infoField.getUniqueId(), templateNode);
		}

		StringWriter stringWriter = new StringWriter();

		template.processTemplate(stringWriter);

		return stringWriter.toString();
	}

	private String _formatTo(String to, NotificationContext notificationContext)
		throws PortalException {

		if (Validator.isNull(to)) {
			return StringPool.BLANK;
		}

		Set<String> emailAddresses = new HashSet<>();

		Matcher matcher = _emailAddressPattern.matcher(to);

		while (matcher.find()) {
			emailAddresses.add(matcher.group());
		}

		return formatLocalizedContent(
			StringUtil.merge(emailAddresses),
			NotificationTermEvaluatorConstants.RECIPIENT, notificationContext);
	}

	private List<Long> _getFileEntryIds(
			long companyId, NotificationContext notificationContext)
		throws PortalException {

		Group group = _groupLocalService.getCompanyGroup(companyId);

		Repository repository = _getRepository(group.getGroupId());

		if (repository == null) {
			return new ArrayList<>();
		}

		List<Long> fileEntryIds = new ArrayList<>();

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		for (NotificationTemplateAttachment notificationTemplateAttachment :
				_notificationTemplateAttachmentLocalService.
					getNotificationTemplateAttachments(
						notificationTemplate.getNotificationTemplateId())) {

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				notificationTemplateAttachment.getObjectFieldId());

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				MapUtil.getLong(
					notificationContext.getTermValues(),
					objectField.getName()));

			if (dlFileEntry == null) {
				continue;
			}

			FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
				null, repository.getGroupId(),
				userLocalService.getDefaultUserId(companyId),
				NotificationTemplate.class.getName(), 0,
				NotificationPortletKeys.NOTIFICATION_TEMPLATES,
				repository.getDlFolderId(), dlFileEntry.getContentStream(),
				_portletFileRepository.getUniqueFileName(
					group.getGroupId(), repository.getDlFolderId(),
					dlFileEntry.getFileName()),
				dlFileEntry.getMimeType(), false);

			fileEntryIds.add(fileEntry.getFileEntryId());
		}

		return fileEntryIds;
	}

	private Repository _getRepository(long groupId) {
		Repository repository = _portletFileRepository.fetchPortletRepository(
			groupId, NotificationPortletKeys.NOTIFICATION_TEMPLATES);

		if (repository != null) {
			return repository;
		}

		try {
			return _portletFileRepository.addPortletRepository(
				groupId, NotificationPortletKeys.NOTIFICATION_TEMPLATES,
				new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private void _setRestClient(Template template, NotificationContext notificationContext)
		throws PortalException {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		List<ServiceContext> serviceContexts = new ArrayList<>();

		while (httpServletRequest == null) {
			serviceContext = ServiceContextThreadLocal.popServiceContext();

			serviceContexts.add(serviceContext);

			httpServletRequest = serviceContext.getRequest();
		}

		for (ServiceContext serviceContextItem : serviceContexts) {
			ServiceContextThreadLocal.pushServiceContext(serviceContextItem);
		}

		if (httpServletRequest == null) {
			throw new NullPointerException("HttpServletRequest is null");
		}

//		ThemeDisplay themeDisplay = new ThemeDisplay();
//
//		themeDisplay.setResponse(new DummyHttpServletResponse());
//
//		template.put("themeDisplay", themeDisplay);
//
//		_templateContextContributor.prepare(template, httpServletRequest);
//
//		template.remove("themeDisplay");


		HttpServletRequest mockHttpServletRequest =
			_getHttpServletRequest(new DummyHttpServletResponse(),
				notificationContext);


		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		template.put("themeDisplay", themeDisplay);

		_templateContextContributor.prepare(template, mockHttpServletRequest);

		template.remove("themeDisplay");
	}

	private HttpServletRequest _getHttpServletRequest(
		HttpServletResponse httpServletResponse, NotificationContext notificationContext)
		throws PortalException {

//		HttpServletRequest httpServletRequest =
//			DynamicServletRequest.addQueryString(
//				new MockHttpServletRequest(), "p_l_id=" + "0",
//				false);

		User user = userLocalService.getUser(notificationContext.getUserId());

		siteDefaultLocale = portal.getSiteDefaultLocale(user.getGroupId());
		userLocale = user.getLocale();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest(user);

		httpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, Long.valueOf(user.getCompanyId()));

//		ThemeDisplay themeDisplay = _getThemeDisplay(user);
		ThemeDisplay themeDisplay =new ThemeDisplay();

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(userLocale));
		themeDisplay.setLocale(userLocale);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(httpServletResponse);

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		httpServletRequest.setAttribute(WebKeys.USER, user);
		httpServletRequest.setAttribute(WebKeys.USER_ID, user.getUserId());

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws PortalException {

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		Company company = _companyLocalService.getCompany(
			user.getCompanyId());

		themeDisplay.setCompany(company);

//		themeDisplay.setLayoutTypePortlet(
//			(LayoutTypePortlet)_layout.getLayoutType());
		//themeDisplay.setPermissionChecker(PermissionCheckerFactoryUtil.create(user));
		//themeDisplay.setPlid(0);
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setPortalURL(
			company.getPortalURL(user.getGroupId()));
		themeDisplay.setRealUser(user);
		themeDisplay.setScopeGroupId(user.getGroupId());
		themeDisplay.setServerPort(
			_portal.getPortalServerPort(_isHttpsEnabled()));
		themeDisplay.setSiteGroupId(user.getGroupId());
		themeDisplay.setTimeZone(user.getTimeZone());
		themeDisplay.setUser(user);

		return themeDisplay;
	}


	private boolean _isHttpsEnabled() {
		return Objects.equals(
			Http.HTTPS,
			PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
			   Objects.equals(
				   Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL));
	}


	private class MockHttpServletRequest implements HttpServletRequest {

		private final User _user;

		public MockHttpServletRequest(User user){

			_user = user;
		}

		@Override
		public boolean authenticate(HttpServletResponse httpServletResponse)
			throws IOException, ServletException {

			return false;
		}

		@Override
		public String changeSessionId() {
			return null;
		}

		@Override
		public AsyncContext getAsyncContext() {
			return null;
		}

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(_attributes.keySet());
		}

		@Override
		public String getAuthType() {
			return null;
		}

		@Override
		public String getCharacterEncoding() {
			return null;
		}

		@Override
		public int getContentLength() {
			return 0;
		}

		@Override
		public long getContentLengthLong() {
			return 0;
		}

		@Override
		public String getContentType() {
			return null;
		}

		@Override
		public String getContextPath() {
			return null;
		}

		@Override
		public Cookie[] getCookies() {
			return new Cookie[0];
		}

		@Override
		public long getDateHeader(String name) {
			return 0;
		}

		@Override
		public DispatcherType getDispatcherType() {
			return null;
		}

		@Override
		public String getHeader(String name) {
			return null;
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			return Collections.emptyEnumeration();
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			return null;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return null;
		}

		@Override
		public int getIntHeader(String name) {
			return 0;
		}

		@Override
		public String getLocalAddr() {
			return null;
		}

		@Override
		public Locale getLocale() {
			return _user.getLocale();
		}

		@Override
		public Enumeration<Locale> getLocales() {
			return null;
		}

		@Override
		public String getLocalName() {
			return null;
		}

		@Override
		public int getLocalPort() {
			return 0;
		}

		@Override
		public String getMethod() {
			return HttpMethods.GET;
		}

		@Override
		public String getParameter(String name) {
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return Collections.emptyMap();
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return null;
		}

		@Override
		public String[] getParameterValues(String name) {
			return new String[0];
		}

		@Override
		public Part getPart(String name) throws IOException, ServletException {
			return null;
		}

		@Override
		public Collection<Part> getParts()
			throws IOException, ServletException {

			return null;
		}

		@Override
		public String getPathInfo() {
			return null;
		}

		@Override
		public String getPathTranslated() {
			return null;
		}

		@Override
		public String getProtocol() {
			return null;
		}

		@Override
		public String getQueryString() {
			return null;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return null;
		}

		@Override
		public String getRealPath(String path) {
			return null;
		}

		@Override
		public String getRemoteAddr() {
			return null;
		}

		@Override
		public String getRemoteHost() {
			return null;
		}

		@Override
		public int getRemotePort() {
			return 0;
		}

		@Override
		public String getRemoteUser() {
			return _user.getScreenName();
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			return DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				ServletContextPool.get(StringPool.BLANK), path);
		}

		@Override
		public String getRequestedSessionId() {
			return null;
		}

		@Override
		public String getRequestURI() {
			return StringPool.BLANK;
		}

		@Override
		public StringBuffer getRequestURL() {
			return null;
		}

		@Override
		public String getScheme() {
			return null;
		}

		@Override
		public String getServerName() {
			return null;
		}

		@Override
		public int getServerPort() {
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			return ServletContextPool.get(StringPool.BLANK);
		}

		@Override
		public String getServletPath() {
			return null;
		}

		@Override
		public HttpSession getSession() {
			return _httpSession;
		}

		@Override
		public HttpSession getSession(boolean create) {
			return _httpSession;
		}

		@Override
		public Principal getUserPrincipal() {
			return new ProtectedPrincipal(_user.getScreenName());
		}

		@Override
		public boolean isAsyncStarted() {
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromURL() {
			return false;
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			return false;
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public boolean isUserInRole(String role) {
			return true;
		}

		@Override
		public void login(String userName, String password)
			throws ServletException {
		}

		@Override
		public void logout() throws ServletException {
		}

		@Override
		public void removeAttribute(String name) {
			_attributes.remove(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			if ((name != null) && (value != null)) {
				_attributes.put(name, value);
			}
		}

		@Override
		public void setCharacterEncoding(String encoding)
			throws UnsupportedEncodingException {
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			return null;
		}

		@Override
		public AsyncContext startAsync(
			ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {

			return null;
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
			throws IOException, ServletException {

			return null;
		}

		private final Map<String, Object> _attributes =
			ConcurrentHashMapBuilder.<String, Object>put(
				WebKeys.CTX, ServletContextPool.get(StringPool.BLANK)
			).build();

		private final HttpSession _httpSession = new HttpSession() {

			@Override
			public Object getAttribute(String name) {
				return _attributes.get(name);
			}

			@Override
			public Enumeration<String> getAttributeNames() {
				return Collections.enumeration(_attributes.keySet());
			}

			@Override
			public long getCreationTime() {
				return 0;
			}

			@Override
			public String getId() {
				return StringPool.BLANK;
			}

			@Override
			public long getLastAccessedTime() {
				return 0;
			}

			@Override
			public int getMaxInactiveInterval() {
				return 0;
			}

			@Override
			public ServletContext getServletContext() {
				return null;
			}

			@Override
			public HttpSessionContext getSessionContext() {
				return null;
			}

			@Override
			public Object getValue(String name) {
				return null;
			}

			@Override
			public String[] getValueNames() {
				return new String[0];
			}

			@Override
			public void invalidate() {
			}

			@Override
			public boolean isNew() {
				return true;
			}

			@Override
			public void putValue(String name, Object value) {
			}

			@Override
			public void removeAttribute(String name) {
			}

			@Override
			public void removeValue(String name) {
			}

			@Override
			public void setAttribute(String name, Object value) {
				_attributes.put(name, value);
			}

			@Override
			public void setMaxInactiveInterval(int interval) {
			}

		};

	}

	private InternetAddress[] _toInternetAddresses(String string)
		throws Exception {

		List<InternetAddress> internetAddresses = new ArrayList<>();

		for (String internetAddressString : StringUtil.split(string)) {
			internetAddresses.add(new InternetAddress(internetAddressString));
		}

		return internetAddresses.toArray(new InternetAddress[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmailNotificationType.class);

	private static final Pattern _emailAddressPattern = Pattern.compile(
		"[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@" +
			"(?:\\w(?:[\\w-]*\\w)?\\.)+(\\w(?:[\\w-]*\\w))");

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private MailService _mailService;

	@Reference
	private NotificationQueueEntryAttachmentLocalService
		_notificationQueueEntryAttachmentLocalService;

	@Reference
	private NotificationTemplateAttachmentLocalService
		_notificationTemplateAttachmentLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PersistedModelLocalServiceRegistry
		_persistedModelLocalServiceRegistry;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference(target = "(name=RESTClient)")
	private TemplateContextContributor _templateContextContributor;

	@Reference
	private TemplateNodeFactory _templateNodeFactory;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Portal _portal;

}
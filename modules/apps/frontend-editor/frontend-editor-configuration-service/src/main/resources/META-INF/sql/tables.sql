create table EditorConfigurationEntry (
	uuid_ VARCHAR(75) null,
	editorConfigurationEntryId LONG not null primary key,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	portletName VARCHAR(75) null,
	editorName VARCHAR(75) null,
	editorConfigKey VARCHAR(75) null,
	configuration VARCHAR(75) null,
	enabled BOOLEAN
);
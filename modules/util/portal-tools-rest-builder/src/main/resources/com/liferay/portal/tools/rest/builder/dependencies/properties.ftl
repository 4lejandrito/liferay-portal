#
# This is a generated file.
#

api.version=${openAPIYAML.info.version}
<#assign
	javaDataType = freeMarkerTool.getJavaDataType(configYAML, openAPIYAML, schemaName)!""
	javaMethodSignatures = freeMarkerTool.getResourceJavaMethodSignatures(configYAML, openAPIYAML, schemaName)
	generateBatch = freeMarkerTool.generateBatch(configYAML, javaDataType, javaMethodSignatures, schemaName)
	generateCRUD = freeMarkerTool.generateCRUD(configYAML, javaMethodSignatures, schemaName)
	openAPISchema = stringUtil.equals(schemaName, "openapi")
/>
<#if !openAPISchema && generateBatch>
batch.engine.entity.class.name=${javaDataType}
batch.engine.task.item.delegate=true
batch.planner.export.enabled=${freeMarkerTool.hasReadVulcanBatchImplementation(configYAML, javaMethodSignatures)?c}
batch.planner.import.enabled=${freeMarkerTool.getVulcanBatchImplementationCreateStrategies(javaMethodSignatures, freeMarkerTool.getDTOProperties(configYAML, openAPIYAML, schema, allSchemas))?has_content?c}
</#if>
<#if generateCRUD>
crud.entity.class.name=${javaDataType}
crud.item.delegate=true
</#if>
<#if javaDataType?has_content>
entity.class.name=${javaDataType}
</#if>
<#if openAPIYAML.info.featureFlag??>
feature.flag.key=${openAPIYAML.info.featureFlag}
</#if>
<#if openAPISchema>
openapi.resource=true
<#if configYAML.application??>
openapi.resource.path=${configYAML.application.baseURI}
</#if>
</#if>
<#if configYAML.resourceApplicationSelect??>
osgi.jaxrs.application.select=${configYAML.resourceApplicationSelect}
<#elseif configYAML.application??>
osgi.jaxrs.application.select=(osgi.jaxrs.name=${configYAML.application.name})
</#if>
osgi.jaxrs.resource=true
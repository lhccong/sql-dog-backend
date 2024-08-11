<#-- 单表 -->
@startuml
entity "${tableName}" {
<#list fieldList as field>
    + ${field.fieldName} : ${field.fieldType} ${field.fieldComment}
</#list>
}
<#if tableComment?has_content>
    note right of "${tableName}"
    ${tableComment}
    end note
</#if>
@enduml
<#-- Java 对象模板 -->
//创建 ${className} 对象
${className} ${objectName} = new ${className}();
<#-- 循环生成字段 ---------->
<#list fieldList as field>
${objectName}.${field.setMethod}(${field.value});
</#list>

//通过 Accessor 方式设置 ${className} 对象
<#-- 循环生成字段 ---------->
${className} ${objectName} = new ${className}()<#list fieldList as field>.${field.setMethod}(${field.value})</#list>;

//通过 Builder 方式设置 ${className} 对象
<#-- 循环生成字段 ----------><#-- 取得字段名并将首字母小写 -->
${className} ${objectName} = ${className}.builder()<#list fieldList as field><#assign fieldName = field.setMethod?substring(3)?uncap_first>.${fieldName}(${field.value})</#list>.build();



<#if score??>
    <#if sqlId??>
            SQL 语句 ID: ${sqlId}
            ------------------------------------------------------------
    </#if>
    <#if sourceSql??>
            您分析的 SQL 语句为: ${sourceSql}
            ------------------------------------------------------------
    </#if>
            SQL 分析结果的分数: ${score}
    <#if needWarn??>
        <#if needWarn>
            ⚠️警告: 分数低于预期值，请判断是否需要修改。
        <#else>
            ✅ 分析正常: 分数符合预期。
        </#if>
    </#if>

    <#if analysisResults??>
        <#list analysisResults as item>
            ------------------------------------------------------------
            🚨 命中规则 (${item_index + 1})
            规则命中原因   : ${item.reason}
            修改建议       : ${item.suggestion}
            分数调整       : <#if item.scoreDeduction < 0>🔼 加分: +<#else>🔽 扣分: </#if>${-item.scoreDeduction}
            ------------------------------------------------------------
        </#list>
    <#else>
        📝 没有命中规则
    </#if>
</#if>

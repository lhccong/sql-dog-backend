package com.cong.sqldog.core.sqlanalyze.builder;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreResult;
import com.cong.sqldog.core.sqlgenerate.builder.PlantUmlBuilder;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

/**
 * SQL 分析结果构建器
 *
 * @author 香香
 * @Date 2024-08-13 15:43
 */
@Component
@Slf4j
public class SqlAnalysisBuilder {

    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        SqlAnalysisBuilder.configuration = configuration;
    }

    /**
     * 构造 SQL 分析结果
     *
     * @param sqlScoreResult SQL 评分结果
     * @return SQL 分析结果
     */
    public static String buildSQLAnalysisResult(SqlScoreResult sqlScoreResult) {
        // 用于生成 sqlAnalysis 的模板处理代码（慢镜分析结果）
        StringWriter stringWriter = new StringWriter();
        try {
            Template temp = configuration.getTemplate("sqlAnalysis.ftl");
            temp.process(sqlScoreResult, stringWriter);
        } catch (Exception e) {
            // 处理模板处理异常
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}

package com.cong.sqldog.service.impl;

import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResult;
import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResultList;
import com.cong.sqldog.core.sqlanalyze.builder.SqlAnalysisBuilder;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreResult;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreService;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreServiceRulesEngine;
import com.cong.sqldog.model.dto.sql.SqlAnalysisRequest;
import com.cong.sqldog.model.vo.sql.SqlAnalysisVO;
import com.cong.sqldog.service.SqlService;
import com.cong.sqldog.utils.GsonUtil;
import com.cong.sqldog.utils.SQLiteToMySqlPlanConverter;
import freemarker.template.Configuration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @Author 香香
 * @Date 2024-08-12 22:30
 **/
@Service
@Slf4j
public class SqlServiceImpl implements SqlService {

    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        SqlServiceImpl.configuration = configuration;
    }

    /**
     * SQL 分数服务
     */
    private static final SqlScoreService sqlScoreService = new SqlScoreServiceRulesEngine();

    /**
     * 获取 SQL 评分
     *
     * @param sqlAnalysisRequest SQL 分析请求
     * @return BaseResponse<SqlScoreResult>
     */
    @Override
    public SqlAnalysisVO getSqlScore(SqlAnalysisRequest sqlAnalysisRequest) {
        // 实例化返回结果
        SqlAnalysisVO sqlAnalysisVO = new SqlAnalysisVO();

        // 获取 SQL 分析结果
        SqlAnalysisResult sqlAnalysisResult = SQLiteToMySqlPlanConverter.convertSqliteToMySqlPlan(sqlAnalysisRequest.getDetail());

        // 创建包含一个分析结果的列表
        SqlAnalysisResultList resultList = new SqlAnalysisResultList();
        // 将单个结果放入列表中
        resultList.setResultList(Collections.singletonList(sqlAnalysisResult));
        // 将 SQL 语句放入结果列表中
        resultList.setSql(sqlAnalysisRequest.getSql());

        // 执行评分
        Optional<SqlScoreResult> optionalScoreResult = Optional.ofNullable(sqlScoreService.score(resultList));
        SqlScoreResult sqlScoreResult = optionalScoreResult.orElseThrow(() -> {
            log.error("SQL 分析评分异常: {}", GsonUtil.bean2Json(resultList));
            return new IllegalStateException("SQL评分结果不可用");
        });

        // 设置源 SQL
        sqlScoreResult.setSourceSql(sqlAnalysisRequest.getSql());

        // 生成 SQL 慢镜评分分析结果
        String sqlAnalysisResultBySlowMirror = SqlAnalysisBuilder.buildSQLAnalysisResult(sqlScoreResult);

        // 设置属性结果
        sqlAnalysisVO.setSqlAnalysisBySlowMirror(sqlAnalysisResultBySlowMirror);
        sqlAnalysisVO.setSqlAnalysisByAIGC("暂无 AI 分析结果...");

        return sqlAnalysisVO;
    }

}

package com.cong.sqldog.service;

import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResult;
import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResultList;
import com.cong.sqldog.core.sqlanalyze.builder.SqlAnalysisBuilder;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreResult;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreService;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreServiceRulesEngine;
import com.cong.sqldog.utils.GsonUtil;
import com.cong.sqldog.utils.SQLiteToMySqlPlanConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

/**
 * @Author 香香
 * @Date 2024-08-13 15:52
 **/
class SqlServiceTest extends TestBase {

    /**
     * 评分规则服务
     */
    private static final SqlScoreService sqlScoreService = new SqlScoreServiceRulesEngine();

    private static final Logger log = LoggerFactory.getLogger(SqlServiceTest.class);


    @Test
    void getSqlScore_ShouldReturnSqlAnalysisVO() {
        // 测试数据集
        String[] sqlStatements = {
                "SELECT * FROM user WHERE id = 1",
                // 题目：冒险者和金币
                "SELECT student_id, student_name, subject_id, subject_name, score, RANK() OVER (PARTITION BY subject_id ORDER BY score DESC) AS score_rank FROM magic_scores;",
                // 题目：大浪淘鸡
                "SELECT adventurer_id, adventurer_name, SUM(reward_coins) AS total_reward_coins FROM rewards GROUP BY adventurer_id, adventurer_name ORDER BY total_reward_coins DESC LIMIT 3;;",
        };

        String[] details = {
                "SCAN user",
                "USE TEMP B-TREE FOR ORDER BY",
                "USE TEMP B-TREE FOR GROUP BY",
        };

        for (int i = 0; i < sqlStatements.length; i++) {
            String sql = sqlStatements[i];
            String detail = details[i];

            // 获取 SQL 分析结果
            SqlAnalysisResult sqlAnalysisResult = SQLiteToMySqlPlanConverter.convertSqliteToMySqlPlan(detail);

            // 创建包含一个分析结果的列表
            SqlAnalysisResultList resultList = new SqlAnalysisResultList();
            // 将单个结果放入列表中
            resultList.setResultList(Collections.singletonList(sqlAnalysisResult));
            // 将 SQL 语句放入结果列表中
            resultList.setSql(sql);

            // 执行评分
            Optional<SqlScoreResult> optional = Optional.ofNullable(sqlScoreService.score(resultList));
            SqlScoreResult sqlScoreResult = optional.orElseThrow(() -> {
                log.error("SQL 分析评分异常 {},{}", GsonUtil.bean2Json(resultList), resultList.getSql());
                return new IllegalStateException("field is not present");
            });

            // 生成 SQL 慢镜评分分析结果
            String sqlAnalysisResultBySlowMirror = SqlAnalysisBuilder.buildSQLAnalysisResult(sqlScoreResult);
            log.info("SQL 慢镜评分分析结果\n" + sqlAnalysisResultBySlowMirror);
            Assertions.assertNotNull(sqlScoreResult);
        }
    }

}

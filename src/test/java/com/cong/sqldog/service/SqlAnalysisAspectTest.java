package com.cong.sqldog.service;

import cn.hutool.json.JSONUtil;
import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.out.MySqlScoreResultOutService;
import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResultList;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreResult;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreService;
import com.cong.sqldog.core.sqlanalyze.score.SqlScoreServiceRulesEngine;
import com.cong.sqldog.utils.GsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class SqlAnalysisAspectTest extends TestBase {

    private static final Logger log = LoggerFactory.getLogger(SqlAnalysisAspectTest.class);
    /**
     * 评分规则服务
     */
    private static final SqlScoreService sqlScoreService = new SqlScoreServiceRulesEngine();
    private static final MySqlScoreResultOutService mySqlScoreResultOutService = new MySqlScoreResultOutService();

    @Test
    void analysisTest() {
        String mockResultListStr = "{\"sql\":\"SELECT * FROM user WHERE id = 1\",\"resultList\":[{\"id\":1,\"selectType\":\"SIMPLE\",\"table\":\"user\",\"type\":\"ALL\",\"rows\":\"1\",\"filtered\":100,\"extra\":\"Using where\"}]}";
        SqlAnalysisResultList resultList = JSONUtil.toBean(mockResultListStr, SqlAnalysisResultList.class);
        Optional<SqlScoreResult> optional = Optional.ofNullable(sqlScoreService.score(resultList));
        SqlScoreResult sqlScoreResult = optional.orElseThrow(() -> {
            log.error("SQL 分析评分异常 {},{}", GsonUtil.bean2Json(resultList), resultList.getSql());
            return new IllegalStateException("field is not present");
        });
        // 输出评分结果(返回前端+持久化)以下是借鉴模板
        sqlScoreResult.setSqlId(resultList.getSql());
        mySqlScoreResultOutService.outResult(sqlScoreResult);

        Assertions.assertNotNull(sqlScoreResult);

    }
}

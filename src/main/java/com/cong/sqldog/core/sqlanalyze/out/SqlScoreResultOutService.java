package com.cong.sqldog.core.sqlanalyze.out;


import com.cong.sqldog.core.sqlanalyze.score.SqlScoreResult;

/**
 * SQL 分数结果输出服务
 *
 * @author cong
 * @date 2024/04/16
 */
public interface SqlScoreResultOutService {

    /**
     * 输出结果
     *
     * @param sqlScoreResult SQL 分数结果
     */
    void outResult(SqlScoreResult sqlScoreResult);
}

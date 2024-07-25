package com.cong.sqldog.sqlanalyze.score;


import com.cong.sqldog.sqlanalyze.analysis.SqlAnalysisResultList;

/**
 * SQL 分数服务
 *
 * @author cong
 * @date 2024/04/16
 */
public interface SqlScoreService {

    SqlScoreResult score(SqlAnalysisResultList sqlAnalysisResultList);
}

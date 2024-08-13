package com.cong.sqldog.service;

import com.cong.sqldog.model.dto.sqlAnalysis.SqlAnalysisRequest;
import com.cong.sqldog.model.vo.sql.SqlAnalysisVO;

/**
 * @Author 香香
 * @Date 2024-08-12 22:30
 **/
public interface SqlService {

    /**
     * 获取 SQL 评分
     * @param sqlAnalysisRequest SQL 分析请求
     * @return BaseResponse<SqlScoreResult>
     */
    SqlAnalysisVO getSqlScore(SqlAnalysisRequest sqlAnalysisRequest);

}

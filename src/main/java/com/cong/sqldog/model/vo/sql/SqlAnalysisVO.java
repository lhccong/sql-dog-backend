package com.cong.sqldog.model.vo.sql;

import lombok.Data;

/**
 * SQL 分析评分结果
 *
 * @Author 香香
 * @Date 2024-08-13 15:08
 **/
@Data
public class SqlAnalysisVO {

    /**
     * AI 分析结果
     */
    private String SqlAnalysisByAIGC;

    /**
     * SQL 慢镜分析结果
     */
    private String SqlAnalysisBySlowMirror;

}

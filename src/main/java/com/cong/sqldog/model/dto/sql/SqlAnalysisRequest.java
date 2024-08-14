package com.cong.sqldog.model.dto.sql;

import lombok.Data;

/**
 * SQL 分析请求
 *
 * @Author 香香
 * @Date 2024-08-12 21:07
 **/
@Data
public class SqlAnalysisRequest {

    /**
     * sql
     */
    private String sql;

    /**
     * 执行计划描述
     */
    private String detail;

}

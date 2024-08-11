package com.cong.sqldog.core.sqlanalyze.analysis;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * SQL 分析结果列表
 *
 * @author cong
 * @date 2024/04/15
 */
@Setter
@Getter
public class SqlAnalysisResultList {

    /**
     * SQL格式
     */
    private String sql;

    /**
     * 分析结果集合
     */
    private List<SqlAnalysisResult> resultList;

}

package com.cong.sqldog.infrastructure.utils;

import com.cong.sqldog.core.sqlanalyze.analysis.SqlAnalysisResult;

/**
 * SQLite到我SQL计划转换器
 *
 * @author cong
 * @date 2024/08/04
 */
public class SQLiteToMySqlPlanConverter {

    public static SqlAnalysisResult convertSqliteToMySqlPlan(String detail) {
        SqlAnalysisResult sqlAnalysisResult = new SqlAnalysisResult();

            // 解析 detail 字段
            String table = "";
            String type = "";
            String key = "";
            String extra = "";
            
            if (detail.contains("SCAN")) {
                table = detail.split(" ")[1];
                // 全表扫描
                type = "ALL";
                extra = "Using where";
            } else if (detail.contains("SEARCH")) {
                String[] parts = detail.split(" ");
                table = parts[1];
                key = parts[4];
                // 使用索引
                type = "ref";
                extra = "Using index condition";
                
                if (detail.contains("PRIMARY KEY")) {
                    key = "PRIMARY";
                }
            }
            
            // 填充 MySQL 执行计划字段
            sqlAnalysisResult.setExtra(extra);
            sqlAnalysisResult.setTable(table);
            sqlAnalysisResult.setType(type);
            sqlAnalysisResult.setSelectType("SIMPLE");
            sqlAnalysisResult.setKey(key);
            sqlAnalysisResult.setPossibleKeys(key);
            sqlAnalysisResult.setExtra(extra);

//
//            planRow.put("key_len", ""); // 需要进一步解析
//            planRow.put("ref", ""); // 根据需要填充
//            planRow.put("rows", ""); // 需要进一步估计

        return sqlAnalysisResult;

        
    }
}

package com.cong.sqldog.core.sqlgenerate.builder.sql;

/**
 * MySQL 方言
 *
 */
public class MySQLDialect implements SQLDialect {

    /**
     * 封装字段名
     *
     * @param name 名字
     * @return {@link String }
     */
    @Override
    public String wrapFieldName(String name) {
        return String.format("`%s`", name);
    }

    /**
     * 解析字段名
     *
     * @param fieldName 字段名称
     * @return {@link String }
     */
    @Override
    public String parseFieldName(String fieldName) {
        if (fieldName.startsWith("`") && fieldName.endsWith("`")) {
            return fieldName.substring(1, fieldName.length() - 1);
        }
        return fieldName;
    }

    /**
     * 包装表名
     *
     * @param name 名字
     * @return {@link String }
     */
    @Override
    public String wrapTableName(String name) {
        return String.format("`%s`", name);
    }

    /**
     * 解析表名
     *
     * @param tableName 表名
     * @return {@link String }
     */
    @Override
    public String parseTableName(String tableName) {
        if (tableName.startsWith("`") && tableName.endsWith("`")) {
            return tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }
}

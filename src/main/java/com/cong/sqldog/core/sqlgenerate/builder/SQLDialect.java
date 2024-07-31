package com.cong.sqldog.core.sqlgenerate.builder;

/**
 * SQL 方言
 *
 * @author cong
 * @date 2024/07/31
 */
public interface SQLDialect {

    /**
     * 封装字段名
     *
     * @param name 名字
     * @return {@link String }
     */
    String wrapFieldName(String name);

    /**
     * 解析字段名
     *
     * @param fieldName 字段名称
     * @return {@link String }
     */
    String parseFieldName(String fieldName);

    /**
     * 封装表名
     *
     * @param name 名字
     * @return {@link String }
     */
    String wrapTableName(String name);

    /**
     * 解析表名
     *
     * @param tableName 表名
     * @return {@link String }
     */
    String parseTableName(String tableName);
}

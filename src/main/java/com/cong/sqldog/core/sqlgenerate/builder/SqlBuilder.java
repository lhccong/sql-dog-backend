package com.cong.sqldog.core.sqlgenerate.builder;

import com.cong.sqldog.infrastructure.common.ErrorCode;
import com.cong.sqldog.core.sqlgenerate.builder.sql.MySQLDialect;
import com.cong.sqldog.core.sqlgenerate.builder.sql.SQLDialect;
import com.cong.sqldog.core.sqlgenerate.builder.sql.SQLDialectFactory;
import com.cong.sqldog.core.sqlgenerate.model.enums.FieldTypeEnum;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import com.cong.sqldog.infrastructure.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SQL 构建器
 *
 * @author cong
 * @date 2024/08/01
 */
@Slf4j
public class SqlBuilder {
    /**
     * 方言
     */
    private final SQLDialect sqlDialect;

    public SqlBuilder() {
        this.sqlDialect = SQLDialectFactory.getDialect(MySQLDialect.class.getName());
    }

    public SqlBuilder(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    /**
     * 构造建表 SQL
     *
     * @param tableSchema 表概要
     * @return 生成的 SQL
     */
    public String buildCreateTableSql(TableSchema tableSchema) {
        // 构造模板
        String template = """
                %s
                create table if not exists %s
                (
                %s
                ) %s;""";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        //带有数据库字段拼接
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表前缀注释
        String tableComment = tableSchema.getTableComment();
        if (StringUtils.isBlank(tableComment)) {
            tableComment = tableName;
        }
        String tablePrefixComment = String.format("-- %s", tableComment);
        // 构造表后缀注释
        String tableSuffixComment = String.format("comment '%s'", tableComment);
        // 构造表字段
        List<Field> fieldList = tableSchema.getFieldList();
        StringBuilder fieldStrBuilder = new StringBuilder();
        int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            Field field = fieldList.get(i);
            fieldStrBuilder.append(buildCreateFieldSql(field));
            // 最后一个字段后没有逗号和换行
            if (i != fieldSize - 1) {
                fieldStrBuilder.append(",");
                fieldStrBuilder.append("\n");
            }
        }
        String fieldStr = fieldStrBuilder.toString();
        // 填充模板
        String result = String.format(template, tablePrefixComment, tableName, fieldStr, tableSuffixComment);
        log.info("sql result = \n{}", result);
        return result;
    }

    /**
     * 生成创建字段的 SQL
     *
     * @param field 田
     * @return {@link String }
     */
    public String buildCreateFieldSql(Field field) {
        if (field == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String fieldName = sqlDialect.wrapFieldName(field.getFieldName());
        String fieldType = field.getFieldType();
        String defaultValue = field.getDefaultValue();
        boolean notNull = field.isNotNull();
        String comment = field.getComment();
        String onUpdate = field.getOnUpdate();
        boolean primaryKey = field.isPrimaryKey();
        boolean autoIncrement = field.isAutoIncrement();
        // e.g. column_name int default 0 not null auto_increment comment '注释' primary key,
        StringBuilder fieldStrBuilder = new StringBuilder();
        // 字段名
        fieldStrBuilder.append(fieldName);
        // 字段类型
        fieldStrBuilder.append(" ").append(fieldType);
        // 默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldStrBuilder.append(" ").append("default ").append(getValueStr(field, defaultValue));
        }
        // 是否非空
        fieldStrBuilder.append(" ").append(notNull ? "not null" : "null");
        // 是否自增
        if (autoIncrement) {
            fieldStrBuilder.append(" ").append("auto_increment");
        }
        // 附加条件
        if (StringUtils.isNotBlank(onUpdate)) {
            fieldStrBuilder.append(" ").append("on update ").append(onUpdate);
        }
        // 注释
        if (StringUtils.isNotBlank(comment)) {
            fieldStrBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        // 是否为主键
        if (primaryKey) {
            fieldStrBuilder.append(" ").append("primary key");
        }
        return fieldStrBuilder.toString();
    }

    /**
     * 构造插入数据 SQL
     * e.g. INSERT INTO report (id, content) VALUES (1, '这个有点问题吧');
     *
     * @param tableSchema 表概要
     * @param dataList 数据列表
     * @return 生成的 SQL 列表字符串
     */
    public String buildInsertSql(TableSchema tableSchema, List<Map<String, Object>> dataList) {
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表字段
        List<Field> fieldList = tableSchema.getFieldList();
        // 过滤掉不模拟的字段
        fieldList = fieldList.stream()
                .filter(field -> {
                    MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                            .orElse(MockTypeEnum.NONE);
                    return !MockTypeEnum.NONE.equals(mockTypeEnum);
                })
                .toList();
        StringBuilder resultStringBuilder = new StringBuilder();
        int total = dataList.size();
        for (int i = 0; i < total; i++) {
            Map<String, Object> dataRow = dataList.get(i);
            String keyStr = fieldList.stream()
                    .map(field -> sqlDialect.wrapFieldName(field.getFieldName()))
                    .collect(Collectors.joining(", "));
            String valueStr = fieldList.stream()
                    .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                    .collect(Collectors.joining(", "));
            // 填充模板
            String result = String.format(template, tableName, keyStr, valueStr);
            resultStringBuilder.append(result);
            // 最后一个字段后没有换行
            if (i != total - 1) {
                resultStringBuilder.append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    /**
     * 根据列的属性获取值字符串
     *
     * @param field 田
     * @param value 价值
     * @return {@link String }
     */
    public static String getValueStr(Field field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
                .orElse(FieldTypeEnum.TEXT);
        String result = String.valueOf(value);
        return switch (fieldTypeEnum) {
            case DATETIME, TIMESTAMP ->
                    result.equalsIgnoreCase("CURRENT_TIMESTAMP") ? result : String.format("'%s'", value);
            case DATE, TIME, CHAR, VARCHAR, TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT, TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB,
                 BINARY, VARBINARY -> String.format("'%s'", value);
            default -> result;
        };
    }
}

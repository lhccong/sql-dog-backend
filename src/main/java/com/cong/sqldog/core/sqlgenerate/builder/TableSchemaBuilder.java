package com.cong.sqldog.core.sqlgenerate.builder;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.core.sqlgenerate.builder.sql.MySQLDialect;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import com.cong.sqldog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 转表概要构建器
 *
 * @Author 香香
 * @Date 2024-08-14 15:32
 **/

@Component
@Slf4j
public class TableSchemaBuilder {

    /**
     * SQL 方言
     */
    private static final MySQLDialect sqlDialect = new MySQLDialect();

    /**
     * 私有化构造方法，防止被实例化
     */
    private TableSchemaBuilder() {
    }

    /**
     * 根据建表 SQL 构建
     *
     * @param sql 建表 SQL
     * @return 生成的 TableSchema
     */
    public static TableSchema buildFromSql(String sql) {
        // 检查传入的 SQL 字符串是否为空或仅包含空白字符
        if (StringUtils.isBlank(sql)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "SQL 语句不能为空");
        }

        try {
            // 创建 MySqlCreateTableParser 对象以解析 SQL 创建表语句
            MySqlCreateTableParser parser = new MySqlCreateTableParser(sql);

            // 解析 SQL 创建表语句并获取 SQLCreateTableStatement 对象
            SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();

            // 创建 TableSchema 对象来存储解析结果
            TableSchema tableSchema = new TableSchema();

            // 设置数据库名称
            tableSchema.setDbName(sqlCreateTableStatement.getSchema());

            // 设置表名称，使用 MySQLDialect 进行表名解析
            tableSchema.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getTableName()));

            // 获取并处理表注释
            String tableComment = null;
            if (sqlCreateTableStatement.getComment() != null) {
                // 去除注释的引号
                tableComment = getExtractComment(sqlCreateTableStatement.getComment().toString());
            }
            tableSchema.setTableComment(tableComment);

            // 创建列表以存储表字段
            List<TableSchema.Field> fieldList = new ArrayList<>();

            // 遍历 SQL 元素列表
            for (SQLTableElement sqlTableElement : sqlCreateTableStatement.getTableElementList()) {
                // 处理主键约束
                if (sqlTableElement instanceof SQLPrimaryKey sqlPrimaryKey) {
                    setPrimaryKey(sqlPrimaryKey, fieldList);
                    // 处理列相关
                } else if (sqlTableElement instanceof SQLColumnDefinition columnDefinition) {
                    setColumnKey(columnDefinition, fieldList);
                }
            }

            // 将字段列表设置到 TableSchema 对象中
            tableSchema.setFieldList(fieldList);

            return tableSchema;
        } catch (Exception e) {
            // 记录异常并抛出业务异常
            log.error("SQL 解析错误", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请确认 SQL 语句正确");
        }
    }

    public static TableSchema buildFromAuto(String content) {
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 切分单词
        String[] words = content.split("[,，]");
        if (ArrayUtils.isEmpty(words) || words.length > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO 根据单词去词库里匹配列信息，未匹配到的使用默认值
        TableSchema tableSchema = new TableSchema();
        tableSchema.setTableName("my_table");
        tableSchema.setTableComment("自动生成的表");
        List<Field> fieldList = new ArrayList<>();
        for (String word : words) {
            // 使用默认值
            fieldList.add(getDefaultField(word));
        }
        tableSchema.setFieldList(fieldList);
        return tableSchema;
    }

    private static void setColumnKey(SQLColumnDefinition columnDefinition, List<TableSchema.Field> fieldList) {
        // 创建并初始化 TableSchema.Field 对象
        TableSchema.Field field = new TableSchema.Field();
        field.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
        field.setFieldType(columnDefinition.getDataType().toString());

        // 获取列的默认值
        String defaultValue = null;
        if (columnDefinition.getDefaultExpr() != null) {
            defaultValue = columnDefinition.getDefaultExpr().toString();
        }
        field.setDefaultValue(defaultValue);

        // 设置列的非空约束
        field.setNotNull(columnDefinition.containsNotNullConstaint());

        // 获取并处理列注释
        String comment = null;
        if (columnDefinition.getComment() != null) {
            comment = columnDefinition.getComment().toString();
            // 去除注释的引号
            if (comment.length() > 2) {
                comment = comment.substring(1, comment.length() - 1);
            }
        }
        field.setComment(comment);

        // 设置列的主键标志、是否自增、更新值等属性
        field.setPrimaryKey(columnDefinition.isPrimaryKey());
        field.setAutoIncrement(columnDefinition.isAutoIncrement());

        String onUpdate = null;
        if (columnDefinition.getOnUpdate() != null) {
            onUpdate = columnDefinition.getOnUpdate().toString();
        }
        field.setOnUpdate(onUpdate);

        // 设置字段的 Mock 类型（默认为 NONE）
        field.setMockType(MockTypeEnum.NONE.getValue());

        // 将字段添加到字段列表中
        fieldList.add(field);
    }

    private static void setPrimaryKey(SQLPrimaryKey sqlPrimaryKey, List<TableSchema.Field> fieldList) {
        // 获取主键字段名
        String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString());

        // 将主键字段设置为主键
        fieldList.forEach(field -> {
            if (field.getFieldName().equals(primaryFieldName)) {
                field.setPrimaryKey(true);
            }
        });
    }

    private static String getExtractComment(String comment) {
        // 定义正则表达式
        String regex = "'([^']*)'";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 匹配字符串
        Matcher matcher = pattern.matcher(comment);

        // 查找并打印结果
        if (matcher.find()) {
            return matcher.group(1);  // 输出：用户
        } else {
            return "";
        }
    }

    private static Field getDefaultField(String word) {
        final Field field = new Field();
        field.setFieldName(word);
        field.setFieldType("text");
        field.setDefaultValue("");
        field.setNotNull(false);
        field.setComment(word);
        field.setPrimaryKey(false);
        field.setAutoIncrement(false);
        field.setMockType("");
        field.setMockParams("");
        field.setOnUpdate("");
        return field;
    }


}

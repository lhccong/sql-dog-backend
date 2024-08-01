package com.cong.sqldog.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.cong.sqldog.core.sqlgenerate.builder.sql.MySQLDialect;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
class TestSqlAnalyzeUtils {

    private static final MySQLDialect sqlDialect = new MySQLDialect();

    @Test
    void testCreateSqlAnalyzeUtils() {
        try {
            //建表 SQL 解析(正式环境需用 try-catch 捕获异常)
            TableSchema tableSchema = new TableSchema();
            MySqlCreateTableParser parser = new MySqlCreateTableParser(getMockCreateSql());
            SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
            tableSchema.setDbName(sqlCreateTableStatement.getSchema());
            log.info("数据库名称：{}", sqlCreateTableStatement.getSchema());
            log.info("表名：{}", sqlDialect.parseTableName(sqlCreateTableStatement.getName().getSimpleName()));
            tableSchema.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getName().getSimpleName()));
            String tableComment = null;
            if (sqlCreateTableStatement.getComment() != null) {
                tableComment = sqlCreateTableStatement.getComment().toString();
                if (tableComment.length() > 2) {
                    //表注释会带引号，需要去掉
                    tableComment = tableComment.substring(1, tableComment.length() - 1);
                }
            }
            log.info("表注释：{}", tableComment);
            tableSchema.setTableComment(tableComment);
            List<TableSchema.Field> fieldList = new ArrayList<>();
            // 解析列
            for (SQLTableElement sqlTableElement : sqlCreateTableStatement.getTableElementList()) {
                // 主键约束
                if (sqlTableElement instanceof SQLPrimaryKey sqlPrimaryKey) {
                    String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString());
                    fieldList.forEach(field -> {
                        if (field.getFieldName().equals(primaryFieldName)) {
                            log.info("设置主键：{}", field.getFieldName());
                            field.setPrimaryKey(true);
                        }
                    });
                } else if (sqlTableElement instanceof SQLColumnDefinition columnDefinition) {
                    log.info("============================================================================");
                    // 列
                    TableSchema.Field field = new TableSchema.Field();
                    log.info("列名：{}", sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                    field.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                    log.info("列字段类型：{}", columnDefinition.getDataType().toString());
                    field.setFieldType(columnDefinition.getDataType().toString());
                    String defaultValue = null;
                    if (columnDefinition.getDefaultExpr() != null) {
                        defaultValue = columnDefinition.getDefaultExpr().toString();
                        log.info("列默认值：{}", defaultValue);
                    }
                    field.setDefaultValue(defaultValue);
                    field.setNotNull(columnDefinition.containsNotNullConstaint());
                    log.info("字段是否为空：{}", columnDefinition.containsNotNullConstaint() ? "不允许为空" : "允许为空");
                    String comment = null;
                    if (columnDefinition.getComment() != null) {
                        comment = columnDefinition.getComment().toString();
                        if (comment.length() > 2) {
                            comment = comment.substring(1, comment.length() - 1);
                            log.info("列注释：{}", comment);
                        }
                    }
                    field.setComment(comment);
                    field.setPrimaryKey(columnDefinition.isPrimaryKey());
                    log.info("是否为主键：{}", columnDefinition.isPrimaryKey() ? "是" : "否");
                    field.setAutoIncrement(columnDefinition.isAutoIncrement());
                    log.info("是否为自增：{}", columnDefinition.isAutoIncrement() ? "是" : "否");
                    String onUpdate = null;
                    if (columnDefinition.getOnUpdate() != null) {
                        onUpdate = columnDefinition.getOnUpdate().toString();
                        log.info("列更新时机：{}", onUpdate);
                    }
                    field.setOnUpdate(onUpdate);
                    field.setMockType(MockTypeEnum.NONE.getValue());
                    fieldList.add(field);
                }
            }
            log.info("============================================================================");
            tableSchema.setFieldList(fieldList);
            log.info("表结构：{}", tableSchema);
            Assertions.assertNotNull(tableSchema.getTableName());
        } catch (Exception e) {
            log.error("解析建表 SQL 失败", e);

        }
    }

    @Test
    void testSelectSqlAnalyzeUtils() {
        List<SQLStatement> statementList = SQLUtils.parseStatements(getMockSelectSql(), DbType.mysql);
        for (SQLStatement statement : statementList) {
            if (statement instanceof SQLSelectStatement selectStatement) {
                SchemaStatVisitor visitor = new SchemaStatVisitor(DbType.mysql);
                statement.accept(visitor);

                //解析表名
                SQLSelectQueryBlock queryBlock = selectStatement.getSelect().getFirstQueryBlock();
                SQLTableSource from = queryBlock.getFrom();
                SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) from;
                String tableName = sqlExprTableSource.getTableName();
                log.info("表名：{}", tableName);
                //查询列
                Collection<TableStat.Column> columns = visitor.getColumns();
                List<String> columnList = new ArrayList<>();
                columns.forEach(row -> {
                    if (row.isSelect()) {
                        //保存select字段
                        columnList.add(row.getName());
                    }
                });
                log.info("查询列：{}", Arrays.toString(columnList.toArray()));
                //查询过滤条件 where
                List<TableStat.Condition> conditions = visitor.getConditions();
                conditions.forEach(condition -> log.info("查询条件：{} {} {}", condition.getColumn().getName(), condition.getOperator(), condition.getValues()));
                log.info("groupBy 字段：{}", Arrays.toString(visitor.getOrderByColumns().toArray()));
                log.info("orderBy 字段：{}", Arrays.toString(visitor.getOrderByColumns().toArray()));
                log.info("limit ：{}", queryBlock.getLimit().getRowCount());
                log.info("offset ：{}", queryBlock.getOffset());
                break;
            }
        }
    }

    String getMockSelectSql() {
        return "SELECT * FROM orders where orderId = 1 and customerId = 2 and totalAmount > 100 group by orderId, customerId order by orderId desc limit 10, 1000";
    }

    String getMockCreateSql() {
        return """
                create table if not exists table_info
       (
           id            bigint auto_increment comment 'id' primary key,
           name          varchar(512)                       null comment '名称',
           content       text                               null comment '表信息（json）',
           reviewStatus  int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
           reviewMessage varchar(512)                       null comment '审核信息',
           userId        bigint                             not null comment '创建用户 id',
           createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
           updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
           isDelete      tinyint  default 0                 not null comment '是否删除'
       ) comment '表信息';""";
    }
}

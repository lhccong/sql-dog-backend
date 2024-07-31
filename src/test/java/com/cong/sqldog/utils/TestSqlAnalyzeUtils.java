package com.cong.sqldog.utils;

import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.cong.sqldog.core.sqlgenerate.builder.MySQLDialect;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class TestSqlAnalyzeUtils {

    private static final MySQLDialect sqlDialect = new MySQLDialect();

    @Test
    void testCreateSqlAnalyzeUtils() {
        //创建 SQL 解析
        TableSchema tableSchema = new TableSchema();
        MySqlCreateTableParser parser = new MySqlCreateTableParser(getMockCreateSql());
        SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
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
                // 列
                TableSchema.Field field = new TableSchema.Field();
                log.info("列名：{}", sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                field.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                log.info("列字段类型：{}", columnDefinition.getDataType().toString());
                field.setFieldType(columnDefinition.getDataType().toString());
                String defaultValue = null;
                if (columnDefinition.getDefaultExpr() != null) {
                    defaultValue = columnDefinition.getDefaultExpr().toString();
                }
                field.setDefaultValue(defaultValue);
                field.setNotNull(columnDefinition.containsNotNullConstaint());
                String comment = null;
                if (columnDefinition.getComment() != null) {
                    comment = columnDefinition.getComment().toString();
                    if (comment.length() > 2) {
                        comment = comment.substring(1, comment.length() - 1);
                    }
                }
                field.setComment(comment);
                field.setPrimaryKey(columnDefinition.isPrimaryKey());
                field.setAutoIncrement(columnDefinition.isAutoIncrement());
                String onUpdate = null;
                if (columnDefinition.getOnUpdate() != null) {
                    onUpdate = columnDefinition.getOnUpdate().toString();
                }
                field.setOnUpdate(onUpdate);
//                field.setMockType(MockTypeEnum.NONE.getValue());
                fieldList.add(field);
            }
        }
        tableSchema.setFieldList(fieldList);
        Assertions.assertNotNull(tableSchema.getTableName());
    }

    String getMockCreateSql() {
        return """
                CREATE TABLE  sql_dog.Orders (
                    OrderID INT AUTO_INCREMENT PRIMARY KEY,           -- 自增主键
                    CustomerID INT NOT NULL,                          -- 客户ID，外键
                    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,     -- 订单日期，默认当前时间
                    Status ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',  -- 订单状态
                    TotalAmount DECIMAL(10, 2) CHECK (TotalAmount >= 0), -- 总金额，带有检查约束
                    Comments TEXT,                                    -- 备注
                    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- 创建时间，默认当前时间
                    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间，自动更新
                    INDEX idx_customer_id (CustomerID),               -- 索引
                    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)  -- 外键约束
                ) COMMENT = 'This table stores order details for the sql_dog database';""";
    }
}

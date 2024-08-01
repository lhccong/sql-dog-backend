package com.cong.sqldog.utils;

import cn.hutool.json.JSONUtil;
import com.cong.sqldog.core.sqlgenerate.builder.JsonBuilder;
import com.cong.sqldog.core.sqlgenerate.builder.MockDataBuilder;
import com.cong.sqldog.core.sqlgenerate.builder.SqlBuilder;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
class TestGeneratorData {

    @Test
    void generateData() {
        SqlBuilder sqlBuilder = new SqlBuilder();
        String TableSchemaStr = "{\"tableName\":\"table_info\",\"tableComment\":\"表信息\",\"fieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"notNull\":false,\"comment\":\"id\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"不模拟\"},{\"fieldName\":\"name\",\"fieldType\":\"varchar(512)\",\"notNull\":false,\"comment\":\"名称\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"随机\",\"mockParams\":\"人名\"},{\"fieldName\":\"content\",\"fieldType\":\"text\",\"notNull\":false,\"comment\":\"表信息（json）\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"},{\"fieldName\":\"reviewStatus\",\"fieldType\":\"int\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"状态（0-待审核, 1-通过, 2-拒绝）\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"},{\"fieldName\":\"reviewMessage\",\"fieldType\":\"varchar(512)\",\"notNull\":false,\"comment\":\"审核信息\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"},{\"fieldName\":\"userId\",\"fieldType\":\"bigint\",\"notNull\":true,\"comment\":\"创建用户 id\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"},{\"fieldName\":\"createTime\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"},{\"fieldName\":\"updateTime\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\",\"onUpdate\":\"CURRENT_TIMESTAMP\"},{\"fieldName\":\"isDelete\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"是否删除\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"不模拟\"}]}";
        TableSchema tableSchema = JSONUtil.toBean(TableSchemaStr, TableSchema.class);
        //生成 SQL
        String tableSQL = sqlBuilder.buildCreateTableSql(tableSchema);
        Assertions.assertNotNull(tableSQL);

        //生成数据
        List<Map<String, Object>> list = MockDataBuilder.generateMockData(tableSchema, 2);
        log.info("生成数据：{}", Arrays.toString(list.toArray()));

        //构造 SQL 插入数据
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, list);
        log.info("构造 SQL 插入数据：{}", insertSql);
        //构造Json
        String jsonStr = JsonBuilder.buildJson(list);
        log.info("构造Json：{}", jsonStr);
    }
}

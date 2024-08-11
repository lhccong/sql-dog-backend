package com.cong.sqldog.controller;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.TestBaseByLogin;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.model.vo.sql.GenerateVO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

class SqlControllerTest extends TestBaseByLogin {

    @Test
    void testGenerateData() throws Exception {
        TableSchema tableSchema = getTableSchema();

        // 发送请求并验证结果
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/sql/generate/schema")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableSchema))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        String content = resultActions.andReturn().getResponse().getContentAsString();

        BaseResponse<GenerateVO> baseResponse = JSONUtil.toBean(content, new TypeReference<>() {
        }, false);
        GenerateVO generateVO = baseResponse.getData();


        checkData(generateVO);
    }

    @Test
    void testGenerateDataByNullField() throws Exception {
        TableSchema tableSchema = getTableSchemaByNullFieldComment();

        // 发送请求并验证结果
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/sql/generate/schema")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableSchema))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        String content = resultActions.andReturn().getResponse().getContentAsString();

        BaseResponse<GenerateVO> baseResponse = JSONUtil.toBean(content, new TypeReference<>() {
        }, false);
        GenerateVO generateVO = baseResponse.getData();


        checkData(generateVO);
    }

    private static void checkData(GenerateVO generateVO) {
        Assertions.assertFalse(generateVO.getDataList().isEmpty());
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getCreateSql()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getInsertSql()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getDataJson()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getJavaEntityCode()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getJavaObjectCode()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getTypescriptTypeCode()));
        Assertions.assertTrue(StringUtils.isNotBlank(generateVO.getPlantUmlCode()));

        System.out.println(generateVO);
    }

    private static TableSchema getTableSchemaByNullFieldComment() {
        // 准备测试数据
        TableSchema.Field field1 = new TableSchema.Field();
        field1.setFieldName("id");
        field1.setFieldType("bigint");
        field1.setNotNull(true);
        field1.setComment(null);
        field1.setPrimaryKey(true);
        field1.setAutoIncrement(true);
        field1.setMockType("递增");

        TableSchema.Field field2 = new TableSchema.Field();
        field2.setFieldName("name");
        field2.setFieldType("string");
        field2.setComment(null);
        field2.setPrimaryKey(false);
        field2.setAutoIncrement(false);
        field2.setMockType("随机");

        TableSchema tableSchema = new TableSchema();
        tableSchema.setDbName("test");
        tableSchema.setTableName("user");
        tableSchema.setTableComment("用户表");
        tableSchema.setMockNum(10);
        tableSchema.setFieldList(Arrays.asList(field1, field2));
        return tableSchema;
    }

    private static TableSchema getTableSchema() {
        // 准备测试数据
        TableSchema.Field field1 = new TableSchema.Field();
        field1.setFieldName("id");
        field1.setFieldType("bigint");
        field1.setNotNull(true);
        field1.setComment("主键");
        field1.setPrimaryKey(true);
        field1.setAutoIncrement(true);
        field1.setMockType("递增");

        TableSchema.Field field2 = new TableSchema.Field();
        field2.setFieldName("name");
        field2.setFieldType("string");
        field2.setComment("名称");
        field2.setPrimaryKey(false);
        field2.setAutoIncrement(false);
        field2.setMockType("随机");

        TableSchema tableSchema = new TableSchema();
        tableSchema.setDbName("test");
        tableSchema.setTableName("user");
        tableSchema.setTableComment("用户表");
        tableSchema.setMockNum(10);
        tableSchema.setFieldList(Arrays.asList(field1, field2));
        return tableSchema;
    }
}

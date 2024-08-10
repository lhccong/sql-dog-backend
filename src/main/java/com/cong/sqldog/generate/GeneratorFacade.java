package com.cong.sqldog.generate;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.core.sqlgenerate.builder.*;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.model.vo.sql.GenerateVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GeneratorFacade {
    GeneratorFacade() {

    }
    public static GenerateVO generateAll(TableSchema tableSchema) {
        // 校验
        validSchema(tableSchema);
        SqlBuilder sqlBuilder = new SqlBuilder();
        log.info("开始构建数据");
        // 构造建表 SQL
        String createSql = sqlBuilder.buildCreateTableSql(tableSchema);
        int mockNum = tableSchema.getMockNum();
        // 生成模拟数据
        List<Map<String, Object>> dataList = MockDataBuilder.generateMockData(tableSchema, mockNum);
        // 生成插入 SQL
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, dataList);
        // 生成数据 json
        String dataJson = JsonBuilder.buildJson(dataList);
        // 生成 java 实体代码
        String javaEntityCode = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        // 生成 java 对象代码
        String javaObjectCode = JavaCodeBuilder.buildJavaObjectCode(tableSchema, dataList);
        // 生成 typescript 类型代码
        String typescriptTypeCode = TypeScriptBuilder.buildTypeScriptTypeCode(tableSchema);
        log.info("建数据结束");

        // 封装返回
        return new GenerateVO()
                .setCreateSql(createSql)
                .setTableSchema(tableSchema)
                .setInsertSql(insertSql)
                .setDataJson(dataJson)
                .setDataList(dataList)
                .setJavaEntityCode(javaEntityCode)
                .setJavaObjectCode(javaObjectCode)
                .setTypescriptTypeCode(typescriptTypeCode);
    }

    /**
     * 验证 schema
     *
     * @param tableSchema 表概要
     */
    public static void validSchema(TableSchema tableSchema) {
        if (tableSchema == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        String tableName = tableSchema.getTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表名不能为空");
        }
        Integer mockNum = tableSchema.getMockNum();
        // 默认生成 20 条
        if (tableSchema.getMockNum() == null) {
            tableSchema.setMockNum(20);
            mockNum = 20;
        }
        if (mockNum > 100 || mockNum < 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成条数设置错误");
        }
        List<Field> fieldList = tableSchema.getFieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段列表不能为空");
        }
        for (Field field : fieldList) {
            validField(field);
        }
    }

    /**
     * 校验字段
     */
    public static void validField(Field field) {
        String fieldName = field.getFieldName();
        String fieldType = field.getFieldType();
        if (StringUtils.isBlank(fieldName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段名不能为空");
        }
        if (StringUtils.isBlank(fieldType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段类型不能为空");
        }
    }
}

package com.cong.sqldog.core.sqlgenerate.builder;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cong.sqldog.core.sqlgenerate.generator.DataGenerator;
import com.cong.sqldog.core.sqlgenerate.generator.DataGeneratorFactory;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 模拟数据生成器
 *
 * @author cong
 * @date 2024/08/01
 */
@Slf4j
public class MockDataBuilder {
    private MockDataBuilder() {

    }

    public static List<Map<String, Object>> generateMockData(TableSchema tableSchema, int rowNum) {
        List<Field> fieldList = tableSchema.getFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>());
        }
        // 依次生成每一列
        for (Field field : fieldList) {
            MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                    .orElse(MockTypeEnum.NONE);
            DataGenerator dataGenerator = DataGeneratorFactory.getGenerator(mockTypeEnum);
            List<Object> mockDataList = dataGenerator.doGenerate(field, rowNum);
            String fieldName = field.getFieldName();
            // 填充结果列表
            if (CollectionUtils.isNotEmpty(mockDataList)) {
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName, isNumber(field) ? Long.parseLong(mockDataList.get(i).toString()) : mockDataList.get(i));
                }
            }
        }
        return resultList;
    }

    private static boolean isNumber(Field field) {
        return "bigint".equalsIgnoreCase(field.getFieldType())
                || "int".equalsIgnoreCase(field.getFieldType())
                || "tinyint".equalsIgnoreCase(field.getFieldType());
    }
}

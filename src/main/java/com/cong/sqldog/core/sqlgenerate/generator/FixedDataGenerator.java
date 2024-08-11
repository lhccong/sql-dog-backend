package com.cong.sqldog.core.sqlgenerate.generator;

import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 固定值数据生成器
 *
 * @author cong
 */
public class FixedDataGenerator implements DataGenerator {

    @Override
    public List<Object> doGenerate(Field field, int rowNum) {
        String mockParams = field.getMockParams();
        if (StringUtils.isBlank(mockParams)) {
            mockParams = isNumber(field) ? "1" : "我真爱粉啊哥们，music~";
        }
        List<Object> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            list.add(mockParams);
        }
        return list;
    }

    private static boolean isNumber(Field field) {
        return "bigint".equalsIgnoreCase(field.getFieldType())
                || "int".equalsIgnoreCase(field.getFieldType())
                || "tinyint".equalsIgnoreCase(field.getFieldType());
    }
}

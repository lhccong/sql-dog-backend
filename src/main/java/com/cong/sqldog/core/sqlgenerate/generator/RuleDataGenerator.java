package com.cong.sqldog.core.sqlgenerate.generator;

import com.mifmif.common.regex.Generex;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * 正则表达式数据生成器
 *
 * @author cong
 * @date 2024/08/01
 */
public class RuleDataGenerator implements DataGenerator {

    @Override
    public List<Object> doGenerate(Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<Object> list = new ArrayList<>(rowNum);
        Generex generex = new Generex(mockParams);
        for (int i = 0; i < rowNum; i++) {
            String randomStr = generex.random();
            list.add(randomStr);
        }
        return list;
    }
}

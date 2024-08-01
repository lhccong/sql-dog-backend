package com.cong.sqldog.core.sqlgenerate.generator;


import com.cong.sqldog.core.sqlgenerate.model.enums.MockParamsRandomTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import com.cong.sqldog.core.sqlgenerate.util.FakerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 随机值数据生成器
 *
 * @author cong
 * @date 2024/08/01
 */
public class RandomDataGenerator implements DataGenerator {

    @Override
    public List<Object> doGenerate(Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<Object> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            MockParamsRandomTypeEnum randomTypeEnum = Optional.ofNullable(
                            MockParamsRandomTypeEnum.getEnumByValue(mockParams))
                    .orElse(MockParamsRandomTypeEnum.STRING);
            String randomString = FakerUtils.getRandomValue(randomTypeEnum);
            list.add(randomString);
        }
        return list;
    }
}

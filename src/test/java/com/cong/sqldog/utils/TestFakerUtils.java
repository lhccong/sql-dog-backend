package com.cong.sqldog.utils;

import com.cong.sqldog.core.sqlgenerate.model.MockParamsRandomTypeEnum;
import com.cong.sqldog.core.sqlgenerate.util.FakerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
class TestFakerUtils {

    @Test
    void testGetMockName() {
        List<String> values = MockParamsRandomTypeEnum.getValues();
        for (String value : values) {
            MockParamsRandomTypeEnum randomType = MockParamsRandomTypeEnum.getEnumByValue(value);
            String mockName = FakerUtils.getRandomValue(randomType);
            Assertions.assertFalse(mockName.isEmpty());
            log.info("模拟{}：{}", value, mockName);
        }

    }
}

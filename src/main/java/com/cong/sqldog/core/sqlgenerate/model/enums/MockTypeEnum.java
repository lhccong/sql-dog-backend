package com.cong.sqldog.core.sqlgenerate.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 模拟类型枚举
 *
 * @author cong
 * @date 2024/08/01
 */
@Getter
public enum MockTypeEnum {

    NONE("不模拟"),
    INCREASE("递增"),
    FIXED("固定"),
    RANDOM("随机"),
    RULE("规则"),
    DICT("词库");

    private final String value;

    MockTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(MockTypeEnum::getValue).toList();
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 价值
     * @return {@link MockTypeEnum }
     */
    public static MockTypeEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (MockTypeEnum mockTypeEnum : MockTypeEnum.values()) {
            if (mockTypeEnum.value.equals(value)) {
                return mockTypeEnum;
            }
        }
        return null;
    }

}

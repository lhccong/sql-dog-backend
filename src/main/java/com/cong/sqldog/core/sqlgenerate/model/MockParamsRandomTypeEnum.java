package com.cong.sqldog.core.sqlgenerate.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 模拟参数随机类型枚举
 *
 * @author cong
 * @date 2024/07/31
 */
@Getter
public enum MockParamsRandomTypeEnum {

    STRING("字符串"),
    NAME("人名"),
    CITY("城市"),
    URL("网址"),
    EMAIL("邮箱"),
    IP("IP"),
    INTEGER("整数"),
    DECIMAL("小数"),
    UNIVERSITY("大学"),
    DATE("日期"),
    TIMESTAMP("时间戳"),
    PHONE("手机号");

    private final String value;

    MockParamsRandomTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(MockParamsRandomTypeEnum::getValue).toList();
    }

    /**
     * 根据 value 获取枚举
     */
    public static MockParamsRandomTypeEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (MockParamsRandomTypeEnum mockTypeEnum : MockParamsRandomTypeEnum.values()) {
            if (mockTypeEnum.value.equals(value)) {
                return mockTypeEnum;
            }
        }
        return null;
    }

}

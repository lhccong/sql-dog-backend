package com.cong.sqldog.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 关卡类型枚举
 *
 * @author shing
 */
@Getter
public enum TopicTypeEnum {

    CUSTOM("自定义", "custom"),

    SYSTEM("系统", "system");

    private final String text;

    private final String value;

    TopicTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).toList();
    }

    /**
     * 根据 value 获取枚举
     */
    public static TopicTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TopicTypeEnum anEnum : TopicTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

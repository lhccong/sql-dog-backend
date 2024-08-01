package com.cong.sqldog.core.sqlgenerate.model.enums;

import com.cong.sqldog.core.sqlgenerate.constant.DataTypeConstant;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段类型枚举
 *
 * @author cong
 * @date 2024/08/01
 */
@Getter
public enum FieldTypeEnum {

    TINYINT("tinyint", DataTypeConstant.INTEGER, DataTypeConstant.NUMBER),
    SMALLINT("smallint", DataTypeConstant.INTEGER, DataTypeConstant.NUMBER),
    MEDIUMINT("mediumint", DataTypeConstant.INTEGER, DataTypeConstant.NUMBER),
    INT("int", DataTypeConstant.INTEGER, DataTypeConstant.NUMBER),
    BIGINT("bigint", "Long", DataTypeConstant.NUMBER),
    FLOAT("float", "Double", DataTypeConstant.NUMBER),
    DOUBLE("double", "Double", DataTypeConstant.NUMBER),
    DECIMAL("decimal", "BigDecimal", DataTypeConstant.NUMBER),
    DATE(DataTypeConstant.DATE, DataTypeConstant.DATE, DataTypeConstant.DATE),
    TIME("time", "Time", DataTypeConstant.DATE),
    YEAR("year", DataTypeConstant.INTEGER, DataTypeConstant.NUMBER),
    DATETIME("datetime", DataTypeConstant.DATE, DataTypeConstant.DATE),
    TIMESTAMP("timestamp", "Long", DataTypeConstant.NUMBER),
    CHAR("char", DataTypeConstant.STRING, DataTypeConstant.STRING),
    VARCHAR("varchar", DataTypeConstant.STRING, DataTypeConstant.STRING),
    TINYTEXT("tinytext", DataTypeConstant.STRING, DataTypeConstant.STRING),
    TEXT("text", DataTypeConstant.STRING, DataTypeConstant.STRING),
    MEDIUMTEXT("mediumtext", DataTypeConstant.STRING, DataTypeConstant.STRING),
    LONGTEXT("longtext", DataTypeConstant.STRING, DataTypeConstant.STRING),
    TINYBLOB("tinyblob", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING),
    BLOB("blob", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING),
    MEDIUMBLOB("mediumblob", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING),
    LONGBLOB("longblob", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING),
    BINARY("binary", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING),
    VARBINARY("varbinary", DataTypeConstant.BYTE_ARRAY, DataTypeConstant.STRING);

    private final String value;

    private final String javaType;

    private final String typescriptType;

    FieldTypeEnum(String value, String javaType, String typescriptType) {
        this.value = value;
        this.javaType = javaType;
        this.typescriptType = typescriptType;
    }

    /**
     * 获取值列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(FieldTypeEnum::getValue).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 价值
     * @return {@link FieldTypeEnum }
     */
    public static FieldTypeEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (FieldTypeEnum mockTypeEnum : FieldTypeEnum.values()) {
            if (mockTypeEnum.value.equals(value)) {
                return mockTypeEnum;
            }
        }
        return null;
    }

}

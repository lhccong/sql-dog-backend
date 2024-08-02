package com.cong.sqldog.core.sqlgenerate.builder;

import cn.hutool.core.text.CharSequenceUtil;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.core.sqlgenerate.model.dto.JavaEntityGenerateDTO;
import com.cong.sqldog.core.sqlgenerate.model.dto.JavaEntityGenerateDTO.FieldDTO;
import com.cong.sqldog.core.sqlgenerate.model.dto.JavaObjectGenerateDTO;
import com.cong.sqldog.core.sqlgenerate.model.dto.JavaObjectGenerateDTO.FieldObjectDTO;
import com.cong.sqldog.core.sqlgenerate.model.enums.FieldTypeEnum;
import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
import com.cong.sqldog.exception.BusinessException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Java 代码构建器
 *
 * @author cong
 * @date 2024/08/01
 */
@Component
@Slf4j
public class JavaCodeBuilder {

    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        JavaCodeBuilder.configuration = configuration;
    }

    /**
     * 构造 Java 实体代码
     *
     * @param tableSchema 表概要
     * @return 生成的 java 代码
     */
    @SneakyThrows
    public static String buildJavaEntityCode(TableSchema tableSchema) {
        String tableName = tableSchema.getTableName();
        //StringUtils.capitalize 将字符串的第一个字符转换为大写，其余字符保持不变
        String upperCamelTableName = StringUtils.capitalize(CharSequenceUtil.toCamelCase(tableName));
        String tableComment = Optional.ofNullable(tableSchema.getTableComment()).orElse(upperCamelTableName);
        // 依次填充每一列
        List<FieldDTO> fieldDTOList = new ArrayList<>();
        tableSchema.getFieldList().forEach(field -> {
            FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType())).orElse(FieldTypeEnum.TEXT);
            FieldDTO fieldDTO = new FieldDTO()
                    .setComment(field.getComment())
                    .setJavaType(fieldTypeEnum.getJavaType())
                    .setFieldName(CharSequenceUtil.toCamelCase(field.getFieldName()));
            fieldDTOList.add(fieldDTO);
        });
        // 填充模板
        // 传递参数==>类名为大写的表名，类注释为表注释/表名
        JavaEntityGenerateDTO javaEntityGenerateDTO = new JavaEntityGenerateDTO()
                .setClassName(upperCamelTableName)
                .setClassComment(tableComment)
                .setFieldList(fieldDTOList);

        StringWriter stringWriter = new StringWriter();
        Template temp = configuration.getTemplate("java_entity.ftl");
        temp.process(javaEntityGenerateDTO, stringWriter);
        return stringWriter.toString();
    }

    /**
     * 构造 Java 对象代码
     *
     * @param tableSchema 表概要
     * @param dataList    数据列表
     * @return 生成的 java 代码
     */
    @SneakyThrows
    public static String buildJavaObjectCode(TableSchema tableSchema, List<Map<String, Object>> dataList) {
        // 依次填充每一列
        Map<String, Object> fillData = dataList.stream().findFirst().orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "缺少示例数据"));
        List<FieldObjectDTO> fieldDTOList = new ArrayList<>();
        List<Field> fieldList = tableSchema.getFieldList();
        // 过滤掉不模拟的字段
        fieldList = fieldList.stream()
                .filter(field -> {
                    MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType())).orElse(MockTypeEnum.NONE);
                    return !MockTypeEnum.NONE.equals(mockTypeEnum);
                })
                .toList();
        for (Field field : fieldList) {
            FieldObjectDTO fieldDTO = new FieldObjectDTO()
                    .setSetMethod(CharSequenceUtil.toCamelCase("set_" + field.getFieldName()))
                    .setValue(getValueStr(field, fillData.get(field.getFieldName())));// 驼峰字段名
            fieldDTOList.add(fieldDTO);
        }

        // 传递参数
        JavaObjectGenerateDTO javaObjectGenerateDTO = new JavaObjectGenerateDTO()
                .setClassName(StringUtils.capitalize(CharSequenceUtil.toCamelCase(tableSchema.getTableName()))) // 类名为大写的表名
                .setObjectName(CharSequenceUtil.toCamelCase(tableSchema.getTableName())) // 变量名为表名
                .setFieldList(fieldDTOList);

        //构造模板
        StringWriter stringWriter = new StringWriter();
        Template temp = configuration.getTemplate("java_object.ftl");
        temp.process(javaObjectGenerateDTO, stringWriter);

        return stringWriter.toString();
    }

    /**
     * 根据列的属性获取值字符串
     *
     * @param field 田
     * @param value 价值
     * @return {@link String }
     */
    public static String getValueStr(Field field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
                .orElse(FieldTypeEnum.TEXT);
        return switch (fieldTypeEnum) {
            case DATE, TIME, DATETIME, CHAR, VARCHAR, TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT, TINYBLOB, BLOB, MEDIUMBLOB,
                 LONGBLOB, BINARY, VARBINARY -> String.format("\"%s\"", value);
            default -> String.valueOf(value);
        };
    }
}

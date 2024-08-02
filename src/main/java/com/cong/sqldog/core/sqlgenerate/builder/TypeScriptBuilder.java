package com.cong.sqldog.core.sqlgenerate.builder;

import cn.hutool.core.text.CharSequenceUtil;
import com.cong.sqldog.core.sqlgenerate.model.dto.TypescriptTypeGenerateDTO;
import com.cong.sqldog.core.sqlgenerate.model.dto.TypescriptTypeGenerateDTO.FieldTypeScriptDTO;
import com.cong.sqldog.core.sqlgenerate.model.enums.FieldTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
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
import java.util.Optional;

/**
 * 类型：脚本生成器
 *
 * @author cong
 * @date 2024/08/01
 */
@Component
@Slf4j
public class TypeScriptBuilder {

    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        TypeScriptBuilder.configuration = configuration;
    }

    /**
     * 构造 Typescript 类型代码
     *
     * @param tableSchema 表概要
     * @return 生成的代码
     */
    @SneakyThrows
    public static String buildTypeScriptTypeCode(TableSchema tableSchema) {
        String tableComment = tableSchema.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(CharSequenceUtil.toCamelCase(tableSchema.getTableName()));

        // 依次填充每一列
        List<FieldTypeScriptDTO> fieldDTOList = new ArrayList<>();
        tableSchema.getFieldList().forEach(field -> {
            FieldTypeScriptDTO fieldDTO = new FieldTypeScriptDTO()
                    .setComment(field.getComment())
                    .setTypescriptType(Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType())).orElse(FieldTypeEnum.TEXT).getTypescriptType())
                    .setFieldName(CharSequenceUtil.toCamelCase(field.getFieldName()));
            fieldDTOList.add(fieldDTO);
        });

        // 传递参数
        TypescriptTypeGenerateDTO generateDTO = new TypescriptTypeGenerateDTO()
                .setClassName(upperCamelTableName) // 类名为大写的表名
                .setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName)) // 类注释为表注释或表名
                .setFieldList(fieldDTOList);
        // 生成代码
        StringWriter stringWriter = new StringWriter();
        Template temp = configuration.getTemplate("typescript_type.ftl");
        temp.process(generateDTO, stringWriter);
        return stringWriter.toString();
    }
}

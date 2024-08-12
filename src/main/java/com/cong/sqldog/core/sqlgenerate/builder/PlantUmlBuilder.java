package com.cong.sqldog.core.sqlgenerate.builder;

import com.cong.sqldog.core.sqlgenerate.model.dto.PlantUmlGenerateDTO;
import com.cong.sqldog.core.sqlgenerate.model.enums.FieldTypeEnum;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * PlantUML 代码构建器
 *
 * @author 香香
 */
@Component
@Slf4j
public class PlantUmlBuilder {


    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        PlantUmlBuilder.configuration = configuration;
    }

    /**
     * 构造 PlantUML 代码
     *
     * @param tableSchema 表概要对象
     * @return 生成的 PlantUML 代码
     */
    public static String buildPlantUmlCode(TableSchema tableSchema) {

        // 表名称
        String tableName = tableSchema.getTableName();
        // 表注释
        String tableComment = tableSchema.getTableComment();
        // 依次填充每一列
        List<PlantUmlGenerateDTO.FieldDTO> fieldList = new ArrayList<>();
        tableSchema.getFieldList().forEach(field -> {
            PlantUmlGenerateDTO.FieldDTO fieldDTO = new PlantUmlGenerateDTO.FieldDTO()
                    .setFieldName(field.getFieldName())
                    .setFieldType(field.getFieldType())
                    .setFieldComment(Optional.ofNullable(field.getComment()).orElse(""));
            fieldList.add(fieldDTO);
        });

        // 创建 PlantUmlGenerateDTO 对象，填充模板,传递参数
        PlantUmlGenerateDTO plantUmlGenerateDTO = new PlantUmlGenerateDTO();
        plantUmlGenerateDTO.setTableName(tableName);
        plantUmlGenerateDTO.setTableComment(tableComment);
        plantUmlGenerateDTO.setFieldList(fieldList);

        // 用于生成 PlantUML 描述的模板处理代码
        StringWriter stringWriter = new StringWriter();
        try {
            Template temp = configuration.getTemplate("tableSchemaToPlantUML.ftl");
            temp.process(plantUmlGenerateDTO, stringWriter);
        } catch (Exception e) {
            // 处理模板处理异常
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}
package com.cong.sqldog.core.sqlgenerate.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * UML 实体生成封装类
 *
 * @author 香香
 */
@Data
@Accessors(chain = true)
public  class PlantUmlGenerateDTO {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 字段列表
     */
    private List<FieldDTO> fieldList;

    @Data
    @Accessors(chain = true)
    public static class FieldDTO {

        /**
         * 字段名
         */
        private String fieldName;

        /**
         * 字段类型
         */
        private String fieldType;

        /**
         * 字段注释
         */
        private String fieldComment;
    }
}
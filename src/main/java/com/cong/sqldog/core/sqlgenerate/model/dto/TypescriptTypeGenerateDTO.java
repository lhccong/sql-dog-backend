package com.cong.sqldog.core.sqlgenerate.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Typescript 类型生成封装类
 *
 * @author cong
 * @date 2024/08/02
 */
@Data
@Accessors(chain = true)
public class TypescriptTypeGenerateDTO {

    /**
     * 类名
     */
    private String className;

    /**
     * 类注释
     */
    private String classComment;

    /**
     * 列信息列表
     */
    private List<FieldTypeScriptDTO> fieldList;

    /**
     * 列信息
     */
    @Data
    @Accessors(chain = true)
    public static class FieldTypeScriptDTO {

        /**
         * 字段名
         */
        private String fieldName;

        /**
         * Typescript 类型
         */
        private String typescriptType;

        /**
         * 字段注释
         */
        private String comment;
    }

}

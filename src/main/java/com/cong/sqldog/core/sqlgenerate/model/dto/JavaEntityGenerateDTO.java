package com.cong.sqldog.core.sqlgenerate.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Java 实体生成封装类
 *
 * @author cong
 * @date 2024/08/02
 */
@Data
@Accessors(chain = true)
public class JavaEntityGenerateDTO {

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
    private List<FieldDTO> fieldList;

    /**
     * 列信息
     */
    @Data
    @Accessors(chain = true)
    public static class FieldDTO {
        /**
         * 字段名
         */
        private String fieldName;

        /**
         * Java 类型
         */
        private String javaType;

        /**
         * 注释（字段中文名）
         */
        private String comment;
    }

}

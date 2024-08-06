package com.cong.sqldog.model.dto.fieldinfo;

import lombok.Data;

/**
 * 字段信息
 */
@Data
public class ContentJson {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否为空
     */
    private Boolean notNull;

    /**
     * 注释
     */
    private String comment;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 是否自增
     */
    private Boolean autoIncrement;

    /**
     * 模拟数据类型
     */
    private String mockType;

    /**
     * 模拟规则
     */
    private String mockParams;

    /**
     * 字段更新动作
     */
    private String onUpdate;

}
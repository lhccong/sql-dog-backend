package com.cong.sqldog.model.dto.fieldinfo;

import com.cong.sqldog.infrastructure.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询字段信息请求
 *
 * @Author 香香
 * @Date 2024 08 06 01 25
 **/
@Data
public class FieldInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段信息（json）
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 序列化 id
     */
    private static final long serialVersionUID = 1L;
}

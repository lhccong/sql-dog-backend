package com.cong.sqldog.model.dto.fieldinfo;

import com.cong.sqldog.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 序列化 id
     */
    private static final long serialVersionUID = 1L;
}

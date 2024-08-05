package com.cong.sqldog.model.dto.tableinfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建表信息请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TableInfoAddRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容（JSON 数组）
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


    @Serial
    private static final long serialVersionUID = 1L;
}
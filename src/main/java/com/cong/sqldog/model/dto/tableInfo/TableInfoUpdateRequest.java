package com.cong.sqldog.model.dto.tableInfo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新表信息请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TableInfoUpdateRequest implements Serializable {

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

    /**
     * 创建人id
     */
    private Long userId;

    /**
     *  序列化版本标识，用于保证序列化和反序列化的兼容性
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
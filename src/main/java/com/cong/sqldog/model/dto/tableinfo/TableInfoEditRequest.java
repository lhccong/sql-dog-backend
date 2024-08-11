package com.cong.sqldog.model.dto.tableinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑表信息请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TableInfoEditRequest implements Serializable {

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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
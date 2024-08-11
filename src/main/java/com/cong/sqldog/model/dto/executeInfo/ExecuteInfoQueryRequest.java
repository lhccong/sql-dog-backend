package com.cong.sqldog.model.dto.executeInfo;

import com.cong.sqldog.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询SQL执行记录请求
 *
 * @author tian
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExecuteInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 执行SQL的内容
     */
    private String sqlContent;

    /**
     * SQL分析结果
     */
    private String sqlAnalyzeResult;

    /**
     * 审核状态（0-待审核, 1-审核通过, 2-审核不通过）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 创建用户 id
     */
    private Long userId;

    @Serial
    private static final long serialVersionUID = 1L;
}
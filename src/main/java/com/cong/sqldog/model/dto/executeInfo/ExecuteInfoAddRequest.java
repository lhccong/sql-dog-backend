package com.cong.sqldog.model.dto.executeInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建SQL执行记录请求
 *
 * @author tian
 */
@Data
public class ExecuteInfoAddRequest implements Serializable {

    /**
     * 执行SQL的内容
     */
    private String sqlContent;

    /**
     * SQL分析结果
     */
    private String sqlAnalyzeResult;

    /**
     * 审核状态（0：待审核 1：审核通过 2：审核不通过）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    
    @Serial
    private static final long serialVersionUID = 1L;
}
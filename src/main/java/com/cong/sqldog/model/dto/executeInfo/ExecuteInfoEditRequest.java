package com.cong.sqldog.model.dto.executeInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑SQL执行记录请求
 *
 * @author tian
 */
@Data
public class ExecuteInfoEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 执行sql的内容
     */
    private String sqlContent;

    /**
     * sql分析结果
     */
    private String sqlAnalyzeResult;

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    
    @Serial
    private static final long serialVersionUID = 1L;
}
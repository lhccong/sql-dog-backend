package com.cong.sqldog.model.dto.executeInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新SQL执行记录请求
 *
 * @author tian
 */
@Data
public class ExecuteInfoUpdateRequest implements Serializable {

    private Long id;

    private String sqlContent;

    private String sqlAnalyzeResult;

    private Integer reviewStatus;

    private String reviewMessage;

    private Long userId;
    
    private static final long serialVersionUID = 1L;
}
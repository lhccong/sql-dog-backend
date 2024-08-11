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

    private Long id;

    private String sqlContent;

    private String sqlAnalyzeResult;

    private Integer reviewStatus;

    private String reviewMessage;

    private Long userId;
    
    @Serial
    private static final long serialVersionUID = 1L;
}
package com.cong.sqldog.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审核请求
 * @author shing
 */
@Data
public class ReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    @Serial
    private static final long serialVersionUID = 1L;
}

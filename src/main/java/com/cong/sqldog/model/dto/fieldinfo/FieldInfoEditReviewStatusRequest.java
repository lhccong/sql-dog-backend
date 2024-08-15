package com.cong.sqldog.model.dto.fieldinfo;

import lombok.Data;

/**
 * 更改字段状态信息请求
 *
 * @Author 香香
 * @Date 2024-08-15 16:14
 **/
@Data
public class FieldInfoEditReviewStatusRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 字段审核状态
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

}

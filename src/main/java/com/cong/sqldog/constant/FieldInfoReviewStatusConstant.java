package com.cong.sqldog.constant;

/**
 * 字段信息状态常量
 *
 * @Author 香香
 * @Date 2024-08-06 22:21
 **/
public interface FieldInfoReviewStatusConstant {

    /**
     * 待审核
     */
    Integer AWAITING_REVIEW = 0;

    /**
     * 通过
     */
    Integer PASSED = 1;

    /**
     * 拒绝
     */
    Integer REJECTED = 2;
}

package com.cong.sqldog.model.dto.topiclevel;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新关卡题目请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TopicLevelUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关卡中文名
     */
    private String title;

    /**
     * 初始化 SQL
     */
    private String initSQL;

    /**
     * 关卡教程
     */
    private String mdContent;

    /**
     * 关卡初始化后默认执行的语句
     */
    private String defaultSQL;

    /**
     * 上一个关卡 id
     */
    private Long preLevelId;

    /**
     * 下一个关卡 id
     */
    private Long nextLevelId;

    /**
     * 关卡标准答案
     */
    private String answer;

    /**
     * 关卡提示
     */
    private String hint;

    /**
     * 关卡类别，custom 自定义、system 系统
     */
    private String type;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    @Serial
    private static final long serialVersionUID = 1L;
}
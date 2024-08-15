package com.cong.sqldog.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 关卡题目
 *
 * @TableName topic_level
 */
@TableName(value = "topic_level")
@Data
public class TopicLevel implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 关卡教程 Markdown 文档
     */
    private String mdContent;

    /**
     * 关卡初始化后默认执行的语句，一般是查询全表
     */
    private String defaultSQL;

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
     * 状态(0-待审核,1-通过,2-拒绝)
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 上一关卡的 id，没有则为 0
     */
    private Long preLevelId;

    /**
     * 下一关卡的 id，没有则为 0
     */
    private Long nextLevelId;

    /**
     * 创建用户 id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
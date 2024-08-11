package com.cong.sqldog.model.dto.topiclevel;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑关卡题目请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TopicLevelEditRequest implements Serializable {

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

    @Serial
    private static final long serialVersionUID = 1L;
}
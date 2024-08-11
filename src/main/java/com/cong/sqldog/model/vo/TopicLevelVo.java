package com.cong.sqldog.model.vo;

import com.cong.sqldog.model.entity.TopicLevel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 关卡题目视图
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TopicLevelVo implements Serializable {

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

    /**
     * 上一个关卡 id
     */
    private Long preLevelId;

    /**
     * 下一个关卡 id
     */
    private Long nextLevelId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @return TopicLevel
     */
    public static TopicLevel voToObj(TopicLevelVo topicLevelVO) {
        if (topicLevelVO == null) {
            return null;
        }
        TopicLevel topicLevel = new TopicLevel();
        BeanUtils.copyProperties(topicLevelVO, topicLevel);
        return topicLevel;
    }

    /**
     * 对象转封装类
     *
     * @return TopicLevelVo
     */
    public static TopicLevelVo objToVo(TopicLevel topicLevel) {
        if (topicLevel == null) {
            return null;
        }
        TopicLevelVo topicLevelVO = new TopicLevelVo();
        BeanUtils.copyProperties(topicLevel, topicLevelVO);
        return topicLevelVO;
    }
}

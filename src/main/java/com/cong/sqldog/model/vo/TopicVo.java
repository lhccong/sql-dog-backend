package com.cong.sqldog.model.vo;

import com.cong.sqldog.model.entity.TopicLevel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 关卡题目视图
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TopicVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关卡中文名
     */
    private String title;

    /**
     * 关卡类别，custom 自定义、system 系统
     */
    private String type;

    /**
     * 封装类转对象
     *
     * @return TopicLevel
     */
    public static TopicLevel voToObj(TopicVo topicLevelVO) {
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
    public static TopicVo objToVo(TopicLevel topicLevel) {
        if (topicLevel == null) {
            return null;
        }
        TopicVo topicLevelVO = new TopicVo();
        BeanUtils.copyProperties(topicLevel, topicLevelVO);
        return topicLevelVO;
    }
}

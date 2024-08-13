package com.cong.sqldog.mapper;

import com.cong.sqldog.model.dto.topiclevel.TopicQueryRequest;
import com.cong.sqldog.model.entity.TopicLevel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cong.sqldog.model.vo.TopicVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author LBC
 * @description 针对表【topic_level(关卡题目)】的数据库操作Mapper
 * @createDate 2024-08-06 18:44:53
 * @Entity com.cong.sqldog.model.entity.TopicLevel
 */
public interface TopicLevelMapper extends BaseMapper<TopicLevel> {

    /**
     * 按页面选择主题级别
     *
     * @param topicQueryRequest 主题查询请求
     * @return {@link List }<{@link TopicVo }>
     */
    List<TopicVo> selectTopicLevelsByPage(@Param("topicQueryRequest") TopicQueryRequest topicQueryRequest);
}





package com.cong.sqldog.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.model.dto.topiclevel.*;
import com.cong.sqldog.model.entity.TopicLevel;
import com.cong.sqldog.model.vo.TopicLevelVo;
import com.cong.sqldog.model.vo.TopicVo;

/**
 * 关卡题目服务
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
public interface TopicLevelService extends IService<TopicLevel> {

    /**
     * 校验数据
     *
     * @param topicLevel 数据
     * @param add        对创建的数据进行校验
     */
    void validTopicLevel(TopicLevel topicLevel, boolean add);

    /**
     * 获取查询条件
     *
     * @param topicLevelQueryRequest 查询条件
     * @return QueryWrapper
     */
    QueryWrapper<TopicLevel> getQueryWrapper(TopicLevelQueryRequest topicLevelQueryRequest);

    /**
     * 获取题目查询条件
     */
    QueryWrapper<TopicLevel> getTopicQueryWrapper(TopicQueryRequest topicQueryRequest);

    /**
     * 获取关卡题目列表封装
     *
     * @param topicLevel 关卡题目实体
     * @return TopicLevelVo
     */
    TopicLevelVo getTopicLevelVo(TopicLevel topicLevel);

    /**
     * 获取题目封装
     */
    TopicVo getTopicVo(TopicLevel topicLevel);

    /**
     * 分页获取关卡题目列表封装
     *
     * @param topicLevelPage 分页数据
     * @return Page<TopicLevelVo>
     */
    Page<TopicLevelVo> getTopicLevelVoPage(Page<TopicLevel> topicLevelPage);

    /**
     * 分页获取题目封装
     *
     * @param topicLevelPage 分页数据
     * @return Page<TopicLevelVo>
     */
    Page<TopicVo> getTopicVoPage(Page<TopicLevel> topicLevelPage);

    /**
     * 新增表信息
     */
    long addTopicLevel(TopicLevelAddRequest topicLevelAddRequest);

    /**
     * 删除表信息
     */
    boolean deleteTopicLevel(DeleteRequest deleteRequest);

    /**
     * 编辑表信息
     */
    boolean editTopicLevel(TopicLevelEditRequest topicLevelEditRequest);

    /**
     * 更新表信息
     */
    boolean updateTopicLevel(TopicLevelUpdateRequest updateRequest);

    /**
     * 根据 id 获取表信息（封装类）
     */
    TopicLevelVo getTopicLevelVoById(long id);

    /**
     * 分页获取表信息
     */
    Page<TopicLevel> listTopicLevelByPage(TopicLevelQueryRequest topicLevelQueryRequest);

    /**
     * 分页获取关卡题目列表（封装类）
     */
    Page<TopicLevelVo> listTopicLevelVoByPage(TopicLevelQueryRequest topicLevelQueryRequest);

    /**
     * 分页获取我的表信息列表（封装类）
     */
    Page<TopicLevelVo> listMyTopicLevelVoByPage(TopicLevelQueryRequest topicLevelQueryRequest);

    /**
     * 分页获取关卡题目（封装类）
     */
    Page<TopicVo> listTopicVoByPage(TopicQueryRequest topicQueryRequest);
}

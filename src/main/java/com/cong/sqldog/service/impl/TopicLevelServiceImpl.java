package com.cong.sqldog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.mapper.TopicLevelMapper;
import com.cong.sqldog.model.dto.topiclevel.*;
import com.cong.sqldog.model.entity.TopicLevel;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.enums.TopicTypeEnum;
import com.cong.sqldog.model.vo.TopicLevelVo;
import com.cong.sqldog.model.vo.TopicVo;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.TopicLevelService;
import com.cong.sqldog.service.UserService;
import com.cong.sqldog.utils.SqlUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 关卡题目服务实现
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Service
@Slf4j
public class TopicLevelServiceImpl extends ServiceImpl<TopicLevelMapper, TopicLevel> implements TopicLevelService {

    @Resource
    private UserService userService;
    @Resource
    private TopicLevelMapper topicLevelMapper;


    /**
     * 校验数据
     */
    @Override
    public void validTopicLevel(TopicLevel topicLevel, boolean add) {
        if (topicLevel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String initSQL = topicLevel.getInitSQL();
        String title = topicLevel.getTitle();
        String mdContent = topicLevel.getMdContent();
        // 创建时，所有参数必须非空
        if (add && StringUtils.isAnyBlank(title, initSQL, mdContent)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(title) && title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(initSQL) && initSQL.length() > 20000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "SQL 语句过长");
        }
        if (StringUtils.isNotBlank(mdContent) && mdContent.length() > 20000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文档内容过长");
        }
    }

    /**
     * 获取查询条件
     */
    @Override
    public QueryWrapper<TopicLevel> getQueryWrapper(TopicLevelQueryRequest topicLevelQueryRequest) {
        QueryWrapper<TopicLevel> queryWrapper = new QueryWrapper<>();
        if (topicLevelQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = topicLevelQueryRequest.getId();
        String title = topicLevelQueryRequest.getTitle();
        String mdContent = topicLevelQueryRequest.getMdContent();
        String searchText = topicLevelQueryRequest.getSearchText();
        Long userId = topicLevelQueryRequest.getUserId();
        String sortField = topicLevelQueryRequest.getSortField();
        String sortOrder = topicLevelQueryRequest.getSortOrder();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like(CommonConstant.TITLE, searchText).or().like("mdContent", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), CommonConstant.TITLE, title);
        queryWrapper.like(StringUtils.isNotBlank(mdContent), "mdContent", mdContent);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目查询条件
     */
    public QueryWrapper<TopicLevel> getTopicQueryWrapper(TopicQueryRequest topicQueryRequest) {
        QueryWrapper<TopicLevel> topicQueryWrapper = new QueryWrapper<>();
        if (topicQueryRequest == null) {
            return topicQueryWrapper;
        }
        // 从对象中取值
        Long id = topicQueryRequest.getId();
        String title = topicQueryRequest.getTitle();
        String searchText = topicQueryRequest.getSearchText();
        String type = topicQueryRequest.getType();
        String sortField = topicQueryRequest.getSortField();
        String sortOrder = topicQueryRequest.getSortOrder();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            topicQueryWrapper.and(qw -> qw.like(CommonConstant.TITLE, searchText));
        }
        // 模糊查询
        topicQueryWrapper.like(StringUtils.isNotBlank(title), CommonConstant.TITLE, title);
        // 精确查询
        topicQueryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        topicQueryWrapper.eq(ObjectUtils.isNotEmpty(type), "type", type);
        // 排序规则
        topicQueryWrapper.orderBy(SqlUtils.validSortField(sortField),
                CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return topicQueryWrapper;
    }

    /**
     * 获取题目关卡封装
     *
     * @param topicLevel return TopicLevelVo
     */
    @Override
    public TopicLevelVo getTopicLevelVo(TopicLevel topicLevel) {
        // 对象转封装类
        TopicLevelVo topicLevelVo = TopicLevelVo.objToVo(topicLevel);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = topicLevel.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        topicLevelVo.setUser(userVO);
        // endregion

        return topicLevelVo;
    }

    /**
     * 获取题目封装
     */
    @Override
    public TopicVo getTopicVo(TopicLevel topicLevel) {
        return TopicVo.objToVo(topicLevel);
    }

    /**
     * 分页关卡题目列表封装
     *
     * @param topicLevelPage 分页数据
     *                       return Page<TopicLevelVo>
     */
    @Override
    public Page<TopicLevelVo> getTopicLevelVoPage(Page<TopicLevel> topicLevelPage) {
        List<TopicLevel> topicLevelList = topicLevelPage.getRecords();
        Page<TopicLevelVo> topicLevelVoPage = new Page<>(topicLevelPage.getCurrent(), topicLevelPage.getSize(), topicLevelPage.getTotal());
        if (CollUtil.isEmpty(topicLevelList)) {
            return topicLevelVoPage;
        }
        // 对象列表 => 封装对象列表
        List<TopicLevelVo> topicLevelVoList = topicLevelList.stream().map(TopicLevelVo::objToVo).toList();

        //  可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = topicLevelList.stream().map(TopicLevel::getUserId).collect(Collectors.toSet());
        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 填充信息
        topicLevelVoList.forEach(topicLevelVo -> {
            Long userId = topicLevelVo.getUserId();
            if (userId != null && userIdUserMap.containsKey(userId)) {
                User user = userIdUserMap.get(userId);
                topicLevelVo.setUser(userService.getUserVO(user));
            }
        });
        // endregion

        topicLevelVoPage.setRecords(topicLevelVoList);
        return topicLevelVoPage;
    }

    /**
     * 分页题目封装
     *
     * @param topicLevelPage 分页数据
     * @return Page<TopicVo>
     */
    @Override
    public Page<TopicVo> getTopicVoPage(Page<TopicLevel> topicLevelPage) {
        List<TopicLevel> topicLevelList = topicLevelPage.getRecords();
        Page<TopicVo> topicVoPage = new Page<>(topicLevelPage.getCurrent(), topicLevelPage.getSize(), topicLevelPage.getTotal());
        if (CollUtil.isEmpty(topicLevelList)) {
            return topicVoPage;
        }
        // 对象列表 => 封装对象列表
        List<TopicVo> topicVoList = topicLevelList.stream().map(TopicVo::objToVo).toList();
        topicVoPage.setRecords(topicVoList);
        return topicVoPage;
    }

    /**
     * 分页获取关卡题目表信息（仅管理员可用）
     */
    @Override
    public Page<TopicLevel> listTopicLevelByPage(TopicLevelQueryRequest topicLevelQueryRequest) {
        long current = topicLevelQueryRequest.getCurrent();
        long size = topicLevelQueryRequest.getPageSize();
        // 查询数据库
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(topicLevelQueryRequest));
    }

    /**
     * 新增关卡题目
     */
    @Override
    public long addTopicLevel(TopicLevelAddRequest topicLevelAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(topicLevelAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 在此处将实体类和 DTO 进行转换
        TopicLevel topicLevel = new TopicLevel();
        BeanUtils.copyProperties(topicLevelAddRequest, topicLevel);

        // 数据校验
        this.validTopicLevel(topicLevel, true);

        // 填充默认值

        // 根据请求中的类型或用户角色类型设置关卡类型
        if (topicLevelAddRequest.getType() != null) {
            topicLevel.setType(topicLevelAddRequest.getType());
        } else {
            if (userService.isAdmin()) {
                topicLevel.setType(TopicTypeEnum.SYSTEM.getValue());
            } else {
                topicLevel.setType(TopicTypeEnum.CUSTOM.getValue());
            }
        }
        // 写入数据库
        boolean result = this.save(topicLevel);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 获取新生成的关卡 id
        long newTopicLevelId = topicLevel.getId();

        // 根据当前关卡 id，设置上一关和下一关的 id
        topicLevel.setPreLevelId(newTopicLevelId - 1);
        topicLevel.setNextLevelId(newTopicLevelId + 1);

        // 更新关卡信息
        result = this.updateById(topicLevel);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 id
        return newTopicLevelId;
    }

    /**
     * 删除关卡题目
     */
    @Override
    public boolean deleteTopicLevel(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        TopicLevel oldTopicLevel = this.getById(id);
        ThrowUtils.throwIf(oldTopicLevel == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!oldTopicLevel.getId().equals(user.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 编辑关卡题目（给用户使用）
     */
    @Override
    public boolean editTopicLevel(TopicLevelEditRequest topicLevelEditRequest) {
        if (topicLevelEditRequest == null || topicLevelEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //  在此处将实体类和 DTO 进行转换
        TopicLevel topicLevel = new TopicLevel();
        BeanUtils.copyProperties(topicLevelEditRequest, topicLevel);
        // 数据校验
        this.validTopicLevel(topicLevel, false);
        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = topicLevelEditRequest.getId();
        TopicLevel oldTopicLevel = this.getById(id);
        ThrowUtils.throwIf(oldTopicLevel == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldTopicLevel.getId().equals(loginUser.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = this.updateById(topicLevel);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 更新关卡题目（仅管理员可用）
     */
    @Override
    public boolean updateTopicLevel(TopicLevelUpdateRequest topicLevelUpdateRequest) {
        if (topicLevelUpdateRequest == null || topicLevelUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //  在此处将实体类和 DTO 进行转换
        TopicLevel topicLevel = new TopicLevel();
        BeanUtils.copyProperties(topicLevelUpdateRequest, topicLevel);
        // 数据校验
        this.validTopicLevel(topicLevel, false);
        // 判断是否存在
        long id = topicLevelUpdateRequest.getId();
        TopicLevel oldTopicLevel = this.getById(id);
        ThrowUtils.throwIf(oldTopicLevel == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = this.updateById(topicLevel);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 根据 id 获取关卡题目（封装类）
     */
    @Override
    public TopicLevelVo getTopicLevelVoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        TopicLevel topicLevel = this.getById(id);
        ThrowUtils.throwIf(topicLevel == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return this.getTopicLevelVo(topicLevel);
    }

    /**
     * 分页获取关卡题目列表（封装类）
     */
    @Override
    public Page<TopicLevelVo> listTopicLevelVoByPage(TopicLevelQueryRequest topicLevelQueryRequest) {
        long current = topicLevelQueryRequest.getCurrent();
        long size = topicLevelQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TopicLevel> topicLevelPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(topicLevelQueryRequest));
        // 获取封装类
        return this.getTopicLevelVoPage(topicLevelPage);
    }


    /**
     * 分页获取当前登录用户创建的关卡题目列表（封装类）
     */
    @Override
    public Page<TopicLevelVo> listMyTopicLevelVoByPage(TopicLevelQueryRequest topicLevelQueryRequest) {
        ThrowUtils.throwIf(topicLevelQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser();
        topicLevelQueryRequest.setUserId(loginUser.getId());
        long current = topicLevelQueryRequest.getCurrent();
        long size = topicLevelQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TopicLevel> topicLevelPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(topicLevelQueryRequest));
        // 获取封装类
        return this.getTopicLevelVoPage(topicLevelPage);
    }


    /**
     * 分页获取关卡题目（封装类）
     */
    @Override
    public Page<TopicVo> listTopicVoByPage(TopicQueryRequest topicQueryRequest) {
        long size = topicQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        List<TopicVo> topicVoList = topicLevelMapper.selectTopicLevelsByPage(topicQueryRequest);
        Page<TopicVo> topicVoPage = new Page<>(topicQueryRequest.getCurrent(), topicQueryRequest.getPageSize(), this.count(this.getTopicQueryWrapper(topicQueryRequest)));
        topicVoPage.setRecords(topicVoList);

        return topicVoPage;
    }

}

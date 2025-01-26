package com.cong.sqldog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.infrastructure.common.DeleteRequest;
import com.cong.sqldog.infrastructure.common.ErrorCode;
import com.cong.sqldog.infrastructure.common.ReviewRequest;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.infrastructure.exception.BusinessException;
import com.cong.sqldog.infrastructure.exception.ThrowUtils;
import com.cong.sqldog.infrastructure.mapper.TableInfoMapper;
import com.cong.sqldog.model.dto.tableinfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.domain.user.entity.User;
import com.cong.sqldog.model.enums.ReviewStatusEnum;
import com.cong.sqldog.model.vo.TableInfoVo;
import com.cong.sqldog.interfaces.vo.user.UserVO;
import com.cong.sqldog.service.TableInfoService;
import com.cong.sqldog.service.UserService;
import com.cong.sqldog.infrastructure.utils.SqlUtils;
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
 * 表信息服务实现
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Service
@Slf4j
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements TableInfoService {


    @Resource
    private UserService userService;


    /**
     * 校验数据
     */
    @Override
    public void validTableInfo(TableInfo tableInfo, boolean add) {
        if (tableInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = tableInfo.getContent();
        String name = tableInfo.getName();
        Integer reviewStatus = tableInfo.getReviewStatus();
        // 创建时，所有参数必须非空
        if (add && StringUtils.isAnyBlank(name, content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(name) && name.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 20000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (reviewStatus != null && !ReviewStatusEnum.getValues().contains(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     */
    @Override
    public QueryWrapper<TableInfo> getQueryWrapper(TableInfoQueryRequest tableInfoQueryRequest) {
        QueryWrapper<TableInfo> queryWrapper = new QueryWrapper<>();
        if (tableInfoQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = tableInfoQueryRequest.getId();
        String content = tableInfoQueryRequest.getContent();
        String name = tableInfoQueryRequest.getName();
        String searchText = tableInfoQueryRequest.getSearchText();
        String sortField = tableInfoQueryRequest.getSortField();
        String sortOrder = tableInfoQueryRequest.getSortOrder();
        Long userId = tableInfoQueryRequest.getUserId();
        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
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
     * 分页获取表信息
     */
    @Override
    public Page<TableInfo> listTableInfoByPage(TableInfoQueryRequest tableInfoQueryRequest) {
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 查询数据库
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(tableInfoQueryRequest));
    }

    /**
     * 获取表信息封装
     */
    @Override
    public TableInfoVo getTableInfoVo(TableInfo tableInfo) {
        // 对象转封装类
        TableInfoVo tableInfoVo = TableInfoVo.objToVo(tableInfo);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = tableInfo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        tableInfoVo.setUser(userVO);
        // endregion

        return tableInfoVo;
    }

    /**
     * 分页获取表信息封装
     *
     * @param tableInfoPage 分页对象
     * @return 分页对象
     */
    @Override
    public Page<TableInfoVo> getTableInfoVoPage(Page<TableInfo> tableInfoPage) {
        List<TableInfo> tableInfoList = tableInfoPage.getRecords();
        Page<TableInfoVo> tableInfoVoPage = new Page<>(tableInfoPage.getCurrent(), tableInfoPage.getSize(), tableInfoPage.getTotal());
        if (CollUtil.isEmpty(tableInfoList)) {
            return tableInfoVoPage;
        }
        // 对象列表 => 封装对象列表
        List<TableInfoVo> tableInfoVoList = tableInfoList.stream().map(TableInfoVo::objToVo).toList();

        //  可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = tableInfoList.stream().map(TableInfo::getUserId).collect(Collectors.toSet());
        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        // 填充信息
        tableInfoVoList.forEach(topicLevelVo -> {
            Long userId = topicLevelVo.getUserId();
            if (userId != null && userIdUserMap.containsKey(userId)) {
                User user = userIdUserMap.get(userId);
                topicLevelVo.setUser(userService.getUserVO(user));
            }
        });
        // endregion

        tableInfoVoPage.setRecords(tableInfoVoList);
        return tableInfoVoPage;
    }

    /**
     * 新增表信息
     */
    @Override
    public long addTableInfo(TableInfoAddRequest tableInfoAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(tableInfoAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 实体类和 DTO 转换
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoAddRequest, tableInfo);

        // 将 content 转换为 JSON 字符串
        String content = tableInfoAddRequest.getContent();
        tableInfo.setContent(JSONUtil.toJsonStr(content));

        // 数据校验
        this.validTableInfo(tableInfo, true);

        // 填充默认值

        // 写入数据库
        boolean result = this.save(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 ID
        return tableInfo.getId();
    }

    /**
     * 删除表信息
     */
    @Override
    public boolean deleteTableInfo(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        TableInfo oldTableInfo = this.getById(id);
        ThrowUtils.throwIf(oldTableInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldTableInfo.getId().equals(user.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 编辑表信息
     */
    @Override
    public boolean editTableInfo(TableInfoEditRequest tableInfoEditRequest) {
        if (tableInfoEditRequest == null || tableInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoEditRequest, tableInfo);
        // 数据校验
        this.validTableInfo(tableInfo, false);
        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = tableInfoEditRequest.getId();
        TableInfo oldTableInfo = getById(id);
        ThrowUtils.throwIf(oldTableInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldTableInfo.getId().equals(loginUser.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = updateById(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 更新表信息
     */
    @Override
    public boolean updateTableInfo(TableInfoUpdateRequest tableInfoUpdateRequest) {
        if (tableInfoUpdateRequest == null || tableInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoUpdateRequest, tableInfo);
        // 数据校验
        this.validTableInfo(tableInfo, false);
        // 判断是否存在
        long id = tableInfoUpdateRequest.getId();
        TableInfo oldTableInfo = getById(id);
        ThrowUtils.throwIf(oldTableInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = updateById(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 根据 id 获取表信息（封装类）
     */
    @Override
    public TableInfoVo getTableInfoVoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        TableInfo tableInfo = this.getById(id);
        ThrowUtils.throwIf(tableInfo == null, ErrorCode.NOT_FOUND_ERROR, "未找到对应的表信息");
        // 获取封装类
        return this.getTableInfoVo(tableInfo);
    }


    /**
     * 分页获取表信息列表（封装类）
     */
    @Override
    public Page<TableInfoVo> listTableInfoVoByPage(TableInfoQueryRequest tableInfoQueryRequest) {
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TableInfo> tableInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(tableInfoQueryRequest));
        // 获取封装类
        return this.getTableInfoVoPage(tableInfoPage);
    }

    /**
     * 分页获取我的表信息列表（封装类）
     */
    @Override
    public Page<TableInfoVo> listMyTableInfoVoByPage(TableInfoQueryRequest tableInfoQueryRequest) {
        User loginUser = userService.getLoginUser();
        tableInfoQueryRequest.setUserId(loginUser.getId());
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TableInfo> tableInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(tableInfoQueryRequest));
        // 获取封装类
        return this.getTableInfoVoPage(tableInfoPage);
    }

    /**
     * 表信息状态审核
     */
    @Override
    public Boolean doTableInfoReview(ReviewRequest reviewRequest) {
        ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = reviewRequest.getId();
        Integer reviewStatus = reviewRequest.getReviewStatus();
        // 校验
        ReviewStatusEnum reviewStatusEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        TableInfo oldTable = this.getById(id);
        ThrowUtils.throwIf(oldTable == null, ErrorCode.NOT_FOUND_ERROR);
        // 已是该状态
        if (oldTable.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 更新审核状态
        TableInfo tableInfo = new TableInfo();
        tableInfo.setId(id);
        tableInfo.setReviewStatus(reviewStatus);
        tableInfo.setReviewMessage(reviewRequest.getReviewMessage());
        boolean result = this.updateById(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}

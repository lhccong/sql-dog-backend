package com.cong.sqldog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.mapper.TableInfoMapper;
import com.cong.sqldog.model.dto.tableInfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.enums.ReviewStatusEnum;
import com.cong.sqldog.model.vo.TableInfoVO;
import com.cong.sqldog.service.TableInfoService;
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
        if (StringUtils.isNotBlank(content)) {
            if (content.length() > 20000) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
            }
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
        String searchText = tableInfoQueryRequest.getSearchText();
        String sortField = tableInfoQueryRequest.getSortField();
        String sortOrder = tableInfoQueryRequest.getSortOrder();
        Long userId = tableInfoQueryRequest.getUserId();
        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
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
     * 获取表信息封装
     *
     * @param tableInfo 表信息实体
     */
    @Override
    public TableInfoVO getTableInfoVO(TableInfo tableInfo) {
        return TableInfoVO.objToVo(tableInfo);
    }

    /**
     * 分页获取表信息封装
     *
     * @param tableInfoPage 分页对象
     * @return 分页对象
     */
    @Override
    public Page<TableInfoVO> getTableInfoVoPage(Page<TableInfo> tableInfoPage) {
        List<TableInfo> tableInfoList = tableInfoPage.getRecords();
        Page<TableInfoVO> tableInfoVoPage = new Page<>(tableInfoPage.getCurrent(), tableInfoPage.getSize(), tableInfoPage.getTotal());
        if (CollUtil.isEmpty(tableInfoList)) {
            return tableInfoVoPage;
        }
        // 对象列表 => 封装对象列表
        List<TableInfoVO> tableInfoVOList = tableInfoList.stream().map(TableInfoVO::objToVo).collect(Collectors.toList());

        //  可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = tableInfoList.stream().map(TableInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        tableInfoVOList.forEach(tableInfoVO -> {
            Long userId = tableInfoVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            tableInfoVO.setUser(userService.getUserVO(user));
        });
        // endregion

        tableInfoVoPage.setRecords(tableInfoVOList);
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
        User loginUser = userService.getLoginUser();
        tableInfo.setUserId(loginUser.getId());

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
        if (!oldTableInfo.getId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = updateById(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return false;
    }

    /**
     * 更新表信息
     */
    @Override
    public boolean updateTableInfo(TableInfoUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(updateRequest, tableInfo);
        // 数据校验
        this.validTableInfo(tableInfo, false);
        // 判断是否存在
        long id = updateRequest.getId();
        TableInfo oldTableInfo = getById(id);
        ThrowUtils.throwIf(oldTableInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = updateById(tableInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}

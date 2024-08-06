package com.cong.sqldog.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.constant.FieldInfoReviewStatusConstant;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.mapper.FieldInfoMapper;
import com.cong.sqldog.model.dto.fieldinfo.*;
import com.cong.sqldog.model.entity.*;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.enums.ReviewStatusEnum;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.FieldInfoService;
import com.cong.sqldog.service.UserService;
import com.cong.sqldog.utils.SqlUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author 香香
 * @description 针对表【field_info(字段信息)】的数据库操作Service实现
 * @createDate 2024-08-06 01:21:20
 */
@Service
public class FieldInfoServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo>
        implements FieldInfoService {

    @Resource
    private UserService userService;


    /**
     * 添加字段信息
     *
     * @param fieldInfoAddRequest
     * @return
     */
    @Override
    public long addFieldInfo(FieldInfoAddRequest fieldInfoAddRequest) {

        // 参数校验
        ThrowUtils.throwIf(fieldInfoAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 实体类和 DTO 转换
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoAddRequest, fieldInfo);
        fieldInfo.setContent(JSONUtil.toJsonStr(fieldInfoAddRequest.getContent()));

        // 数据校验
        this.validFieldInfo(fieldInfo, true);

        // 填充默认值
        User loginUser = userService.getLoginUser();
        fieldInfo.setUserId(loginUser.getId());

        // 写入数据库
        boolean result = this.save(fieldInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 ID
        return fieldInfo.getId();
    }

    /**
     * 删除字段信息
     *
     * @param deleteRequest
     * @return
     */
    @Override
    public boolean deleteFieldInfo(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        FieldInfo oldFieldInfo = this.getById(id);
        ThrowUtils.throwIf(oldFieldInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldFieldInfo.getId().equals(user.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }


    /**
     * 分页获取字段信息封装
     *
     * @param fieldInfoPage 分页对象
     * @return 分页对象
     */
    @Override
    public Page<FieldInfoVO> getFieldInfoVoPage(Page<FieldInfo> fieldInfoPage) {
        List<FieldInfo> fieldInfoList = fieldInfoPage.getRecords();
        Page<FieldInfoVO> fieldInfoVoPage = new Page<>(fieldInfoPage.getCurrent(), fieldInfoPage.getSize(), fieldInfoPage.getTotal());
        if (CollUtil.isEmpty(fieldInfoList)) {
            return fieldInfoVoPage;
        }
        List<FieldInfoVO> fieldInfoVOList;
        // 对象列表 => 封装对象列表
        fieldInfoVOList = fieldInfoList.stream().map(FieldInfoVO::objToVo).toList();

        //  可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = fieldInfoList.stream().map(FieldInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        fieldInfoVOList.forEach(fieldInfoVO -> {
            Long userId = fieldInfoVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            fieldInfoVO.setUserVO(userService.getUserVO(user));
        });
        // endregion

        fieldInfoVoPage.setRecords(fieldInfoVOList);
        return fieldInfoVoPage;
    }

    /**
     * 编辑表信息（给用户使用）
     *
     * @param fieldInfoEditRequest
     * @return
     */
    @Override
    public boolean editFieldInfo(FieldInfoEditRequest fieldInfoEditRequest) {
        if (fieldInfoEditRequest == null || fieldInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        long id = fieldInfoEditRequest.getId();
        FieldInfo oldFieldInfo = getById(id);
        ThrowUtils.throwIf(oldFieldInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断状态是否异常
        if (oldFieldInfo.getReviewStatus().equals(FieldInfoReviewStatusConstant.PASSED) ||
                oldFieldInfo.getReviewStatus().equals(FieldInfoReviewStatusConstant.REJECTED)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"只能编辑待审批的字段信息");
        }

        // 在此处将实体类和 DTO 进行转换
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoEditRequest, fieldInfo);
        fieldInfo.setContent(JSONUtil.toJsonStr(fieldInfoEditRequest.getContent()));

        // 数据校验
        this.validFieldInfo(fieldInfo, false);
        User loginUser = userService.getLoginUser();

        // 仅本人或管理员可编辑
        if (!oldFieldInfo.getId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = updateById(fieldInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 更新字段信息（给管理员使用）
     *
     * @param fieldInfoUpdateRequest
     * @return
     */
    @Override
    public boolean editFieldInfoByAdmin(FieldInfoUpdateRequest fieldInfoUpdateRequest) {
        if (fieldInfoUpdateRequest == null || fieldInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        long id = fieldInfoUpdateRequest.getId();
        FieldInfo oldFieldInfo = getById(id);
        ThrowUtils.throwIf(oldFieldInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 在此处将实体类和 DTO 进行转换
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoUpdateRequest, fieldInfo);
        fieldInfo.setContent(JSONUtil.toJsonStr(fieldInfoUpdateRequest.getContent()));

        // 数据校验
        this.validFieldInfo(fieldInfo, false);
        User loginUser = userService.getLoginUser();

        // 仅本人或管理员可编辑
        if (!oldFieldInfo.getId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = updateById(fieldInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    // region 工具方法


    /**
     * 数据校验
     *
     * @param fieldInfo
     * @param result
     */
    public void validFieldInfo(FieldInfo fieldInfo, boolean result) {

        if (fieldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = fieldInfo.getName();
        String fieldName = fieldInfo.getFieldName();
        String content = fieldInfo.getContent();
        Integer reviewStatus = fieldInfo.getReviewStatus();
        String reviewMessage = fieldInfo.getReviewMessage();

        // 创建时，所有参数必须非空
        if (result && StringUtils.isAnyBlank(name, content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(name) && name.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(fieldName) && fieldName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段名称过长");
        }
        if (StringUtils.isNotBlank(content) && (content.length() > 20000)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段信息过长");
        }
        if (StringUtils.isNotBlank(reviewMessage) && (reviewMessage.length() > 20000)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核信息过长");
        }
        if (reviewStatus != null && !ReviewStatusEnum.getValues().contains(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

    }


    /**
     * 获取字段信息封装
     *
     * @param fieldInfo 字段信息实体
     */
    @Override
    public FieldInfoVO getFieldInfoVO(FieldInfo fieldInfo, UserVO userVo) {
        return FieldInfoVO.objToVo(fieldInfo, userVo);
    }


    /**
     * 获取查询条件
     *
     * @param fieldInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<FieldInfo> getQueryWrapper(FieldInfoQueryRequest fieldInfoQueryRequest) {
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>();
        if (fieldInfoQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = fieldInfoQueryRequest.getId();
        String fieldName = fieldInfoQueryRequest.getFieldName();
        String content = fieldInfoQueryRequest.getContent();
        String searchText = fieldInfoQueryRequest.getSearchText();
        String sortField = fieldInfoQueryRequest.getSortField();
        String sortOrder = fieldInfoQueryRequest.getSortOrder();
        Long userId = fieldInfoQueryRequest.getUserId();
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
        queryWrapper.eq(ObjectUtils.isNotEmpty(fieldName), "fieldName", fieldName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }





    // endregion
}





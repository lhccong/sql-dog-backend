package com.cong.sqldog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.constant.FieldInfoReviewStatusConstant;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.mapper.FieldInfoMapper;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoAddRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoEditRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoQueryRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoUpdateRequest;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.enums.ReviewStatusEnum;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.service.FieldInfoService;
import com.cong.sqldog.service.UserService;
import com.cong.sqldog.utils.SqlUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 香香
 * @description 针对表【field_info(字段信息)】的数据库操作Service实现
 * @createDate 2024-08-06 01:21:20
 */
@Service
@Slf4j
public class FieldInfoServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo>
        implements FieldInfoService {

    @Resource
    private UserService userService;


    /**
     * 添加字段信息
     *
     * @param fieldInfoAddRequest 创建字段信息请求
     * @return 字段 id
     */
    @Override
    public long addFieldInfo(FieldInfoAddRequest fieldInfoAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(fieldInfoAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 校验字段信息是否合法
        this.processFieldInfo(fieldInfoAddRequest.getContent(), TableSchema.Field.class);

        // 实体类和 DTO 转换
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoAddRequest, fieldInfo);
        fieldInfo.setContent(JSONUtil.toJsonStr(fieldInfoAddRequest.getContent()));

        // 数据校验
        this.validFieldInfo(fieldInfo, true);

        // 写入数据库
        boolean result = this.save(fieldInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 ID
        return fieldInfo.getId();
    }

    /**
     * 删除字段信息
     *
     * @param deleteRequest 删除请求
     * @return 是否删除成功
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
        if (!oldFieldInfo.getUserId().equals(user.getId()) && !userService.isAdmin()) {
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

        fieldInfoVoPage.setRecords(fieldInfoVOList);
        return fieldInfoVoPage;
    }

    /**
     * 编辑表信息（给用户使用）
     *
     * @param fieldInfoEditRequest 编辑字段信息请求
     * @return 是否编辑成功
     */
    @Override
    public boolean editFieldInfo(FieldInfoEditRequest fieldInfoEditRequest) {
        if (fieldInfoEditRequest == null || fieldInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验字段信息是否合法
        this.processFieldInfo(fieldInfoEditRequest.getContent(), TableSchema.Field.class);

        // 判断是否存在
        long id = fieldInfoEditRequest.getId();
        FieldInfo oldFieldInfo = getById(id);
        ThrowUtils.throwIf(oldFieldInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断状态是否异常
        if (oldFieldInfo.getReviewStatus().equals(FieldInfoReviewStatusConstant.PASSED) ||
                oldFieldInfo.getReviewStatus().equals(FieldInfoReviewStatusConstant.REJECTED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能编辑待审批的字段信息");
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
     * @param fieldInfoUpdateRequest 更新字段信息请求
     * @return 是否编辑成功
     */
    @Override
    public boolean updateFieldInfoByAdmin(FieldInfoUpdateRequest fieldInfoUpdateRequest) {
        if (fieldInfoUpdateRequest == null || fieldInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验字段信息是否合法
        this.processFieldInfo(fieldInfoUpdateRequest.getContent(), TableSchema.Field.class);

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

    /**
     * 根据 id 获取字段信息
     *
     * @param id 字段信息 id
     * @return 字段信息
     */
    @Override
    public FieldInfoVO getFieldInfoVoById(long id) {

        // 如果 id 小于等于 0，报错请求参数错误
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取 id 查询字段信息
        FieldInfo fieldInfo = this.getById(id);
        ThrowUtils.throwIf(fieldInfo == null, ErrorCode.NOT_FOUND_ERROR);

        return this.getFieldInfoVO(fieldInfo);
    }

    /**
     * 分页获取新段信息列表
     *
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfo>
     */
    @Override
    public Page<FieldInfo> listFieldInfoByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 查询数据库
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(fieldInfoQueryRequest));
    }

    /**
     * 分页获取字段信息列表
     *
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfoVO>
     */
    @Override
    public Page<FieldInfoVO> listFieldInfoVoByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FieldInfo> tableInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(fieldInfoQueryRequest).eq("reviewStatus", ReviewStatusEnum.PASS.getValue()));
        // 获取封装类
        return this.getFieldInfoVoPage(tableInfoPage);
    }

    /**
     * 分页获取我的字段信息列表
     *
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfoVO>
     */
    @Override
    public Page<FieldInfoVO> listMyFieldInfoVOByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        ThrowUtils.throwIf(fieldInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser();
        fieldInfoQueryRequest.setUserId(loginUser.getId());
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FieldInfo> tableInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(fieldInfoQueryRequest));
        // 获取封装类
        return this.getFieldInfoVoPage(tableInfoPage);
    }

    // region 工具方法


    /**
     * 数据校验
     *
     * @param fieldInfo 字段信息
     * @param result    void
     */
    public void validFieldInfo(FieldInfo fieldInfo, boolean result) {
        // 参数不能为 null
        if (fieldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取字段信息
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
    public FieldInfoVO getFieldInfoVO(FieldInfo fieldInfo) {
        return FieldInfoVO.objToVo(fieldInfo);
    }


    /**
     * 获取查询条件
     *
     * @param fieldInfoQueryRequest 查询字段请求信息
     * @return QueryWrapper<FieldInfo>
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

    /**
     * 处理字段信息并验证字段值
     *
     * @param jsonString JSON 字符串
     * @param fieldClass 目标字段类
     * @throws BusinessException 如果包含未知字段或值类型不匹配
     */
    public void processFieldInfo(String jsonString, Class<?> fieldClass) throws BusinessException {
        // 解析 JSON 字符串为 JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(jsonString);

        // 获取有效字段及其类型
        Map<String, Class<?>> validFieldTypes = getValidFieldTypes(fieldClass);

        // 校验字段
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            String key = stringObjectEntry.getKey();
            if (!validFieldTypes.containsKey(key)) {
                // 发现未知字段，立即抛出异常
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "包含未知字段: " + key);
            }

            // 获取字段类型
            Class<?> fieldType = validFieldTypes.get(key);
            Object value = stringObjectEntry.getValue();

            // 验证值类型是否匹配字段类型
            if (!isValidType(value, fieldType)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段 " + key + " 的值类型不匹配，期望类型: " + fieldType.getSimpleName());
            }
        }

    }

    /**
     * 动态获取有效字段及其类型的映射
     *
     * @param clazz 要分析的类
     * @return Map<String, Class < ?>>
     */
    private static Map<String, Class<?>> getValidFieldTypes(Class<?> clazz) {
        Map<String, Class<?>> fieldTypes = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            fieldTypes.put(field.getName(), field.getType());
        }
        return fieldTypes;
    }

    /**
     * 验证值的类型是否匹配字段的类型
     *
     * @param value     值
     * @param fieldType 字段类型
     * @return 是否匹配
     */
    private boolean isValidType(Object value, Class<?> fieldType) {
        // 如果字段是基本类型且值为 null，则不匹配
        if (value == null) {
            return !fieldType.isPrimitive();
        }

        // 根据字段类型进行匹配
        if (fieldType.isInstance(value)) {
            return true;
        }

        if (fieldType == Integer.class || fieldType == int.class) {
            return value instanceof Number;
        }
        if (fieldType == Long.class || fieldType == long.class) {
            return value instanceof Number;
        }
        if (fieldType == Double.class || fieldType == double.class) {
            return value instanceof Number;
        }
        if (fieldType == Boolean.class || fieldType == boolean.class) {
            return value instanceof Boolean;
        }
        if (fieldType == String.class) {
            return value instanceof String;
        }

        return false;
    }

    // endregion
}





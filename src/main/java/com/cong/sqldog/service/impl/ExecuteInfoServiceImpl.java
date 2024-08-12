package com.cong.sqldog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ReviewRequest;
import com.cong.sqldog.constant.CommonConstant;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.mapper.ExecuteInfoMapper;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoAddRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoEditRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoQueryRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoUpdateRequest;
import com.cong.sqldog.model.entity.ExecuteInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.enums.ReviewStatusEnum;
import com.cong.sqldog.model.vo.ExecuteInfoVO;
import com.cong.sqldog.service.ExecuteInfoService;
import com.cong.sqldog.service.UserService;
import com.cong.sqldog.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SQL执行记录服务实现
 *
 * @author tian

 */
@Service
@Slf4j
public class ExecuteInfoServiceImpl extends ServiceImpl<ExecuteInfoMapper, ExecuteInfo> implements ExecuteInfoService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param executeInfo SQL执行记录
     * @param add  创建SQL执行记录的标识，true-创建，false-更新
     */
    @Override
    public void validExecuteInfo(ExecuteInfo executeInfo, boolean add) {
        ThrowUtils.throwIf(executeInfo == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String sqlContent = executeInfo.getSqlContent();
        Integer reviewStatus = executeInfo.getReviewStatus();
        Long userId = executeInfo.getUserId();
        // 创建数据时，参数不能为空，用户id要大于0
        if(add && (StringUtils.isAnyBlank(sqlContent) || reviewStatus == null || userId <= 0) ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(sqlContent)) {
            ThrowUtils.throwIf(sqlContent.length() > 512, ErrorCode.PARAMS_ERROR, "执行SQL的内容过长");
        }
        if (reviewStatus != null) {
            ThrowUtils.throwIf(!ReviewStatusEnum.getValues().contains(reviewStatus), ErrorCode.PARAMS_ERROR, "审核状态不符合规范");
        }
        if (userId !=null) {
            ThrowUtils.throwIf(userId <= 0, ErrorCode.PARAMS_ERROR, "用户id错误");
        }
    }

    /**
     * 获取查询条件
     *
     * @param executeInfoQueryRequest SQL执行记录请求
     * @return QueryWrapper<ExecuteInfo> SQL执行记录查询条件
     */
    @Override
    public QueryWrapper<ExecuteInfo> getQueryWrapper(ExecuteInfoQueryRequest executeInfoQueryRequest) {
        QueryWrapper<ExecuteInfo> queryWrapper = new QueryWrapper<>();
        if (executeInfoQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = executeInfoQueryRequest.getId();
        String sqlContent = executeInfoQueryRequest.getSqlContent();
        String sqlAnalyzeResult = executeInfoQueryRequest.getSqlAnalyzeResult();
        String searchText = executeInfoQueryRequest.getSearchText();
        String sortField = executeInfoQueryRequest.getSortField();
        String sortOrder = executeInfoQueryRequest.getSortOrder();
        Long userId = executeInfoQueryRequest.getUserId();
        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("sqlContent", searchText).or().like("sqlAnalyzeResult", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(sqlContent), "sqlContent", sqlContent);
        queryWrapper.like(StringUtils.isNotBlank(sqlAnalyzeResult), "sqlAnalyzeResult", sqlAnalyzeResult);
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
     * 获取SQL执行记录封装
     *
     * @param executeInfo SQL执行记录
     * @return ExecuteInfoVO SQL执行记录封装
     */
    @Override
    public ExecuteInfoVO getExecuteInfoVO(ExecuteInfo executeInfo) {
        return ExecuteInfoVO.objToVo(executeInfo);
    }

    /**
     * 分页获取SQL执行记录封装
     *
     * @param executeInfoPage 分页对象
     * @return 分页对象
     */
    @Override
    public Page<ExecuteInfoVO> getExecuteInfoVOPage(Page<ExecuteInfo> executeInfoPage) {
        List<ExecuteInfo> executeInfoList = executeInfoPage.getRecords();
        Page<ExecuteInfoVO> executeInfoVOPage = new Page<>(executeInfoPage.getCurrent(), executeInfoPage.getSize(), executeInfoPage.getTotal());
        if (CollUtil.isEmpty(executeInfoList)) {
            return executeInfoVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ExecuteInfoVO> executeInfoVOList = executeInfoList.stream().map(ExecuteInfoVO::objToVo).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = executeInfoList.stream().map(ExecuteInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        executeInfoVOList.forEach(executeInfoVO -> {
            Long userId = executeInfoVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            executeInfoVO.setUser(userService.getUserVO(user));
        });
        // endregion

        executeInfoVOPage.setRecords(executeInfoVOList);
        return executeInfoVOPage;
    }

    /**
     * 创建SQL执行记录
     *
     * @param executeInfoAddRequest SQL执行记录创建请求
     * @return SQL执行记录ID
     */
    @Override
    public long addExecuteInfo(ExecuteInfoAddRequest executeInfoAddRequest) {

        //参数校验
        ThrowUtils.throwIf(executeInfoAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取登录用户
        User loginUser = userService.getLoginUser();
        // 仅本人和管理员可以添加
        Long userId= executeInfoAddRequest.getUserId();
        if (!userId.equals(loginUser.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //实体类和DTO转换
        ExecuteInfo executeInfo = new ExecuteInfo();
        BeanUtils.copyProperties(executeInfoAddRequest, executeInfo);

        //数据校验
        this.validExecuteInfo(executeInfo, true);

        //写入数据库
        boolean result = this.save(executeInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        //返回新写入的数据 ID
        return executeInfo.getId();
    }

    /**
     * 删除SQL执行记录
     *
     * @param deleteRequest SQL执行记录删除请求
     * @return boolean 是否删除成功
     */
    @Override
    public Boolean deleteExecuteInfo(DeleteRequest deleteRequest) {

        //校验请求是否为空和id是否正确
        if(deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser();

        // 判断要删除的信息是否存在
        long id = deleteRequest.getId();
        ExecuteInfo oldExecuteInfo = this.getById(id);
        ThrowUtils.throwIf(oldExecuteInfo == null, ErrorCode.NOT_FOUND_ERROR);

        //仅本人或管理员可以删除
        if ((!oldExecuteInfo.getUserId().equals(loginUser.getId())) && (!userService.isAdmin())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }

    /**
     * 编辑SQL执行记录
     *
     * @param executeInfoEditRequest SQL执行记录编辑请求
     * @return boolean 是否编辑成功
     */
    @Override
    public Boolean editExecuteInfo(ExecuteInfoEditRequest executeInfoEditRequest) {
        //判断请求是否为空和id是否正确
        if(executeInfoEditRequest == null || executeInfoEditRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //实体类和DTO转换
        ExecuteInfo executeInfo = new ExecuteInfo();
        BeanUtils.copyProperties(executeInfoEditRequest, executeInfo);

        //数据校验
        this.validExecuteInfo(executeInfo, false);
        //判断要编辑的信息是否存在
        long id = executeInfoEditRequest.getId();
        ExecuteInfo oldExecuteInfo = this.getById(id);
        ThrowUtils.throwIf(oldExecuteInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //获取登录用户,仅本人或管理员可编辑
        User loginUser = userService.getLoginUser();
        if (!oldExecuteInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //操作数据库
        boolean result = this.updateById(executeInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 更新SQL执行记录（仅管理员可用）
     *
     * @param executeInfoUpdateRequest SQL执行记录更新请求
     * @return boolean 是否更新成功
     */
    @Override
    public Boolean updateExecuteInfo(ExecuteInfoUpdateRequest executeInfoUpdateRequest) {
        //判断请求是否为空和id是否正确
        if(executeInfoUpdateRequest == null || executeInfoUpdateRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //实体类和DTO转换
        ExecuteInfo executeInfo = new ExecuteInfo();
        BeanUtils.copyProperties(executeInfoUpdateRequest, executeInfo);

        //数据校验
        this.validExecuteInfo(executeInfo, false);
        //判断要更新的信息是否存在
        long id = executeInfoUpdateRequest.getId();
        ExecuteInfo oldExecuteInfo = this.getById(id);
        ThrowUtils.throwIf(oldExecuteInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //操作数据库
        boolean result = this.updateById(executeInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 根据id获取SQL执行记录（封装类）
     * @param id SQL执行记录id
     * @return ExecuteInfo SQL执行记录
     */
    @Override
    public ExecuteInfoVO getExecuteInfoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ExecuteInfo executeInfo = this.getById(id);
        ThrowUtils.throwIf(executeInfo == null, ErrorCode.NOT_FOUND_ERROR, "未找到对应的SQL执行记录");
        // 获取封装类
        return this.getExecuteInfoVO(executeInfo);

    }

    /**
     * 分页获取SQL执行记录列表（仅管理员可用）
     *
     * @param executeInfoQueryRequest SQL执行记录查询请求
     * @return Page<ExecuteInfo> SQL执行记录列表
     */
    @Override
    public Page<ExecuteInfo> listExecuteInfoByPage(ExecuteInfoQueryRequest executeInfoQueryRequest){
        long current = executeInfoQueryRequest.getCurrent();
        long size = executeInfoQueryRequest.getPageSize();
        // 查询数据库
        //返回结果
        return this.page(new Page<>(current, size),
                this.getQueryWrapper(executeInfoQueryRequest));


    }

    /**
     * 分页获取SQL执行记录列表（封装类）
     *
     * @param executeInfoQueryRequest SQL执行记录查询请求
     * @return Page<ExecuteInfoVO> SQL执行记录列表(封装类)
     */
    @Override
    public Page<ExecuteInfoVO> listExecuteInfoVoByPage(ExecuteInfoQueryRequest executeInfoQueryRequest) {
        long current = executeInfoQueryRequest.getCurrent();
        long size = executeInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ExecuteInfo> executeInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(executeInfoQueryRequest));
        // 获取封装类
        return this.getExecuteInfoVOPage(executeInfoPage);
    }

    /**
     * 分页获取当前登录用户创建的SQL执行记录列表(封装类)
     *
     * @param executeInfoQueryRequest SQL执行记录查询请求
     * @return Page<ExecuteInfo> SQL执行记录列表
     */
    @Override
    public Page<ExecuteInfoVO> listMyExecuteInfoVOByPage(ExecuteInfoQueryRequest executeInfoQueryRequest) {
        ThrowUtils.throwIf(executeInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser();
        executeInfoQueryRequest.setUserId(loginUser.getId());
        long current = executeInfoQueryRequest.getCurrent();
        long size = executeInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ExecuteInfo> executeInfoPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(executeInfoQueryRequest));
        // 获取封装类
        return this.getExecuteInfoVOPage(executeInfoPage);
    }

    /**
     * 审核SQL执行记录
     *
     * @param reviewRequest 审核请求
     * @return boolean 是否审核成功
     */
    @Override
    public Boolean reviewExecuteInfo(ReviewRequest reviewRequest) {
        ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = reviewRequest.getId();
        Integer reviewStatus = reviewRequest.getReviewStatus();
        // 参数校验
        ReviewStatusEnum reviewStatusEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
        if(id ==null || reviewStatusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断要审核的信息是否存在
        ExecuteInfo oldExecuteInfo = this.getById(id);
        ThrowUtils.throwIf(oldExecuteInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //如果审核状态传入的状态一样，则不进行审核
        if(oldExecuteInfo.getReviewStatus().equals(reviewStatus)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复审核");
        }
        //更新审核状态
        ExecuteInfo executeInfo = new ExecuteInfo();
        executeInfo.setId(id);
        executeInfo.setReviewStatus(reviewStatus);
        executeInfo.setReviewMessage(reviewRequest.getReviewMessage());
        boolean result = this.updateById(executeInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;

    }

}

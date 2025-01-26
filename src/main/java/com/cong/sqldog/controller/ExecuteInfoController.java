package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.infrastructure.common.BaseResponse;
import com.cong.sqldog.infrastructure.common.DeleteRequest;
import com.cong.sqldog.infrastructure.common.ResultUtils;
import com.cong.sqldog.infrastructure.common.ReviewRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoAddRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoEditRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoQueryRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoUpdateRequest;
import com.cong.sqldog.model.entity.ExecuteInfo;
import com.cong.sqldog.model.vo.ExecuteInfoVO;
import com.cong.sqldog.service.ExecuteInfoService;
import com.cong.sqldog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * SQL执行记录接口
 *
 * @author tian

 */
@RestController
@RequestMapping("/executeInfo")
@Slf4j
//@Tag(name = "SQL执行记录接口")
public class ExecuteInfoController {

    @Resource
    private ExecuteInfoService executeInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建SQL执行记录
     *
     * @param executeInfoAddRequest 创建SQL执行记录请求
     * @return {@link BaseResponse }<{@link Long }>
     */
    @PostMapping("/add")
    @Operation(summary = "创建SQL执行记录")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Long> addExecuteInfo(@RequestBody ExecuteInfoAddRequest executeInfoAddRequest) {
        long newExecuteInfoId = executeInfoService.addExecuteInfo(executeInfoAddRequest);
        return ResultUtils.success(newExecuteInfoId);
    }

    /**
     * 删除SQL执行记录
     *
     * @param deleteRequest 删除SQL执行记录请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/delete")
    @Operation(summary = "删除SQL执行记录")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> deleteExecuteInfo(@RequestBody DeleteRequest deleteRequest) {
        Boolean result = executeInfoService.deleteExecuteInfo(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 编辑SQL执行记录（给用户使用）
     *
     * @param executeInfoEditRequest 编辑SQL执行记录请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑SQL执行记录（给用户使用）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> editExecuteInfo(@RequestBody ExecuteInfoEditRequest executeInfoEditRequest) {
        Boolean result = executeInfoService.editExecuteInfo(executeInfoEditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新SQL执行记录（仅管理员可用）
     *
     * @param executeInfoUpdateRequest 更新SQL执行记录请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/update")
    @Operation(summary = "更新SQL执行记录（仅管理员可用）")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateExecuteInfo(@RequestBody ExecuteInfoUpdateRequest executeInfoUpdateRequest) {
        Boolean result = executeInfoService.updateExecuteInfo(executeInfoUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取SQL执行记录（封装类）
     *
     * @param id SQL执行记录 id
     * @return {@link BaseResponse }<{@link ExecuteInfoVO }>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取SQL执行记录（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<ExecuteInfoVO> getExecuteInfoVOById(long id) {
        return ResultUtils.success(executeInfoService.getExecuteInfoById(id));
    }

    /**
     * 分页获取SQL执行记录列表（仅管理员可用）
     *
     * @param executeInfoQueryRequest 分页获取SQL执行记录列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link ExecuteInfo }>>
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "分页获取SQL执行记录列表（仅管理员可用）")
    public BaseResponse<Page<ExecuteInfo>> listExecuteInfoByPage(@RequestBody ExecuteInfoQueryRequest executeInfoQueryRequest) {
        return ResultUtils.success(executeInfoService.listExecuteInfoByPage(executeInfoQueryRequest));
    }

    /**
     * 分页获取SQL执行记录列表（封装类）
     *
     * @param executeInfoQueryRequest 分页获取SQL执行记录列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link ExecuteInfoVO }>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取SQL执行记录列表（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<ExecuteInfoVO>> listExecuteInfoVOByPage(@RequestBody ExecuteInfoQueryRequest executeInfoQueryRequest) {
        return ResultUtils.success(executeInfoService.listExecuteInfoVoByPage(executeInfoQueryRequest));
    }

    /**
     * 分页获取当前登录用户创建的SQL执行记录列表
     *
     * @param executeInfoQueryRequest 分页获取SQL执行记录列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link ExecuteInfoVO }>>
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取当前登录用户创建的SQL执行记录列表")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<ExecuteInfoVO>> listMyExecuteInfoVOByPage(@RequestBody ExecuteInfoQueryRequest executeInfoQueryRequest) {
        return ResultUtils.success(executeInfoService.listMyExecuteInfoVOByPage(executeInfoQueryRequest));
    }

    /**
     * SQL执行记录表审核状态修改(仅管理员可用)
     */
    @PostMapping("/review")
    @Operation(summary = "SQL执行记录表审核状态修改(仅管理员可用)")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doReviewExecuteInfo(@RequestBody ReviewRequest reviewRequest) {
        Boolean result = executeInfoService.reviewExecuteInfo(reviewRequest);
        return ResultUtils.success(result);
    }

}

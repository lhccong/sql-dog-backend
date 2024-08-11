package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.common.ReviewRequest;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.model.dto.tableinfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.model.vo.TableInfoVo;
import com.cong.sqldog.service.TableInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 表信息接口
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@RestController
@RequestMapping("/tableInfo")
@Slf4j
//@Tag(name = "表信息接口")
public class TableInfoController {

    @Resource
    private TableInfoService tableInfoService;

    // region 增删改查

    /**
     * 创建表信息
     *
     * @param tableInfoAddRequest 创建表信息请求
     * @return {@link BaseResponse }<{@link Long }>
     */
    @PostMapping("/add")
    @Operation(summary = "创建表信息")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Long> addTableInfo(@RequestBody TableInfoAddRequest tableInfoAddRequest) {
        long newTableInfoId = tableInfoService.addTableInfo(tableInfoAddRequest);
        return ResultUtils.success(newTableInfoId);
    }

    /**
     * 删除表信息
     *
     * @param deleteRequest 删除表信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/delete")
    @Operation(summary = "删除表信息")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> deleteTableInfo(@RequestBody DeleteRequest deleteRequest) {
        boolean result = tableInfoService.deleteTableInfo(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取表信息（封装类）
     *
     * @param id 表信息 id
     * @return {@link BaseResponse }<{@link TableInfoVo }>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取表信息（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<TableInfoVo> getTableInfoVoById(long id) {
        return ResultUtils.success(tableInfoService.getTableInfoVoById(id));
    }

    /**
     * 分页获取表信息列表（仅管理员可用）
     *
     * @param tableInfoQueryRequest 分页获取表信息列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TableInfo }>>
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "分页获取表信息列表（仅管理员可用）")
    public BaseResponse<Page<TableInfo>> listTableInfoByPage(@RequestBody TableInfoQueryRequest tableInfoQueryRequest) {
        return ResultUtils.success(tableInfoService.listTableInfoByPage(tableInfoQueryRequest));
    }

    /**
     * 分页获取表信息列表（封装类）
     *
     * @param tableInfoQueryRequest 分页获取表信息列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TableInfoVo }>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取表信息列表（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<TableInfoVo>> listTableInfoVoByPage(@RequestBody TableInfoQueryRequest tableInfoQueryRequest) {
        return ResultUtils.success(tableInfoService.listTableInfoVoByPage(tableInfoQueryRequest));
    }

    /**
     * 分页获取当前登录用户创建的表信息列表
     *
     * @param tableInfoQueryRequest 分页获取表信息列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TableInfoVo }>>
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取当前登录用户创建的表信息列表")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<TableInfoVo>> listMyTableInfoVOByPage(@RequestBody TableInfoQueryRequest tableInfoQueryRequest) {
        return ResultUtils.success(tableInfoService.listMyTableInfoVoByPage(tableInfoQueryRequest));
    }

    /**
     * 更新表信息（仅管理员可用）
     *
     * @param tableInfoUpdateRequest 更新表信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/update")
    @Operation(summary = "更新表信息（仅管理员可用）")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTableInfo(@RequestBody TableInfoUpdateRequest tableInfoUpdateRequest) {
        boolean result = tableInfoService.updateTableInfo(tableInfoUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 编辑表信息（给用户使用）
     *
     * @param tableInfoEditRequest 编辑表信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑表信息（给用户使用）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> editTableInfo(@RequestBody TableInfoEditRequest tableInfoEditRequest) {
        boolean result = tableInfoService.editTableInfo(tableInfoEditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 表信息状态审核（仅管理员可用）
     */
    @PostMapping("/review")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "表信息状态审核（仅管理员可用）")
    public BaseResponse<Boolean> doTableInfoReview(@RequestBody ReviewRequest reviewRequest) {
        boolean result = tableInfoService.doTableInfoReview(reviewRequest);
        return ResultUtils.success(result);
    }
}

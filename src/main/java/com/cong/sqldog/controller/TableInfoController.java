package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.model.dto.tableInfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.TableInfoVO;
import com.cong.sqldog.service.TableInfoService;
import com.cong.sqldog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "表信息接口")
public class TableInfoController {

    @Resource
    private TableInfoService tableInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建表信息
     *
     * @param tableInfoAddRequest 创建表信息请求
     * @return {@link BaseResponse }<{@link Long }>
     */
    @PostMapping("/add")
    @Operation(summary = "创建表信息")
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
    public BaseResponse<Boolean> deleteTableInfo(@RequestBody DeleteRequest deleteRequest) {
        boolean result = tableInfoService.deleteTableInfo(deleteRequest);
        return ResultUtils.success(result);
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
     * 根据 id 获取表信息（封装类）
     *
     * @param id 表信息 id
     * @return {@link BaseResponse }<{@link TableInfoVO }>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取表信息（封装类）")
    public BaseResponse<TableInfoVO> getTableInfoVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        TableInfo tableInfo = tableInfoService.getById(id);
        ThrowUtils.throwIf(tableInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(tableInfoService.getTableInfoVO(tableInfo, request));
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
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 查询数据库
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(current, size),
                tableInfoService.getQueryWrapper(tableInfoQueryRequest));
        return ResultUtils.success(tableInfoPage);
    }

    /**
     * 分页获取表信息列表（封装类）
     *
     * @param tableInfoQueryRequest 分页获取表信息列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TableInfoVO }>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取表信息列表（封装类）")
    public BaseResponse<Page<TableInfoVO>> listTableInfoVOByPage(@RequestBody TableInfoQueryRequest tableInfoQueryRequest) {
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(current, size),
                tableInfoService.getQueryWrapper(tableInfoQueryRequest));
        // 获取封装类
        return ResultUtils.success(tableInfoService.getTableInfoVOPage(tableInfoPage));
    }

    /**
     * 分页获取当前登录用户创建的表信息列表
     *
     * @param tableInfoQueryRequest 分页获取表信息列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TableInfoVO }>>
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取当前登录用户创建的表信息列表")
    public BaseResponse<Page<TableInfoVO>> listMyTableInfoVOByPage(@RequestBody TableInfoQueryRequest tableInfoQueryRequest) {
        ThrowUtils.throwIf(tableInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser();
        tableInfoQueryRequest.setId(loginUser.getId());
        long current = tableInfoQueryRequest.getCurrent();
        long size = tableInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(current, size),
                tableInfoService.getQueryWrapper(tableInfoQueryRequest));
        // 获取封装类
        return ResultUtils.success(tableInfoService.getTableInfoVOPage(tableInfoPage));
    }

    /**
     * 编辑表信息（给用户使用）
     *
     * @param tableInfoEditRequest 编辑表信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑表信息（给用户使用）")
    public BaseResponse<Boolean> editTableInfo(@RequestBody TableInfoEditRequest tableInfoEditRequest) {
        boolean result = tableInfoService.editTableInfo(tableInfoEditRequest);
        return ResultUtils.success(result);
    }

    // endregion
}

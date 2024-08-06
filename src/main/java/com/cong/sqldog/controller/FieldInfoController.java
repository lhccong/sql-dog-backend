package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoAddRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoEditRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoQueryRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoUpdateRequest;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.FieldInfoService;
import com.cong.sqldog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 字段信息接口
 *
 * @author 香香
 */
@RestController
@RequestMapping("/fieldInfo")
@Slf4j
@Tag(name = "字段信息接口")
public class FieldInfoController {

    @Resource
    private FieldInfoService fieldInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建字段信息
     *
     * @param fieldInfoAddRequest 创建字段信息请求
     * @return {@link BaseResponse }<{@link Long }>
     */
    @PostMapping("/add")
    @Operation(summary = "创建字段信息")
    public BaseResponse<Long> addFieldInfo(@RequestBody FieldInfoAddRequest fieldInfoAddRequest) {
        long newFieldInfoId = fieldInfoService.addFieldInfo(fieldInfoAddRequest);
        return ResultUtils.success(newFieldInfoId);
    }

    /**
     * 删除字段信息
     *
     * @param deleteRequest 删除字段信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/delete")
    @Operation(summary = "删除字段信息")
    public BaseResponse<Boolean> deleteFieldInfo(@RequestBody DeleteRequest deleteRequest) {
        boolean result = fieldInfoService.deleteFieldInfo(deleteRequest);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取字段信息（封装类）
     *
     * @param id 字段信息 id
     * @return {@link BaseResponse }<{@link FieldInfoVO }>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取字段信息（封装类）")
    public BaseResponse<FieldInfoVO> getFieldInfoVoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        FieldInfo fieldInfo = fieldInfoService.getById(id);
        ThrowUtils.throwIf(fieldInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取当前登录用户信息
        UserVO userVO = new UserVO();
        User loginUser = userService.getLoginUser();
        BeanUtils.copyProperties(loginUser, userVO);
        // 获取封装类
        return ResultUtils.success(fieldInfoService.getFieldInfoVO(fieldInfo,userVO));
    }

    /**
     * 分页获取字段信息列字段（仅管理员可用）
     *
     * @param fieldInfoQueryRequest 分页获取字段信息列字段请求
     * @return {@link BaseResponse }<{@link Page }<{@link FieldInfo }>>
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "分页获取字段信息列字段（仅管理员可用）")
    public BaseResponse<Page<FieldInfo>> listFieldInfoByPage(@RequestBody FieldInfoQueryRequest fieldInfoQueryRequest) {
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 查询数据库
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(current, size),
                fieldInfoService.getQueryWrapper(fieldInfoQueryRequest));
        return ResultUtils.success(fieldInfoPage);
    }

    /**
     * 分页获取字段信息列字段（封装类）
     *
     * @param fieldInfoQueryRequest 分页获取字段信息列字段请求
     * @return {@link BaseResponse }<{@link Page }<{@link FieldInfoVO }>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取字段信息列字段（封装类）")
    public BaseResponse<Page<FieldInfoVO>> listFieldInfoVoByPage(@RequestBody FieldInfoQueryRequest fieldInfoQueryRequest) {
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(current, size),
                fieldInfoService.getQueryWrapper(fieldInfoQueryRequest));
        // 获取封装类
        return ResultUtils.success(fieldInfoService.getFieldInfoVoPage(fieldInfoPage));
    }

    /**
     * 分页获取当前登录用户创建的字段信息列字段
     *
     * @param fieldInfoQueryRequest 分页获取字段信息列字段请求
     * @return {@link BaseResponse }<{@link Page }<{@link FieldInfoVO }>>
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取当前登录用户创建的字段信息列字段")
    public BaseResponse<Page<FieldInfoVO>> listMyFieldInfoVOByPage(@RequestBody FieldInfoQueryRequest fieldInfoQueryRequest) {
        ThrowUtils.throwIf(fieldInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser();
        fieldInfoQueryRequest.setUserId(loginUser.getId());
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(current, size),
                fieldInfoService.getQueryWrapper(fieldInfoQueryRequest));
        // 获取封装类
        return ResultUtils.success(fieldInfoService.getFieldInfoVoPage(fieldInfoPage));
    }

    /**
     * 编辑字段信息（给用户使用）
     *
     * @param fieldInfoEditRequest 编辑字段信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑字段信息（给用户使用）")
    public BaseResponse<Boolean> editFieldInfo(@RequestBody FieldInfoEditRequest fieldInfoEditRequest) {
        boolean result = fieldInfoService.editFieldInfo(fieldInfoEditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新字段信息（给管理员使用）
     *
     * @param fieldInfoUpdateRequest 更新字段信息请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/update")
    @Operation(summary = "编辑字段信息（给管理员使用）")
    public BaseResponse<Boolean> updateFieldInfo(@RequestBody FieldInfoUpdateRequest fieldInfoUpdateRequest) {
        boolean result = fieldInfoService.editFieldInfoByAdmin(fieldInfoUpdateRequest);
        return ResultUtils.success(result);
    }

    // endregion
}

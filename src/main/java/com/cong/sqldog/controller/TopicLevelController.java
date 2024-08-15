package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.common.ReviewRequest;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.model.dto.topiclevel.*;
import com.cong.sqldog.model.entity.TopicLevel;
import com.cong.sqldog.model.vo.TopicLevelVo;
import com.cong.sqldog.model.vo.TopicVo;
import com.cong.sqldog.service.TopicLevelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 关卡题目接口
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@RestController
@RequestMapping("/topicLevel")
@Slf4j
//@Tag(name = "关卡题目接口")
public class TopicLevelController {

    @Resource
    private TopicLevelService topicLevelService;

    // region 增删改查

    /**
     * 创建关卡题目
     *
     * @param topicLevelAddRequest 创建关卡题目请求
     * @return {@link BaseResponse }<{@link Long }>
     */
    @PostMapping("/add")
    @Operation(summary = "创建关卡题目")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Long> addTopicLevel(@RequestBody TopicLevelAddRequest topicLevelAddRequest) {
        long newTableInfoId = topicLevelService.addTopicLevel(topicLevelAddRequest);
        return ResultUtils.success(newTableInfoId);
    }

    /**
     * 删除关卡题目
     *
     * @param deleteRequest 删除关卡题目请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/delete")
    @Operation(summary = "删除关卡题目")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> deleteTopicLevel(@RequestBody DeleteRequest deleteRequest) {
        boolean result = topicLevelService.deleteTopicLevel(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 编辑关卡题目（给用户使用）
     *
     * @param topicLevelEditRequest 编辑关卡题目请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑关卡题目（给用户使用）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> editTopicLevel(@RequestBody TopicLevelEditRequest topicLevelEditRequest) {
        boolean result = topicLevelService.editTopicLevel(topicLevelEditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新关卡题目（仅管理员可用）
     *
     * @param topicLevelUpdateRequest 更新关卡题目请求
     * @return {@link BaseResponse }<{@link Boolean }>
     */
    @PostMapping("/update")
    @Operation(summary = "更新关卡题目（仅管理员可用）")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTopicLevel(@RequestBody TopicLevelUpdateRequest topicLevelUpdateRequest) {
        boolean result = topicLevelService.updateTopicLevel(topicLevelUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取关卡题目（封装类）
     *
     * @param id 关卡题目 id
     * @return {@link BaseResponse }<{@link TopicLevelVo }>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取关卡题目列表（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<TopicLevelVo> getTopicLevelVoById(long id) {
        // 获取封装类
        return ResultUtils.success(topicLevelService.getTopicLevelVoById(id));
    }

    /**
     * 分页获取关卡题目列表（仅管理员可用）
     *
     * @param topicLevelQueryRequest 分页获取关卡题目列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TopicLevel }>>
     */
    @PostMapping("/list/page")
    @Operation(summary = "分页获取关卡题目列表（仅管理员可用）")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<TopicLevel>> listTopicLevelByPage(@RequestBody TopicLevelQueryRequest topicLevelQueryRequest) {
        // 获取封装类
        return ResultUtils.success(topicLevelService.listTopicLevelByPage(topicLevelQueryRequest));
    }

    /**
     * 分页获取关卡题目列表（封装类）
     *
     * @param topicLevelQueryRequest 分页获取关卡题目列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TopicLevelVo }>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取关卡题目列表（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<TopicLevelVo>> listTopicLevelVoByPage(@RequestBody TopicLevelQueryRequest topicLevelQueryRequest) {
        // 获取封装类
        return ResultUtils.success(topicLevelService.listTopicLevelVoByPage(topicLevelQueryRequest));
    }

    /**
     * 分页获取当前登录用户创建的关卡题目列表
     *
     * @param topicLevelQueryRequest 分页获取关卡题目列表请求
     * @return {@link BaseResponse }<{@link Page }<{@link TopicLevelVo }>>
     */
    @PostMapping("/my/list/page/vo")
    @Operation(summary = "分页获取当前登录用户创建的关卡题目列表")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<TopicLevelVo>> listMyTopicLevelVoByPage(@RequestBody TopicLevelQueryRequest topicLevelQueryRequest) {
        return ResultUtils.success(topicLevelService.listMyTopicLevelVoByPage(topicLevelQueryRequest));
    }

    /**
     * 分页获取题目（封装类）
     *
     * @param topicQueryRequest 分页获取题目请求
     * @return {@link BaseResponse }<{@link Page }<{@link TopicLevelVo }>>
     */
    @PostMapping("/list/page/topic")
    @Operation(summary = "分页获取关卡题目（封装类）")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<TopicVo>> listTopicVoByPage(@RequestBody TopicQueryRequest topicQueryRequest) {
        // 获取封装类
        return ResultUtils.success(topicLevelService.listTopicVoByPage(topicQueryRequest));
    }

    /**
     * 表信息状态审核（仅管理员可用）
     */
    @PostMapping("/review")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "表信息状态审核（仅管理员可用）")
    public BaseResponse<Boolean> doTopicLevelReview(@RequestBody ReviewRequest reviewRequest) {
        boolean result = topicLevelService.doTopicReview(reviewRequest);
        return ResultUtils.success(result);
    }

    // endregion
}

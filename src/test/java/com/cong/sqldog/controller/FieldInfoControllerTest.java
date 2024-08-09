package com.cong.sqldog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.model.dto.fieldinfo.ContentJson;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoAddRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoEditRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoQueryRequest;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.FieldInfoService;
import com.cong.sqldog.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * 字段信息测试类
 *
 * @Author 香香
 * @Date 2024-08-06 18:54
 **/
@SpringBootTest
class FieldInfoControllerTest {

    @Resource
    private FieldInfoService fieldInfoService;

    @Resource
    private UserService userService;

    /**
     * 登录 - 测试
     */
    void login() {
        // 登录
        String userAccount = "xiangxiang";
        String userPassword = "xiangxiang";
        userService.userLogin(userAccount, userPassword);
    }


    /**
     * 创建字段信息 - 测试
     *
     */
    @Test
    void addFieldInfo() {
        // 登录
        login();

        FieldInfoAddRequest fieldInfoAddRequest = new FieldInfoAddRequest();

        // 名称
        fieldInfoAddRequest.setName("name");
        // 字段名
        fieldInfoAddRequest.setFieldName("name");
        ContentJson contentJson = new ContentJson();
        // 字段名
        contentJson.setFieldName("name");
        // 字段类型
        contentJson.setFieldType("String");
        // 默认值
        contentJson.setDefaultValue("zhangsan");
        // 是否为空
        contentJson.setNotNull(false);
        // 注释
        contentJson.setComment("注释");
        // 是否主键
        contentJson.setPrimaryKey(false);
        // 是否自增
        contentJson.setAutoIncrement(false);
        // 模拟数据类型
        contentJson.setMockType("name");
        // 模拟规则
        contentJson.setMockParams("demo");
        // 字段更新动作
        contentJson.setOnUpdate("demo");
        // 字段更新动作
        fieldInfoAddRequest.setContent(contentJson);
        // 状态（0-待审核, 1-通过, 2-拒绝）
        fieldInfoAddRequest.setReviewStatus(0);
        // 审核信息
        fieldInfoAddRequest.setReviewMessage("info1");

        fieldInfoService.addFieldInfo(fieldInfoAddRequest);


    }

    /**
     * 删除字段信息 - 测试
     *
     */
    @Test
    void deleteFieldInfo() {
        // 登录
        login();

        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(1L);
        fieldInfoService.deleteFieldInfo(deleteRequest);
    }

    /**
     * 根据 id 获取字段信息（封装类） - 测试
     *
     */
    @Test
    void getFieldInfoVoById() {
        // 登录
        login();

        // mock
        long id = 1820811949573373953L;
        FieldInfo fieldInfo = fieldInfoService.getById(id);
        ThrowUtils.throwIf(fieldInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        fieldInfoService.getFieldInfoVO(fieldInfo);
    }

    /**
     * 分页获取字段信息列字段（仅管理员可用） - 测试
     *
     */
    @Test
    void listFieldInfoByPage() {
        // 登录（非管路不可操作）
        login();

        FieldInfoQueryRequest fieldInfoQueryRequest = new FieldInfoQueryRequest();
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();

        // 查询数据库
        fieldInfoService.page(new Page<>(current, size),
                fieldInfoService.getQueryWrapper(fieldInfoQueryRequest));
    }

    /**
     * 分页获取字段信息列字段（封装类） - 测试
     *
     */
    @Test
    void listFieldInfoVoByPage() {
        // 登录
        login();

        FieldInfoQueryRequest fieldInfoQueryRequest = new FieldInfoQueryRequest();
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(current, size),
                fieldInfoService.getQueryWrapper(fieldInfoQueryRequest));
        // 获取封装类
        fieldInfoService.getFieldInfoVoPage(fieldInfoPage);
    }

    /**
     * 分页获取当前登录用户创建的字段信息列字段 - 测试
     *
     */
    @Test
    void listMyFieldInfoVOByPage() {
        // 登录
        login();

        FieldInfoQueryRequest fieldInfoQueryRequest = new FieldInfoQueryRequest();
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
        fieldInfoService.getFieldInfoVoPage(fieldInfoPage);
    }

    /**
     * 编辑字段信息（给用户使用）- 测试
     *
     */
    @Test
    void editFieldInfo() {
        // 登录
        login();

        FieldInfoEditRequest editRequest = new FieldInfoEditRequest();
        // mock
        editRequest.setId(1820811949573373953L);
        editRequest.setFieldName("newFieldName");
        fieldInfoService.editFieldInfo(editRequest);
    }
}
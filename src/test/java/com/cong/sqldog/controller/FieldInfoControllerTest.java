package com.cong.sqldog.controller;

import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.*;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.model.dto.fieldinfo.*;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.FieldInfoService;
import com.cong.sqldog.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;


/**
 * 字段信息测试类
 *
 * @Author 香香
 * @Date 2024-08-06 18:54
 **/
@SpringBootTest
class FieldInfoControllerTest extends TestBase {

    @MockBean
    private UserService userService;

    @MockBean
    StpLogic stpLogic;

    /**
     * 模拟已登录用户,
     */
    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1816001696590692353L);
        mockUser.setUserRole("admin");

        // 模拟 getLoginUser 方法返回 mockUser
        Mockito.when(userService.getLoginUser()).thenReturn(mockUser);
        Mockito.when(userService.isAdmin()).thenReturn(true);

        //模拟 userService.getById 方法返回mockUser
        Mockito.when(userService.getById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(stpLogic.getLoginId(any())).thenAnswer(invocation -> mockUser.getId().toString());
        Mockito.when(stpLogic.getLoginId()).thenAnswer(invocation -> mockUser.getId().toString());
    }

    /**
     * 创建字段信息 - 测试
     *
     */
    @Test
    void addFieldInfo() throws Exception {

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
        contentJson.setComment("comment");
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

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/fieldInfo/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.toJsonStr(fieldInfoAddRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());


    }

    /**
     * 删除字段信息 - 测试
     *
     */
    @Test
    void deleteFieldInfo() throws Exception {

        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(1821846185877061634L);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/fieldInfo/delete")
                .content(JSONUtil.toJsonStr(deleteRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }



    /**
     * 编辑字段信息（给用户使用）- 测试
     *
     */
    @Test
    void editFieldInfo() throws Exception {

        FieldInfoEditRequest editRequest = new FieldInfoEditRequest();
        // mock
        editRequest.setId(1820812119325270018L);
        editRequest.setFieldName("newFieldName");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/fieldInfo/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.toJsonStr(editRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 更新字段信息（给管理使用）- 测试
     *
     */
    @Test
    void UpdateFieldInfo() throws Exception {

        FieldInfoUpdateRequest updateRequest = new FieldInfoUpdateRequest();
        // mock
        updateRequest.setId(1820812119325270018L);
        updateRequest.setFieldName("newFieldName");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/fieldInfo/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONUtil.toJsonStr(updateRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }
}
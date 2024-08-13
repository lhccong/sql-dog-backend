package com.cong.sqldog.service;


import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.json.JSONUtil;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ReviewRequest;
import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoAddRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoEditRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoQueryRequest;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.enums.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;

/**
 * ExecuteInfo 单元测试类
 * @author tian
 *
 */
class ExecuteInfoServiceTest extends TestBase {

    @MockBean
    private UserService userService;

    @MockBean
    StpLogic stpLogic;


    /**
     * 模拟已登录用户
     *
     * 1816001696590692353L 为插入数据的用户id
     *
     * 3211234596590692353L 为非插入记录的用户id
     */
    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1816001696590692353L);
        mockUser.setUserRole("admin");


        // 模拟 getLoginUser 方法返回 mockUser
        Mockito.when(userService.getLoginUser()).thenReturn(mockUser);
        // 模拟 isAdmin 方法返回 true/false
        Mockito.when(userService.isAdmin()).thenReturn(false);

        //模拟 userService.getById 方法返回mockUser
        Mockito.when(userService.getById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(stpLogic.getLoginId(any())).thenAnswer(invocation -> mockUser.getId().toString());
        Mockito.when(stpLogic.getLoginId()).thenAnswer(invocation -> mockUser.getId().toString());
    }

    /**
     * SQL执行记录表添加测试
     */
    @Test
    void addTableExecuteInfo() throws Exception {
        // 准备测试数据
        ExecuteInfoAddRequest executeInfoAddRequest = new ExecuteInfoAddRequest();
        String sqlContent = "insert into execute_info(sqlContent) value(insert)";
        Integer reviewStatus = 0;
        executeInfoAddRequest.setSqlContent(sqlContent);
        executeInfoAddRequest.setReviewStatus(reviewStatus);
        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(executeInfoAddRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * SQL执行记录表删除测试
     */
    @Test
    void deleteTableExecuteInfo() throws Exception {
        System.out.println(UserRoleEnum.ADMIN.getValue());
        // 准备测试数据
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(2488L);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/delete")
                        .content(JSONUtil.toJsonStr(deleteRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * SQL执行记录表用户编辑测试
     *
     */
    @Test
    void editTableExecuteInfo() throws Exception {

        // 准备测试数据
        Long id = 2488L;
        String sqlContent = "update execute_info set sqlContent = update execute";
        String sqlAnalyzeResult = "SQL Analyze Result ";
        Integer reviewStatus = 0;
        String reviewMessage="reviewing";

        //给用户编辑请求填入数据
        ExecuteInfoEditRequest executeInfoEditRequest = new ExecuteInfoEditRequest();
        executeInfoEditRequest.setId(id);
        executeInfoEditRequest.setSqlContent(sqlContent);
        executeInfoEditRequest.setSqlAnalyzeResult(sqlAnalyzeResult);
        executeInfoEditRequest.setReviewStatus(reviewStatus);
        executeInfoEditRequest.setReviewMessage(reviewMessage);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/edit")
                        .content(JSONUtil.toJsonStr(executeInfoEditRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }


    /**
     * SQL执行记录表管理员更新测试
     *
     */
    @Test
    void UpdateTableExecuteInfo() throws Exception {

        // 准备测试数据
        Long id = 2488L;
        String sqlContent = "update execute_info set sqlContent = update execute";
        String sqlAnalyzeResult = "SQL Analyze Result";
        Integer reviewStatus = 0;
        String reviewMessage="reviewing";

        //给用户编辑请求填入数据
        ExecuteInfoEditRequest executeInfoEditRequest = new ExecuteInfoEditRequest();
        executeInfoEditRequest.setId(id);
        executeInfoEditRequest.setSqlContent(sqlContent);
        executeInfoEditRequest.setSqlAnalyzeResult(sqlAnalyzeResult);
        executeInfoEditRequest.setReviewStatus(reviewStatus);
        executeInfoEditRequest.setReviewMessage(reviewMessage);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/edit")
                        .content(JSONUtil.toJsonStr(executeInfoEditRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 根据id获取SQL执行记录（封装类）
     * throws Exception 异常
     */
    @Test
    void getTableExecuteInfoById() throws Exception {
        long id = 2488L;
        mockMvc.perform(MockMvcRequestBuilders.get("/executeInfo/get/vo")
                        .param("id", String.valueOf(id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    /**
     * 分页获取SQL执行记录列表（仅管理员可用）
     */

    @Test
    void getTableExecuteInfoByPage() throws Exception {
        ExecuteInfoQueryRequest executeInfoQueryRequest = new ExecuteInfoQueryRequest();
        Long userId = 3211234596590692353L;

        executeInfoQueryRequest.setUserId(userId);
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/list/page")
                        .content(JSONUtil.toJsonStr(executeInfoQueryRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }


    /**
     * 分页获取SQL执行记录列表（封装类）
     */
    @Test
    void getTableExecuteInfoVOByPage() throws Exception {
        ExecuteInfoQueryRequest executeInfoQueryRequest = new ExecuteInfoQueryRequest();
        Long userId = 1816001696590692353L;
        int current = 1;
        int size = 1;
        executeInfoQueryRequest.setUserId(userId);
        executeInfoQueryRequest.setCurrent(current);
        executeInfoQueryRequest.setPageSize(size);
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/list/page/vo")
                        .content(JSONUtil.toJsonStr(executeInfoQueryRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }
    /**
     * 分页获取当前登录用户创建的SQL执行记录列表(封装类)
     */
    @Test
    void getTableExecuteInfoCurrentUserVOByPage() throws Exception {
        ExecuteInfoQueryRequest executeInfoQueryRequest = new ExecuteInfoQueryRequest();
        // 这里就算设置了userId，但是代码逻辑里会获取当前登录用户的userId，所以设置无效。
        Long userId = 3211234596590692353L;
        int current = 2;
        int size = 1;
        executeInfoQueryRequest.setUserId(userId);
        executeInfoQueryRequest.setCurrent(current);
        executeInfoQueryRequest.setPageSize(size);
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/my/list/page/vo")
                        .content(JSONUtil.toJsonStr(executeInfoQueryRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    /**
     * 审核状态修改测试
     */

    @Test
    void doReviewTest() throws Exception {
        // 准备测试数据
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setId(2488L);
        reviewRequest.setReviewStatus(2);
        reviewRequest.setReviewMessage("审核不通过");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/executeInfo/review")
                        .contentType(MediaType.APPLICATION_JSON) // 设置Content-Type为application/json
                        .content(JSONUtil.toJsonStr(reviewRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }
}

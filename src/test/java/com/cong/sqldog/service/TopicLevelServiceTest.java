package com.cong.sqldog.service;

import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.json.JSONUtil;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.model.dto.topiclevel.*;
import com.cong.sqldog.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author shing
 */
class TopicLevelServiceTest extends TestBase {

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
     * 测试 addTopicLevel 方法
     */
    @Test
    void addTopicLevelTest() throws Exception {
        // 准备测试数据
        TopicLevelAddRequest topicLevelAddRequest = new TopicLevelAddRequest();
        topicLevelAddRequest.setTitle("test title");
        topicLevelAddRequest.setInitSQL(JSONUtil.toJsonStr("select * from magic_scores"));
        topicLevelAddRequest.setMdContent("自定义关卡");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelAddRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试 deleteTopicLevel 方法
     */
    @Test
    void deleteTopicLevelTest() throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(100L);
        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/delete")
                        .content(JSONUtil.toJsonStr(deleteRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 测试 editTopicLevel 方法
     */
    @Test
    void editTopicLevelTest() throws Exception {
        // 准备测试数据
        TopicLevelEditRequest topicLevelEditRequest = new TopicLevelEditRequest();
        topicLevelEditRequest.setId(101L);
        topicLevelEditRequest.setTitle("edited title");
        topicLevelEditRequest.setMdContent("编辑关卡内容");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelEditRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));

    }

    /**
     * 测试 updateTopicLevel 方法
     */
    @Test
    void updateTopicLevelTest() throws Exception {
        // 准备测试数据
        TopicLevelUpdateRequest topicLevelUpdateRequest = new TopicLevelUpdateRequest();
        topicLevelUpdateRequest.setId(101L);
        topicLevelUpdateRequest.setTitle("updated title");
        topicLevelUpdateRequest.setMdContent("更新关卡内容");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));

    }

    /**
     * 测试 getTopicLevelById 方法
     */
    @Test
    void getTopicLevelVoByIdTest() throws Exception {

        // 发送查询请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/topicLevel/get/vo")
                        .param("id", String.valueOf(101L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());

    }

    /**
     * 测试 listTopicLevelByPage 方法
     */
    @Test
    void listTopicLevelByPageTest() throws Exception {
        // 准备测试数据
        TopicLevelQueryRequest topicLevelQueryRequest = new TopicLevelQueryRequest();
        // 设置分页参数
        topicLevelQueryRequest.setCurrent(1);  // 一般分页从 1 开始
        topicLevelQueryRequest.setPageSize(10);

        // 发送分页查询请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/list/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelQueryRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试 listTopicLevelVoByPage 方法
     */
    @Test
    void listTopicLevelVoByPageTest() throws Exception {
        // 准备测试数据
        TopicLevelQueryRequest topicLevelQueryRequest = new TopicLevelQueryRequest();
        // 设置分页参数
        topicLevelQueryRequest.setCurrent(1);
        topicLevelQueryRequest.setPageSize(10);

        // 发送分页查询请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/list/page/vo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelQueryRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试 listMyTopicLevelVoByPage 方法
     */
    @Test
    void listMyTopicLevelVoByPageTest() throws Exception {
        // 准备测试数据
        TopicLevelQueryRequest topicLevelQueryRequest = new TopicLevelQueryRequest();
        // 设置分页参数
        topicLevelQueryRequest.setCurrent(1);
        topicLevelQueryRequest.setPageSize(10);

        // 设置用户ID
        topicLevelQueryRequest.setUserId(1818965526718836738L);

        // 发送分页查询请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/my/list/page/vo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicLevelQueryRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试 listTopicVoByPage 方法
     */
    @Test
    void listTopicVoByPageTest() throws Exception {
        TopicQueryRequest topicQueryRequest = new TopicQueryRequest();
        // 设置分页参数
        topicQueryRequest.setPageSize(10);
        topicQueryRequest.setCurrent(0);

        // 发送分页查询请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/topicLevel/list/page/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(topicQueryRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }
}
package com.cong.sqldog.service;

import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.json.JSONUtil;
import com.cong.sqldog.infrastructure.common.DeleteRequest;
import com.cong.sqldog.infrastructure.common.ReviewRequest;
import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.model.dto.tableinfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoUpdateRequest;
import com.cong.sqldog.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;

/**
 * TableInfo 单元测试类
 *
 * @author shing
 */
class TableInfoServiceTest extends TestBase {

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
     * 添加表格信息测试
     */
    @Test
    void addTable() throws Exception {
        // 准备测试数据
        TableInfoAddRequest tableInfoAddRequest = new TableInfoAddRequest();
        String content = "{\"id\":\"12345\",\"name\":\"shing\",\"age\":\"18\"}"; // 修改为对象格式
        tableInfoAddRequest.setContent(JSONUtil.toJsonStr(content));
        tableInfoAddRequest.setName("test3");

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableInfoAddRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty());
    }

    /**
     * 删除表格信息测试
     */
    @Test
    void deleteTable() throws Exception {
        // 准备测试数据
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(1820740567387107329L);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/delete")
                        .content(JSONUtil.toJsonStr(deleteRequest)) // 将整个对象序列化为JSON字符串并作为请求体发送
                        .contentType(MediaType.APPLICATION_JSON)) // 设置请求体的内容类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 编辑表格信息测试
     */
    @Test
    void editTable() throws Exception {
        // 准备测试数据
        TableInfoEditRequest tableInfoEditRequest = new TableInfoEditRequest();
        tableInfoEditRequest.setName("edit测试表");
        tableInfoEditRequest.setId(1820740567387107329L);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableInfoEditRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 更新表格信息测试
     */
    @Test
    void UpdateTable() throws Exception {
        // 准备测试数据
        TableInfoUpdateRequest tableInfoUpdateRequest = new TableInfoUpdateRequest();
        tableInfoUpdateRequest.setName("update测试表");
        tableInfoUpdateRequest.setId(1820740567387107328L);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableInfoUpdateRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 根据 id 获取表格信息测试
     */
    @Test
    void getTableById() throws Exception {
        long id = 1820740567387107329L;
        mockMvc.perform(MockMvcRequestBuilders.get("/tableInfo/get/vo")
                        .param("id", String.valueOf(id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    void doReviewTest() throws Exception {
        // 准备测试数据
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setId(1820740567387107329L);
        reviewRequest.setReviewStatus(2);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/review")
                        .contentType(MediaType.APPLICATION_JSON) // 设置Content-Type为application/json
                        .content(JSONUtil.toJsonStr(reviewRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }
}
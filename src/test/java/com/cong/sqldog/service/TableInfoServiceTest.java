package com.cong.sqldog.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.TestBase;
import com.cong.sqldog.model.dto.tableInfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.vo.LoginUserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * TableInfo 单元测试类
 * @author shing
 */
class TableInfoServiceTest extends TestBase {

    private static final Logger log = LoggerFactory.getLogger(TableInfoServiceTest.class);

    @Resource
    UserService userService;

    /**
     * 获取token
     */
    private String getToken() {
        // 模拟用户登录
        String userAccount = "cong";
        String userPassword = "12345678";
        String token = StpUtil.getTokenValue();
        if (token == null) {
            try {
                LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword);
                // 记录用户的登录态
                StpUtil.login(loginUserVO.getId());
                // 获取token
                token = StpUtil.getTokenValue();
            } catch (Exception e) {
                // 登录失败，打印错误信息
                log.error("登录失败：{}", e.getMessage());
            }
        }
        return token;
    }

    /**
     * 添加表格信息测试
     */
    @Test
    void addTable() throws Exception {
        // 获取token
        String token = getToken();
        log.info("token: {}", token);
        // 准备测试数据
        TableInfoAddRequest tableInfoAddRequest = new TableInfoAddRequest();
        String content = "{\"id\":\"12345\",\"name\":\"shing\",\"age\":\"18\"}"; // 修改为对象格式
        tableInfoAddRequest.setContent(JSONUtil.toJsonStr(content));
        String name = "test3";
        tableInfoAddRequest.setName(name);

        // 创建cookie并添加到请求中
        MockCookie cookie = new MockCookie(StpUtil.getTokenName(), token);


        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/add")
                        .cookie(cookie) // 将token添加到cookie中
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
        // 获取token
        String token = getToken();

        // 准备测试数据
        DeleteRequest deleteRequest = new DeleteRequest();
        long id = 1819403133009338369L;
        deleteRequest.setId(id);

        // 创建cookie并添加到请求中
        MockCookie cookie = new MockCookie(StpUtil.getTokenName(), token);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(deleteRequest);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/delete")
                        .content(requestBody) // 将请求体添加到请求中
                        .contentType(MediaType.APPLICATION_JSON) // 设置请求体的内容类型为JSON
                        .cookie(cookie)) // 将token添加到cookie中
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));

    }

    /**
     * 更新表格信息测试
     */
    @Test
    void updateTable() throws Exception {
        // 获取token
        String token = getToken();
        // 准备测试数据
        TableInfoUpdateRequest tableInfoUpdateRequest = new TableInfoUpdateRequest();
        long UpdateId = 1819578907435847681L;
        String updateName = "updated 测试表";
        tableInfoUpdateRequest.setName(updateName);
        tableInfoUpdateRequest.setId(UpdateId);

        // 创建cookie并添加到请求中
        MockCookie cookie = new MockCookie(StpUtil.getTokenName(), token);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/update")
                        .cookie(cookie) // 将token添加到cookie中
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.toJsonStr(tableInfoUpdateRequest))) // 将整个对象序列化为JSON字符串并作为请求体发送
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    /**
     * 编辑表格信息测试

     */
    @Test
    void editTable() throws Exception {
        // 获取token
        String token = getToken();
        // 准备测试数据
        TableInfoUpdateRequest tableInfoUpdateRequest = new TableInfoUpdateRequest();
        long UpdateId = 1819578907435847681L;
        String updateName = "updated 测试表";
        tableInfoUpdateRequest.setName(updateName);
        tableInfoUpdateRequest.setId(UpdateId);

        // 创建cookie并添加到请求中
        MockCookie cookie = new MockCookie(StpUtil.getTokenName(), token);

        // 发送请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/tableInfo/edit")
                        .cookie(cookie) // 将token添加到cookie中
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
        long id = 1819417908070068225L;
        mockMvc.perform(MockMvcRequestBuilders.get("/tableInfo/get/vo")
                        .param("id", String.valueOf(id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());

    }

}
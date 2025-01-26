package com.cong.sqldog.common;

import cn.dev33.satoken.stp.StpLogic;
import com.cong.sqldog.domain.user.entity.User;
import com.cong.sqldog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;

public class TestBaseByLogin extends TestBase {

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
}

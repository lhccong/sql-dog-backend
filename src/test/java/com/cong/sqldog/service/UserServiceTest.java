package com.cong.sqldog.service;

import com.cong.sqldog.common.TestBase;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 * <p>
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
class UserServiceTest extends TestBase {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "cong";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "cg";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }

}

package com.cong.sqldog.manager;

import com.cong.sqldog.infrastructure.api.CosApi;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cos 操作测试
 * <p>
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@SpringBootTest
class CosApiTest {

    @Resource
    private CosApi cosApi;

    @Test
    void putObject() {
        PutObjectResult test = cosApi.putObject("test", "test.json");
        Assertions.assertNotNull(test);
    }
}
package com.cong.sqldog.utils;

import com.cong.sqldog.common.TestBase;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestIdWorkerUtil extends TestBase {
    @Resource
    private IdWorkerUtil idWorkerUtil;

    @Test
    void testNextId() {
        long id = idWorkerUtil.nextId();
        Assertions.assertTrue(id > 0);
    }
}

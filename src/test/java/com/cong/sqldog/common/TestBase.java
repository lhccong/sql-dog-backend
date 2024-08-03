package com.cong.sqldog.common;

import com.cong.sqldog.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = { MainApplication.class, },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {"spring.profiles.active=test"}
)
@Slf4j
@AutoConfigureMockMvc
public abstract class TestBase {

    protected static final Long TEST_GROUP_ID = 1L;

    @Autowired
    protected MockMvc mockMvc;

}

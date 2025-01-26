package com.cong.sqldog.controller;


import com.cong.sqldog.infrastructure.config.GitHubConfig;
import io.swagger.v3.oas.annotations.Operation;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@RestController
@RequestMapping("/oauth")
//@Tag(name = "Github 登录相关")
public class RestAuthController {
    @Resource
    private GitHubConfig gitHubConfig;

    @GetMapping("/render")
    @Operation(summary = "获取GitHub授权地址重定向")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = gitHubConfig.getAuthRequest();
        response.sendRedirect(authRequest.authorize("wegoGithub"));
    }

}

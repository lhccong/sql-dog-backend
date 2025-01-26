package com.cong.sqldog.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SQLDog API")
                        .description("基于 SpringBoot + MySQL + SQL 慢镜\uD83D\uDD0D（自研）致力于 SQL 刷题、SQL 解析，帮助人们解决 SQL 能力薄弱问题")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("https://github.com/lhccong/sql-dog-backend")))
                .externalDocs(new ExternalDocumentation()
                        .description("基于 SpringBoot + MySQL + SQL 慢镜\uD83D\uDD0D（自研）致力于 SQL 刷题、SQL 解析，帮助人们解决 SQL 能力薄弱问题")
                        .url("https://github.com/lhccong/sql-dog-backend"));
    }
}

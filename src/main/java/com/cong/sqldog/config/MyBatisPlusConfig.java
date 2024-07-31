package com.cong.sqldog.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.cong.sqldog.core.sqlanalyze.config.ConfigUtils;
import com.cong.sqldog.core.sqlanalyze.core.SqlAnalysisAspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis Plus 配置
 * @author <a href="https://github.com/lhccong">...</a>
 */
@Configuration
@MapperScan("com.cong.sqldog.mapper")
public class MyBatisPlusConfig {

    /**
     * SQL分析方面(需放在开头)
     *
     * @return {@link SqlAnalysisAspect}
     */
    @Bean
    public SqlAnalysisAspect sqlAnalysisAspect() {

        // 加载配置文件，此处加载的是名为"sql.slow.mirror"的配置
        Properties properties = ConfigUtils.loadConfig("sql.slow.mirror");

        // 创建SQL分析切面的实例
        SqlAnalysisAspect sqlAnalysisAspect = new SqlAnalysisAspect();

        // 将加载的配置属性设置到SQL分析切面实例中
        sqlAnalysisAspect.setProperties(properties);

        // 返回配置完毕的SQL分析切面实例
        return sqlAnalysisAspect;
    }

    /**
     * MyBatis Plus 拦截器
     * 拦截器配置
     *
     * @return {@link MybatisPlusInterceptor}
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
package com.cong.sqldog.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        if (metaObject.hasGetter("userId")) {
            try {
                this.strictInsertFill(metaObject, "userId", Long.class, Long.valueOf(StpUtil.getLoginId().toString()));
            } catch (Exception e) {
                log.error("插入填充失败：{}", e.getMessage());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
    }
}
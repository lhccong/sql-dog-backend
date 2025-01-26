package com.cong.sqldog.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.cong.sqldog.infrastructure.common.ErrorCode;
import com.cong.sqldog.constant.SystemConstants;
import com.cong.sqldog.infrastructure.exception.BusinessException;
import com.cong.sqldog.domain.user.entity.User;
import com.cong.sqldog.service.UserService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {


    @Resource
    private UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        SaSession tokenSession = StpUtil.getTokenSession();
        // 先判断是否已登录
        Object userObj = tokenSession.get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        long userId = currentUser.getId();
        currentUser = userService.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<String> list = new ArrayList<>();
        list.add(currentUser.getUserRole());
        return list;
    }

}

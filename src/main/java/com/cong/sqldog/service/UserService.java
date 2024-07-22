package com.cong.sqldog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.model.dto.user.UserQueryRequest;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.LoginUserVO;
import com.cong.sqldog.model.vo.TokenLoginUserVo;
import com.cong.sqldog.model.vo.UserVO;

import java.util.List;

import me.zhyd.oauth.model.AuthCallback;

/**
 * 用户服务
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword);


    /**
     * 获取当前登录用户
     *
     * @return {@link User}
     */
    User getLoginUser();

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @return {@link User}
     */
    User getLoginUserPermitNull();

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    boolean isAdmin();

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return boolean
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @return boolean
     */
    boolean userLogout();

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 用户
     * @return {@link LoginUserVO}
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return {@link UserVO}
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList 用户列表
     * @return {@link List}<{@link UserVO}>
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link QueryWrapper}<{@link User}>
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户通过 GitHub 登录
     *
     * @param callback 回调
     * @return {@link TokenLoginUserVo }
     */
    TokenLoginUserVo userLoginByGithub(AuthCallback callback);
}

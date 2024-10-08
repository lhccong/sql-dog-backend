package com.cong.sqldog.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.constant.SystemConstants;
import com.cong.sqldog.constant.UserConstant;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.exception.ThrowUtils;
import com.cong.sqldog.model.dto.user.UserAddRequest;
import com.cong.sqldog.model.dto.user.UserLoginRequest;
import com.cong.sqldog.model.dto.user.UserQueryRequest;
import com.cong.sqldog.model.dto.user.UserRegisterRequest;
import com.cong.sqldog.model.dto.user.UserUpdateMyRequest;
import com.cong.sqldog.model.dto.user.UserUpdateRequest;
import com.cong.sqldog.model.entity.User;
import com.cong.sqldog.model.vo.CaptchaVO;
import com.cong.sqldog.model.vo.LoginUserVO;
import com.cong.sqldog.model.vo.TokenLoginUserVo;
import com.cong.sqldog.model.vo.UserVO;
import com.cong.sqldog.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.cong.sqldog.utils.IdWorkerUtil;
import com.wf.captcha.SpecCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cong.sqldog.constant.SystemConstants.SALT;

/**
 * 用户接口
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@RestController
@RequestMapping("/user")
@Slf4j
//@Tag(name = "用户相关")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final StringRedisTemplate redisTemplate;

    private final IdWorkerUtil idWorkerUtil;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userRegisterRequest.getVerKey() == null || Boolean.FALSE.equals(redisTemplate.hasKey(userRegisterRequest.getVerKey()))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }
        String redisCode = redisTemplate.opsForValue().get(userRegisterRequest.getVerKey());
        String verCode = userRegisterRequest.getVerCode();

        if (verCode == null || !Objects.equals(redisCode, verCode.trim().toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }
        //删除验证码
        redisTemplate.delete(userRegisterRequest.getVerKey());

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @return {@link BaseResponse}<{@link TokenLoginUserVo}>
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<TokenLoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TokenLoginUserVo loginUserVO = userService.userLogin(userAccount, userPassword);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户通过 GitHub 登录
     *
     * @param callback 回调
     * @return {@link BaseResponse}<{@link TokenLoginUserVo}>
     */
    @PostMapping("/login/github")
    @Operation(summary = "用户GitHub登录")
    public BaseResponse<TokenLoginUserVo> userLoginByGithub(AuthCallback callback) {
        if (callback.getCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Github 登录失败，code 为空");
        }
        TokenLoginUserVo tokenLoginUserVo = userService.userLoginByGithub(callback);
        return ResultUtils.success(tokenLoginUserVo);

    }

    /**
     * 用户注销
     *
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public BaseResponse<Boolean> userLogout() {

        boolean result = userService.userLogout();
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @return {@link BaseResponse}<{@link LoginUserVO}>
     */
    @GetMapping("/get/login")
    @Operation(summary = "获取当前登录用户")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<LoginUserVO> getLoginUser() {
        User user = userService.getLoginUser();
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest 用户添加请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "创建用户")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "删除用户")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "更新用户")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id 编号
     * @return {@link BaseResponse}<{@link User}>
     */
    @GetMapping("/get")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "根据 id 获取用户（仅管理员）")
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id 编号
     * @return {@link BaseResponse}<{@link UserVO}>
     */
    @GetMapping("/get/vo")
    @Operation(summary = "根据 id 获取包装类")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<UserVO> getUserVoById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link User}>>
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @Operation(summary = "分页获取用户列表（仅管理员）")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link UserVO}>>
     */
    @PostMapping("/list/page/vo")
    @Operation(summary = "分页获取用户封装列表")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVoPage.setRecords(userVO);
        return ResultUtils.success(userVoPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 用户更新我请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update/my")
    @Operation(summary = "更新个人信息")
    @SaCheckRole(value = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE}, mode = SaMode.OR)
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser();
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    public BaseResponse<CaptchaVO> captcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        String key = String.valueOf(idWorkerUtil.nextId());
        // 存入redis并设置过期时间为5分钟
        redisTemplate.opsForValue().set(SystemConstants.SQL_DOG + key, verCode, 5, TimeUnit.MINUTES);
        CaptchaVO captchaVO = new CaptchaVO().setKey(SystemConstants.SQL_DOG + key).setCode(specCaptcha.toBase64());

        // 将key和base64返回给前端
        return ResultUtils.success(captchaVO);
    }
}

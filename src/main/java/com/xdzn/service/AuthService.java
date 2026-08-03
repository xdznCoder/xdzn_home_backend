package com.xdzn.service;

import com.xdzn.model.dto.AuthSession;
import com.xdzn.model.dto.AuthUser;
import com.xdzn.model.dto.LoginDto;
import com.xdzn.model.dto.RegisterDto;

/**
 * AuthService
 * <p>
 * 认证服务接口，基于 Sa-Token 定义注册、登录、登出与当前用户查询的契约。
 * <p>
 * 登录会话由 Sa-Token 统一管理：access_token（JWT）+ {@code active-timeout}
 * 滑动续期，无需自研 refresh_token。
 *
 * @author xdzn
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param dto 注册参数（用户名、邮箱、密码）
     * @return 认证会话（access_token、用户信息）
     */
    AuthSession register(RegisterDto dto);

    /**
     * 用户登录
     *
     * @param dto 登录参数（邮箱、密码）
     * @return 认证会话（access_token、用户信息）
     */
    AuthSession login(LoginDto dto);

    /**
     * 退出登录，使当前 access_token 立即失效
     * <p>
     * 未登录时调用不抛异常，静默成功。
     */
    void logout();

    /**
     * 获取当前登录用户信息（安全 DTO，不含密码）
     *
     * @return 当前登录用户信息
     */
    AuthUser me();
}

package com.xdzn.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * AuthSession
 * <p>
 * 认证会话 DTO，登录/注册成功后返回给前端的会话信息。
 * <p>
 * access_token 为 Sa-Token JWT，配合服务端 {@code active-timeout} 活动续期：
 * 用户持续活跃时每次请求自动刷新有效期，无需 refresh_token；
 * 超过 {@code active-timeout} 不活跃才失效，需重新登录。
 *
 * @author xdzn
 */
@Data
@Builder
public class AuthSession {

    /**
     * 访问令牌（access_token），请求鉴权时通过 {@code Authorization: Bearer <token>} 携带
     */
    private String accessToken;

    /**
     * 当前登录用户信息
     */
    private AuthUser user;
}

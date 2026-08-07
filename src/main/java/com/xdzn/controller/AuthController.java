package com.xdzn.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xdzn.common.Result;
import com.xdzn.model.dto.AuthSession;
import com.xdzn.model.dto.AuthUser;
import com.xdzn.model.dto.LoginDto;
import com.xdzn.model.dto.RegisterDto;
import com.xdzn.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController
 * <p>
 * 认证相关接口：注册、登录、登出与当前用户信息查询。
 * <p>
 * 登录采用 Sa-Token access_token（JWT），配合服务端 {@code active-timeout}
 * 活动续期自动刷新有效期，无 refresh_token；请求通过
 * {@code Authorization: Bearer <token>} 头携带令牌。
 *
 * @author xdzn
 */
@Tag(name = "认证接口", description = "注册、登录、登出与当前用户信息查询（基于 Sa-Token，Bearer 令牌）")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 认证服务
     */
    private final AuthService authService;

    /**
     * 构造注入认证服务
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     *
     * @param dto 注册参数（用户名、邮箱、密码）
     * @return access_token 与用户信息
     */
    @Operation(summary = "用户注册", description = "注册新用户（角色为 member），成功后直接登录并返回 access_token 与用户信息")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody @Valid RegisterDto dto) {
        AuthSession session = authService.register(dto);
        return Result.ok(Map.of(
                "access_token", session.getAccessToken(),
                "user", session.getUser()
        ));
    }

    /**
     * 用户登录
     *
     * @param dto 登录参数（邮箱、密码）
     * @return access_token 与用户信息
     */
    @Operation(summary = "用户登录", description = "使用邮箱 + 密码登录，返回 access_token 与用户信息；密码错误会记录失败计数（防暴力破解）")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginDto dto) {
        AuthSession session = authService.login(dto);
        return Result.ok(Map.of(
                "access_token", session.getAccessToken(),
                "user", session.getUser()
        ));
    }

    /**
     * 退出登录，使当前 access_token 立即失效
     *
     * @return 操作结果
     */
    @Operation(summary = "退出登录", description = "使当前 access_token 立即失效（基于 Redis 会话，登出后同 token 再访问会返回 401）")
    @PostMapping("/logout")
    public Result<Map<String, Boolean>> logout() {
        authService.logout();
        return Result.ok(Map.of("success", true));
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 该接口已在 SaTokenConfig 中配置登录校验，未登录时由 Sa-Token 返回 401。
     *
     * @return access_token 与用户信息
     */
    @Operation(summary = "获取当前用户", description = "返回当前登录用户信息与 access_token；需携带 Authorization: Bearer 头")
    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        AuthUser user = authService.me();
        return Result.ok(Map.of(
                "access_token", StpUtil.getTokenValue(),
                "user", user
        ));
    }
}

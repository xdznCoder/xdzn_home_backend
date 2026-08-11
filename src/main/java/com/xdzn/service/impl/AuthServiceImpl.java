package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.AuthSession;
import com.xdzn.model.dto.AuthUser;
import com.xdzn.model.dto.LoginDto;
import com.xdzn.model.dto.RegisterDto;
import com.xdzn.model.entity.User;
import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.AuthRedisKey;
import com.xdzn.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl
 * <p>
 * 认证服务实现，基于 Sa-Token 完成注册、登录、登出与用户查询。
 * <p>
 * 会话管理完全交由 Sa-Token：登录后签发 JWT access_token，配合
 * {@code active-timeout} 每次请求自动续期（滑动续期），登出时
 * {@link StpUtil#logout()} 使令牌立即失效。不再使用自研 refresh_token。
 * <p>
 * Redis 交互统一走 {@link RedisService}，key 前缀定义见 {@link AuthRedisKey}，
 * 当前仅保留 {@link AuthRedisKey#LOGIN_FAIL} 登录失败计数（防暴力破解）。
 *
 * @author xdzn
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * 用户表 Mapper
     */
    private final UserMapper userMapper;

    /**
     * Redis 操作服务（统一缓存读写入口）
     */
    private final RedisService redisService;

    /**
     * BCrypt 密码编码器，用于密码加密与校验
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 构造注入依赖
     *
     * @param userMapper   用户表 Mapper
     * @param redisService Redis 操作服务
     */
    public AuthServiceImpl(UserMapper userMapper,
                           RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户注册
     * <p>
     * 校验邮箱唯一性后创建用户（角色固定为 member），随后签发认证会话。
     *
     * @param dto 注册参数（用户名、邮箱、密码）
     * @return 认证会话
     */
    @Override
    @Transactional
    public AuthSession register(RegisterDto dto) {
        // 检查邮箱是否已注册
        if (userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail())) != null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "邮箱已被注册");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("member");
        userMapper.insert(user);

        return createSession(user);
    }

    /**
     * 用户登录
     * <p>
     * 校验邮箱与密码；密码错误时记录失败次数（防暴力破解），
     * 登录成功后清空失败计数并签发认证会话。
     *
     * @param dto 登录参数（邮箱、密码）
     * @return 认证会话
     */
    @Override
    public AuthSession login(LoginDto dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail()));
        if (user == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            redisService.incr(AuthRedisKey.LOGIN_FAIL, dto.getEmail());
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "密码错误");
        }

        redisService.delete(AuthRedisKey.LOGIN_FAIL, dto.getEmail());
        return createSession(user);
    }

    /**
     * 退出登录，使当前 access_token 立即失效
     * <p>
     * 已登录才执行 Sa-Token 登出；未登录时静默成功，避免无谓的 401 报错。
     */
    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 接口路径已在 SaTokenConfig 配置登录校验，此处 isLogin 判断作为双保险。
     *
     * @return 当前登录用户信息（不含密码）
     */
    @Override
    public AuthUser me() {
        if (!StpUtil.isLogin()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(loginId);
        return AuthUser.from(user);
    }

    // ── 内部方法 ──────────────────────

    /**
     * 为用户签发认证会话：Sa-Token 登录并返回 access_token 与用户信息
     *
     * @param user 用户实体
     * @return 认证会话
     */
    private AuthSession createSession(User user) {
        // 统一使用 String 类型 loginId（Sa-Token JWT 模式内部即为 String，避免 Long/String 混用）
        StpUtil.login(user.getId().toString());
        // 角色由 StpInterfaceImpl 提供（鉴权时查 users 表），登录时无需写入会话
        return AuthSession.builder()
                .accessToken(StpUtil.getTokenValue())
                .user(AuthUser.from(user))
                .build();
    }
}

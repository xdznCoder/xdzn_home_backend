package com.xdzn.common.config;

import cn.dev33.satoken.stp.StpInterface;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.entity.User;
import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.CacheRedisKey;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StpInterfaceImpl
 * <p>
 * Sa-Token 权限数据源实现：向 Sa-Token 提供账号的角色 / 权限列表。
 * <p>
 * Sa-Token 的 {@code checkRole} / {@code checkPermission} 通过本接口查询数据。
 * 本项目角色存储在 users 表 {@code role} 字段。
 * <p>
 * <b>性能优化</b>：{@link #getRoleList} 每次鉴权都会被 Sa-Token 调用，为避免实时打库，
 * 经统一缓存 {@link CacheRedisKey#USER_ROLE} + {@link RedisService#getOrSet} 缓存角色
 * （命中零 DB，角色变更时由 {@code MemberServiceImpl#setMemberRole} 失效缓存，保持一致）。
 *
 * @author xdzn
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 用户表 Mapper，用于按登录 id 查询角色（缓存未命中时回源）
     */
    private final UserMapper userMapper;

    /**
     * 统一 Redis 缓存服务
     */
    private final RedisService redisService;

    /**
     * 构造注入依赖
     *
     * @param userMapper   用户表 Mapper
     * @param redisService 统一 Redis 缓存服务
     */
    public StpInterfaceImpl(UserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
    }

    /**
     * 返回账号拥有的权限码列表
     * <p>
     * 本项目暂无权限码体系（仅角色校验），返回空列表。
     *
     * @param loginId   登录 id
     * @param loginType 登录类型（默认 "login"）
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回账号拥有的角色列表（来自 users 表 role 字段）
     *
     * @param loginId   登录 id（统一为 String 形式的用户主键）
     * @param loginType 登录类型
     * @return 角色列表，如 ["admin"]、["member"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // loginId 统一为 String，转 Long
        Long userId = Long.valueOf(String.valueOf(loginId));
        // 缓存未命中时查库并回填；命中零 DB。角色为单一字符串（非可空集合），空值标记防穿透。
        String role = redisService.getOrSet(CacheRedisKey.USER_ROLE, String.valueOf(userId), String.class,
                () -> {
                    User user = userMapper.selectById(userId);
                    return user == null ? null : user.getRole();
                });
        if (role == null) {
            return List.of();
        }
        return List.of(role);
    }
}

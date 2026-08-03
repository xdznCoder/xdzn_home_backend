package com.xdzn.redis.key;

/**
 * AuthRedisKey
 * <p>
 * 认证模块相关 Redis 键前缀枚举，实现 {@link KeyPrefix} 接口。
 * 集中管理 {@code /api/auth} 认证流程中涉及的 Redis key 前缀与过期时间，
 * 配合 {@link com.xdzn.redis.RedisService} 完成统一的缓存读写与失效。
 * <p>
 * 说明：登录会话本身由 Sa-Token 管理（存储于 Redis，见 {@code dao-prefix} 配置），
 * 此处仅保留认证辅助计数类缓存。
 *
 * @author xdzn
 */
public enum AuthRedisKey implements KeyPrefix {

    /**
     * 登录失败计数前缀
     * <p>
     * 记录指定邮箱的连续登录失败次数，配合登录接口实现防暴力破解。
     * 过期时间 15 分钟，保证失败计数窗口自动清零。
     */
    LOGIN_FAIL("login:fail:", 15 * 60);

    /**
     * 缓存键前缀
     */
    private final String prefix;

    /**
     * 过期时间（秒）
     */
    private final int expireSeconds;

    /**
     * 枚举构造方法
     *
     * @param prefix        缓存键前缀
     * @param expireSeconds 过期时间（秒）
     */
    AuthRedisKey(String prefix, int expireSeconds) {
        this.prefix = prefix;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 获取过期时间
     *
     * @return 过期时间（秒）
     */
    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 获取 key 前缀
     *
     * @return key 前缀字符串
     */
    @Override
    public String getPrefix() {
        return prefix;
    }
}

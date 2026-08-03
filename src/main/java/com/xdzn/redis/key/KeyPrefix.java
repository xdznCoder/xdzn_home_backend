package com.xdzn.redis.key;

/**
 * KeyPrefix
 * <p>
 * Redis key 前缀接口，统一约定「过期时间」与「键前缀」的获取方式。
 * 业务模块通过实现该接口（通常继承 {@link BasePrefix}）定义各自的缓存 key 规则，
 * 由 {@link com.xdzn.redis.RedisService} 在读写时拼接完整 key 并应用过期时间。
 *
 * @author russell
 * @date 2026/1/29
 */
public interface KeyPrefix {

    /**
     * 获取过期时间
     *
     * @return 过期时间（秒），0 表示不设置过期时间
     */
    int getExpireSeconds();

    /**
     * 获取 key 前缀
     *
     * @return key 前缀字符串
     */
    String getPrefix();

}

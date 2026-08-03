package com.xdzn.redis.key;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * BasePrefix
 * <p>
 * {@link KeyPrefix} 的基础抽象实现，提供「过期时间 + 键前缀」的组合存储，
 * 供各业务模块继承定义具体的 Redis key 前缀。
 * <p>
 * 若前缀不需要过期时间，可使用 {@link #BasePrefix(String)} 构造器（过期时间默认为 0，表示永不过期）。
 *
 * @author russell
 * @date 2026/1/29
 */
@AllArgsConstructor
@Setter
public abstract class BasePrefix implements KeyPrefix {

    /**
     * 过期时间（秒），0 表示不设置过期时间
     */
    private int expireSeconds;

    /**
     * 缓存键前缀
     */
    private String prefix;

    /**
     * 构造方法：仅指定前缀，过期时间默认为 0（永不过期）
     *
     * @param prefix 缓存键前缀
     */
    public BasePrefix(String prefix) {
        this(0, prefix);
    }

    /**
     * 获取缓存键前缀
     *
     * @return 键前缀字符串
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 获取过期时间
     *
     * @return 过期时间（秒），0 表示永不过期
     */
    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }
}

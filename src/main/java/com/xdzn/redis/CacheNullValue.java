package com.xdzn.redis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * CacheNullValue
 * <p>
 * 空值缓存标记类，用于标识数据库中不存在的数据，防止缓存穿透。
 * <p>
 * 当查询结果为空时，由 {@link RedisService#set} / {@link RedisService#setList}
 * 将本标记写入 Redis 并设置较短的过期时间；后续查询命中标记时直接返回空结果，
 * 避免每次穿透到数据库。同时提供 Jackson 序列化/反序列化支持，
 * 兼容 Redis 中已存在的 {@code "CACHE_NULL"} 格式缓存。
 *
 * @author xdzn
 */
public class CacheNullValue {

    /**
     * 单例实例
     */
    public static final CacheNullValue INSTANCE = new CacheNullValue();

    /**
     * 私有构造函数，防止外部实例化
     */
    private CacheNullValue() {
        // 单例，禁止外部创建
    }

    /**
     * Jackson 序列化时使用此方法的返回值，确保即使意外序列化也不会报错
     *
     * @return 固定的空值标记字符串
     */
    @JsonValue
    public String serialize() {
        return "CACHE_NULL";
    }

    /**
     * Jackson 反序列化时从字符串构造实例，兼容 Redis 中已有的 "CACHE_NULL" 格式
     *
     * @param value 序列化字符串
     * @return 单例实例；非空值标记时返回 null
     */
    @JsonCreator
    public static CacheNullValue of(String value) {
        if ("CACHE_NULL".equals(value)) {
            return INSTANCE;
        }
        return null;
    }

    /**
     * 字符串表示形式
     *
     * @return 空值标记字符串
     */
    @Override
    public String toString() {
        return "CACHE_NULL";
    }
}

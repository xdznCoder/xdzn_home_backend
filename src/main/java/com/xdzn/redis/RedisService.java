package com.xdzn.redis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.xdzn.redis.key.KeyPrefix;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * RedisService
 * <p>
 * 封装 Redis 操作的服务类，作为项目内所有手动 Redis 操作的统一入口。
 * <p>
 * 主要特性：
 * <ul>
 *     <li>基于 {@link KeyPrefix} 统一管理 key 前缀与过期时间，避免 key 散落各处</li>
 *     <li>值缓存与列表缓存支持空值标记，防止缓存穿透</li>
 *     <li>设置缓存时添加随机过期偏移，防止缓存雪崩</li>
 *     <li>同时提供基于 {@link StringRedisTemplate} 的 JSON 字符串直存方案，绕过 Jackson 类型包装</li>
 *     <li>提供集合、Hash、批量删除、按前缀删除、分布式锁等常用能力</li>
 * </ul>
 *
 * @author russell
 * @date 2026/1/29
 */
@Service
@Slf4j
public class RedisService {

    /**
     * 通用 RedisTemplate，value 使用 GenericJackson2JsonRedisSerializer 序列化（带类型信息）
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 字符串 RedisTemplate，value 以纯字符串形式存取（用于计数、JSON 字符串等场景）
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取 RedisTemplate 实例（用于高级操作）
     *
     * @return 通用 RedisTemplate
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 对指定 key 执行原子自增操作
     * <p>
     * 使用 {@link StringRedisTemplate} 存储纯字符串数字，避免 Jackson 类型包装
     * 导致后续 INCR 命令对已存 JSON 值自增失败。
     *
     * @param prefix 缓存键前缀（可携带过期时间）
     * @param key    业务键（自动拼接前缀）
     * @return 自增后的数值；若 Redis 异常则可能返回 null
     */
    public Long incr(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        Long count = stringRedisTemplate.opsForValue().increment(realKey);
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            stringRedisTemplate.expire(realKey, expireSeconds, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 对指定 key 执行原子自减操作（实现同 {@link #incr}，使用字符串模板）
     *
     * @param prefix 缓存键前缀（可携带过期时间）
     * @param key    业务键（自动拼接前缀）
     * @return 自减后的数值；若 Redis 异常则可能返回 null
     */
    public Long decr(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        Long count = stringRedisTemplate.opsForValue().decrement(realKey);
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            stringRedisTemplate.expire(realKey, expireSeconds, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 向 Set 集合中添加元素，并可按前缀设置过期时间
     *
     * @param prefix 缓存键前缀（可携带过期时间）
     * @param key    业务键（自动拼接前缀）
     * @param value  待添加的元素
     * @param <T>    元素类型
     */
    public <T> void sAdd(KeyPrefix prefix, String key, T value) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.opsForSet().add(realKey, value);
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            redisTemplate.expire(realKey, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取 Set 集合中的全部元素
     *
     * @param prefix 缓存键前缀
     * @param key    业务键
     * @param clazz  目标元素类型
     * @param <T>    元素类型
     * @return 元素集合；集合不存在时返回空集合
     */
    public <T> Set<T> sMembers(KeyPrefix prefix, String key, Class<T> clazz) {
        String realKey = prefix.getPrefix() + key;
        Set<Object> set = redisTemplate.opsForSet().members(realKey);
        return set.stream().map(clazz::cast).collect(Collectors.toSet());
    }

    /**
     * 从 Set 集合中移除指定元素
     *
     * @param prefix 缓存键前缀
     * @param key    业务键
     * @param value  待移除的元素
     * @param <T>    元素类型
     */
    public <T> void sRemove(KeyPrefix prefix, String key, T value) {
        String realKey = prefix.getPrefix() + key;
        redisTemplate.opsForSet().remove(realKey, value);
    }

    /**
     * 设置缓存（支持空值缓存，防止缓存穿透）
     *
     * @param prefix 缓存键前缀（可携带过期时间）
     * @param key    缓存键
     * @param value  缓存值（如果为 null，则缓存空值标记 {@link CacheNullValue}）
     * @param <T>    值类型
     */
    public <T> void set(KeyPrefix prefix, String key, T value) {
        // 拼接 key 前缀
        String realKey = prefix.getPrefix() + key;
        // 根据是否设置过期时间调用不同方法
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            // 添加随机偏移量（0~300 秒），防止缓存雪崩
            int randomOffset = (int) (Math.random() * 300);
            // 如果值为 null，缓存空值标记对象
            Object cacheValue = (value == null) ? CacheNullValue.INSTANCE : value;
            redisTemplate.opsForValue().set(realKey, cacheValue, expireSeconds + randomOffset, TimeUnit.SECONDS);
        } else {
            Object cacheValue = (value == null) ? CacheNullValue.INSTANCE : value;
            redisTemplate.opsForValue().set(realKey, cacheValue);
        }
    }

    /**
     * 设置列表缓存（支持空列表缓存，防止缓存穿透）
     *
     * @param prefix 缓存键前缀（可携带过期时间）
     * @param key    缓存键
     * @param value  缓存值（如果为 null 或空列表，则缓存空值标记 {@link CacheNullValue}）
     * @param <T>    元素类型
     */
    public <T> void setList(KeyPrefix prefix, String key, List<T> value) {
        // 拼接 key 前缀
        String realKey = prefix.getPrefix() + key;
        // 根据是否设置过期时间调用不同方法
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            // 添加随机偏移量（0~300 秒），防止缓存雪崩
            int randomOffset = (int) (Math.random() * 300);
            // 如果值为 null 或空列表，缓存空值标记对象
            Object cacheValue = (value == null || value.isEmpty()) ? CacheNullValue.INSTANCE : value;
            redisTemplate.opsForValue().set(realKey, cacheValue, expireSeconds + randomOffset, TimeUnit.SECONDS);
        } else {
            Object cacheValue = (value == null || value.isEmpty()) ? CacheNullValue.INSTANCE : value;
            redisTemplate.opsForValue().set(realKey, cacheValue);
        }
    }

    /**
     * 获取单个缓存值
     * <p>
     * 命中空值缓存时返回 null；基本类型与 Java 8 时间类型做特殊转换，
     * 复杂对象通过 Hutool {@link BeanUtil} 转换。反序列化失败会删除损坏缓存并返回 null。
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @param clazz  目标类型
     * @param <T>    目标类型
     * @return 缓存值；缓存不存在、命中空值标记或反序列化失败时返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        try {
            // 拼接 key 前缀
            String realKey = prefix.getPrefix() + key;
            // 从 Redis 中获取值
            Object value = redisTemplate.opsForValue().get(realKey);
            if (value == null) {
                return null;
            }
            // 检查是否为空值标记（防止缓存穿透）
            if (value instanceof CacheNullValue) {
                log.debug("命中空值缓存：key={}", realKey);
                return null;
            }
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            // 对于基本类型和包装类型，使用 Convert 进行转换
            if (clazz == Long.class || clazz == long.class) {
                return (T) Convert.toLong(value);
            }
            if (clazz == Integer.class || clazz == int.class) {
                return (T) Convert.toInt(value);
            }
            if (clazz == String.class) {
                return (T) Convert.toStr(value);
            }
            if (clazz == Boolean.class || clazz == boolean.class) {
                return (T) Convert.toBool(value);
            }
            // Java 8 时间类型特殊处理（避免 Hutool 反射访问私有构造函数导致 InaccessibleObjectException）
            if (clazz == LocalDate.class) {
                return (T) LocalDate.parse((String) value);
            }
            if (clazz == LocalDateTime.class) {
                return (T) LocalDateTime.parse((String) value);
            }
            if (clazz == LocalTime.class) {
                return (T) LocalTime.parse((String) value);
            }
            // 对于复杂对象，使用 BeanUtil 转换
            return BeanUtil.toBean(value, clazz);
        } catch (SerializationException e) {
            // 记录序列化异常，删除损坏的缓存，返回 null 让调用方查数据库
            log.warn("Redis 反序列化失败，将删除损坏的缓存：key={}, clazz={}",
                    prefix.getPrefix() + key, clazz.getSimpleName(), e);
            delete(prefix, key);
            return null;
        } catch (Exception e) {
            // 其他异常也返回 null
            log.error("Redis 获取数据失败：key={}", prefix.getPrefix() + key, e);
            return null;
        }
    }

    /**
     * 获取列表缓存
     * <p>
     * 命中空值缓存时返回空列表；若反序列化后的元素已是目标类型则直接强转返回（快速路径），
     * 否则通过 Hutool {@link Convert#toList} 转换。异常时删除损坏缓存并返回 null。
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @param clazz  元素目标类型
     * @param <T>    元素目标类型
     * @return 元素列表；缓存不存在或反序列化失败时返回 null，命中空值标记时返回空列表
     */
    public <T> List<T> getList(KeyPrefix prefix, String key, Class<T> clazz) {
        try {
            String realKey = prefix.getPrefix() + key;
            Object value = redisTemplate.opsForValue().get(realKey);
            // 防止返回空集合，引发歧义
            if (value == null) {
                return null;
            }
            // 检查是否为空值标记（防止缓存穿透）
            if (value instanceof CacheNullValue) {
                log.debug("命中空值缓存：key={}", realKey);
                return new ArrayList<>();
            }
            // 如果是列表类型，进行转换
            if (value instanceof List) {
                List<?> rawList = (List<?>) value;
                // 检查列表中是否包含 CacheNullValue
                if (!rawList.isEmpty() && rawList.get(0) instanceof CacheNullValue) {
                    log.debug("命中空值缓存（列表）：key={}", realKey);
                    return new ArrayList<>();
                }
                // 快速路径：Jackson WRAPPER_ARRAY 反序列化后元素已是目标类型，
                // 直接强转跳过 Convert.toList 的 BeanUtil 反射开销（高并发下有同步瓶颈）
                if (!rawList.isEmpty() && clazz.isInstance(rawList.get(0))) {
                    @SuppressWarnings("unchecked")
                    List<T> typed = (List<T>) rawList;
                    return typed;
                }
                return Convert.toList(clazz, value);
            }
            // 其他情况尝试转换
            return Convert.toList(clazz, value);
        } catch (SerializationException e) {
            log.warn("Redis 反序列化失败，将删除损坏的缓存：key={}, clazz={}",
                    prefix.getPrefix() + key, clazz.getSimpleName(), e);
            delete(prefix, key);
            return null;
        } catch (Exception e) {
            log.error("Redis 获取列表数据失败：key={}", prefix.getPrefix() + key, e);
            return null;
        }
    }

    // ==================== 缓存回填方法（统一处理穿透 / 击穿 / 雪崩） ====================

    /**
     * 读取缓存，未命中时通过 {@code loader} 回源并回填缓存。
     * <p>
     * 统一处理缓存三大问题：
     * <ul>
     *     <li><b>穿透</b>：{@code loader} 返回 null 时经 {@link #set} 写入空值标记，下次命中直接返回 null</li>
     *     <li><b>击穿</b>：miss 后用 {@link #tryLock} 互斥，仅持锁线程回源，其余线程短暂等待重读缓存</li>
     *     <li><b>雪崩</b>：回填经 {@link #set} 叠加随机 TTL 偏移</li>
     * </ul>
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @param clazz  值类型
     * @param loader 数据加载器（未命中时回调查库）
     * @param <T>    值类型
     * @return 缓存值或回源值；命中空值标记返回 null
     */
    public <T> T getOrSet(KeyPrefix prefix, String key, Class<T> clazz, Supplier<T> loader) {
        // 命中（含空值标记，get 对空值标记返回 null）直接返回，避免打库
        if (Boolean.TRUE.equals(exists(prefix, key))) {
            return get(prefix, key, clazz);
        }
        String lockKey = prefix.getPrefix() + key + ":lock";
        if (Boolean.TRUE.equals(tryLock(lockKey, 30))) {
            try {
                // 双检：等待锁期间可能已被其他线程回填
                if (Boolean.TRUE.equals(exists(prefix, key))) {
                    return get(prefix, key, clazz);
                }
                T value = loader.get();
                // null 也会经 set 写空值标记（防穿透）
                set(prefix, key, value);
                return value;
            } finally {
                unlock(lockKey);
            }
        }
        // 未拿到锁：短暂等待重读缓存，仍无则回源（容忍极小并发）
        for (int i = 0; i < 3; i++) {
            sleepQuietly(50);
            if (Boolean.TRUE.equals(exists(prefix, key))) {
                return get(prefix, key, clazz);
            }
        }
        T value = loader.get();
        set(prefix, key, value);
        return value;
    }

    /**
     * 读取列表缓存，未命中时通过 {@code loader} 回源并回填（逻辑同 {@link #getOrSet}，列表版）。
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @param clazz  元素类型
     * @param loader 列表加载器
     * @param <T>    元素类型
     * @return 缓存列表或回源列表；命中空值标记返回空列表
     */
    public <T> List<T> getOrSetList(KeyPrefix prefix, String key, Class<T> clazz, Supplier<List<T>> loader) {
        if (Boolean.TRUE.equals(exists(prefix, key))) {
            return getList(prefix, key, clazz);
        }
        String lockKey = prefix.getPrefix() + key + ":lock";
        if (Boolean.TRUE.equals(tryLock(lockKey, 30))) {
            try {
                if (Boolean.TRUE.equals(exists(prefix, key))) {
                    return getList(prefix, key, clazz);
                }
                List<T> value = loader.get();
                setList(prefix, key, value);
                return value;
            } finally {
                unlock(lockKey);
            }
        }
        for (int i = 0; i < 3; i++) {
            sleepQuietly(50);
            if (Boolean.TRUE.equals(exists(prefix, key))) {
                return getList(prefix, key, clazz);
            }
        }
        List<T> value = loader.get();
        setList(prefix, key, value);
        return value;
    }

    /**
     * 静默休眠（中断时恢复中断标记）
     *
     * @param millis 休眠毫秒数
     */
    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== StringRedisTemplate 方法（绕过 Jackson 类型包装） ====================

    /**
     * 直接缓存 JSON 字符串，跳过 GenericJackson2JsonRedisSerializer 的类型包装
     * <p>
     * 读取时用 {@link #getJsonString} 取回原始字符串，由调用方自行反序列化，
     * 相比 {@link #get} 省去类型包装与反射开销，速度提升 3-5 倍。
     *
     * @param prefix     缓存键前缀（可携带过期时间）
     * @param key        缓存键
     * @param jsonString 待缓存的 JSON 字符串
     */
    public void setJsonString(KeyPrefix prefix, String key, String jsonString) {
        String realKey = prefix.getPrefix() + key;
        int expireSeconds = prefix.getExpireSeconds();
        if (expireSeconds > 0) {
            int randomOffset = (int) (Math.random() * 300);
            stringRedisTemplate.opsForValue().set(realKey, jsonString, expireSeconds + randomOffset, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(realKey, jsonString);
        }
    }

    /**
     * 从 Redis 读取 JSON 字符串（无类型反序列化开销）
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @return JSON 字符串，null 表示缓存未命中
     */
    public String getJsonString(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return stringRedisTemplate.opsForValue().get(realKey);
    }

    /**
     * 删除 JSON 字符串缓存
     *
     * @param prefix 缓存键前缀
     * @param key    缓存键
     * @return 是否删除成功（key 存在则返回 true）
     */
    public Boolean deleteJsonString(KeyPrefix prefix, String key) {
        return stringRedisTemplate.delete(prefix.getPrefix() + key);
    }

    /**
     * 向 Hash 中写入单个字段值
     *
     * @param prefix  缓存键前缀（可携带过期时间）
     * @param key     业务键（可为 null，此时仅使用前缀作为 Hash key）
     * @param hashKey Hash 字段名
     * @param value   字段值
     * @param <T>     值类型
     */
    public <T> void setHash(KeyPrefix prefix, String key, String hashKey, T value) {
        String realKey = prefix.getPrefix();
        if (key != null) {
            realKey += key;
        }
        redisTemplate.opsForHash().put(realKey, hashKey, value);
        // 设置 TTL
        if (prefix.getExpireSeconds() > 0) {
            redisTemplate.expire(realKey, prefix.getExpireSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * 读取 Hash 中指定字段值
     *
     * @param prefix  缓存键前缀
     * @param key     业务键（可为 null）
     * @param hashKey Hash 字段名
     * @param clazz   目标类型
     * @param <T>     目标类型
     * @return 字段值；字段不存在或反序列化失败时返回 null
     */
    public <T> T getHash(KeyPrefix prefix, String key, String hashKey, Class<T> clazz) {
        String realKey = prefix.getPrefix();
        if (key != null) {
            realKey += key;
        }
        try {
            Object value = redisTemplate.opsForHash().get(realKey, hashKey);
            if (value == null) {
                return null;
            }
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            // Java 8 时间类型特殊处理（避免 Hutool 反射访问私有构造函数导致 InaccessibleObjectException）
            if (clazz == LocalDate.class) {
                return (T) LocalDate.parse((String) value);
            }
            if (clazz == LocalDateTime.class) {
                return (T) LocalDateTime.parse((String) value);
            }
            if (clazz == LocalTime.class) {
                return (T) LocalTime.parse((String) value);
            }
            return BeanUtil.toBean(value, clazz);
        } catch (SerializationException e) {
            log.warn("Redis Hash 反序列化失败，将删除损坏的缓存：key={}, hashKey={}, clazz={}",
                    realKey, hashKey, clazz.getSimpleName(), e);
            redisTemplate.opsForHash().delete(realKey, hashKey);
            return null;
        } catch (Exception e) {
            log.error("Redis 获取 Hash 数据失败：key={}, hashKey={}", realKey, hashKey, e);
            return null;
        }
    }

    /**
     * 向 Hash 写入列表字段值（使用前缀作为 Hash key，无业务键）
     *
     * @param prefix  缓存键前缀（可携带过期时间）
     * @param hashKey Hash 字段名
     * @param value   列表值
     * @param <T>     元素类型
     */
    public <T> void setHashList(KeyPrefix prefix, String hashKey, List<T> value) {
        redisTemplate.opsForHash().put(prefix.getPrefix(), hashKey, value);
        // 设置 TTL
        if (prefix.getExpireSeconds() > 0) {
            redisTemplate.expire(prefix.getPrefix(), prefix.getExpireSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * 读取 Hash 列表字段值（使用前缀作为 Hash key，无业务键）
     *
     * @param prefix  缓存键前缀
     * @param hashKey Hash 字段名
     * @param clazz   元素目标类型
     * @param <T>     元素目标类型
     * @return 列表；字段不存在或反序列化失败时返回 null
     */
    public <T> List<T> getHashList(KeyPrefix prefix, String hashKey, Class<T> clazz) {
        return getHashList(prefix, null, hashKey, clazz);
    }

    /**
     * 读取 Hash 列表字段值
     *
     * @param prefix  缓存键前缀
     * @param key     业务键（可为 null）
     * @param hashKey Hash 字段名
     * @param clazz   元素目标类型
     * @param <T>     元素目标类型
     * @return 列表；字段不存在或反序列化失败时返回 null
     */
    public <T> List<T> getHashList(KeyPrefix prefix, String key, String hashKey, Class<T> clazz) {
        try {
            String realKey = prefix.getPrefix();
            if (key != null) {
                realKey += key;
            }
            Object value = redisTemplate.opsForHash().get(realKey, hashKey);
            if (value == null) {
                return null;
            }
            return Convert.toList(clazz, value);
        } catch (SerializationException e) {
            log.warn("Redis Hash List 反序列化失败，将删除损坏的缓存：key={}, hashKey={}, clazz={}",
                    prefix.getPrefix() + (key != null ? key : ""), hashKey, clazz.getSimpleName(), e);
            String realKey = prefix.getPrefix();
            if (key != null) {
                realKey += key;
            }
            redisTemplate.opsForHash().delete(realKey, hashKey);
            return null;
        } catch (Exception e) {
            log.error("Redis 获取 Hash 列表数据失败：key={}, hashKey={}",
                    prefix.getPrefix() + (key != null ? key : ""), hashKey, e);
            return null;
        }
    }

    /**
     * 获取指定 Hash 的所有字段名
     *
     * @param prefix 缓存键前缀
     * @param key    业务键（可为 null）
     * @param clazz  字段名目标类型
     * @param <T>    字段名目标类型
     * @return 字段名集合
     */
    public <T> Set<T> getAllHashKey(KeyPrefix prefix, String key, Class<T> clazz) {
        String realKey = prefix.getPrefix();
        if (key != null) {
            realKey += key;
        }
        Set<Object> keys = redisTemplate.opsForHash().keys(realKey);
        return keys.stream().map(clazz::cast).collect(Collectors.toSet());
    }

    /**
     * 判断指定 key 是否存在
     *
     * @param prefix 缓存键前缀
     * @param key    业务键
     * @return true 表示存在，false 表示不存在
     */
    public Boolean exists(KeyPrefix prefix, String key) {
        return redisTemplate.hasKey(prefix.getPrefix() + key);
    }

    /**
     * 删除指定 key
     *
     * @param prefix 缓存键前缀
     * @param key    业务键
     * @return 是否删除成功
     */
    public Boolean delete(KeyPrefix prefix, String key) {
        return redisTemplate.delete(prefix.getPrefix() + key);
    }

    /**
     * 删除仅使用前缀作为 key 的缓存（如无业务键的 Hash）
     *
     * @param prefix 缓存键前缀
     * @return 是否删除成功
     */
    public Boolean delete(KeyPrefix prefix) {
        return redisTemplate.delete(prefix.getPrefix());
    }

    /**
     * 批量删除多个 key（一次 Redis 请求，适用于大批量缓存失效场景）
     *
     * @param prefix 缓存键前缀
     * @param keys   key 列表
     * @return 实际删除的 key 数量
     */
    public Long deleteBatch(KeyPrefix prefix, List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        List<String> realKeys = keys.stream()
                .map(k -> prefix.getPrefix() + k)
                .toList();
        return redisTemplate.delete(realKeys);
    }

    /**
     * 删除所有匹配指定前缀的 key（支持通配符）
     *
     * @param prefix 缓存键前缀
     * @return 删除的 key 数量
     */
    public Long deleteByPattern(KeyPrefix prefix) {
        String pattern = prefix.getPrefix() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            return redisTemplate.delete(keys);
        }
        return 0L;
    }

    /**
     * 尝试获取分布式锁（基于 SETNX + 过期时间）
     *
     * @param lockKey       锁的 Key
     * @param expireSeconds 过期时间（秒），防止持有者异常退出导致死锁
     * @return 是否获取成功
     */
    public Boolean tryLock(String lockKey, long expireSeconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", expireSeconds, TimeUnit.SECONDS));
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁的 Key
     */
    public void unlock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }
}

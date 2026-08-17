package com.xdzn.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * RedisConfig
 * <p>
 * Redis 相关配置：
 * <ul>
 *     <li>声明通用 {@link RedisTemplate}（key 字符串序列化、value JSON 序列化，供 RedisService 使用）</li>
 *     <li>显式声明 {@link StringRedisTemplate}（键值均以字符串存取）</li>
 *     <li>启用 Spring Cache 并声明 {@link CacheManager}，为各业务缓存（members/projects 等）
 *         配置独立的过期时间，key 使用字符串序列化、value 使用 JSON 序列化</li>
 * </ul>
 * <p>
 * 说明：项目内手动 Redis 读写统一通过 {@link com.xdzn.redis.RedisService}，
 * 本类仅服务于声明式 Spring Cache（{@code @Cacheable}/{@code @CacheEvict}）。
 *
 * @author xdzn
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 创建供 Redis 值序列化使用的 ObjectMapper（私有方法，不注册为全局 Bean）
     * <p>
     * 注册 JavaTimeModule 以支持 Java 8 日期时间类型（LocalDateTime 等），
     * 将日期序列化为字符串而非时间戳；并开启默认类型信息（default typing），
     * 保证 {@link GenericJackson2JsonRedisSerializer} 反序列化时能还原具体类型。
     * <p>
     * ⚠️ 必须为私有方法：若注册为 {@code @Bean} 会覆盖 Spring Boot 自动配置的
     * 全局 ObjectMapper，导致所有 HTTP JSON 响应带 {@code @class} 类型包装。
     *
     * @return ObjectMapper 实例
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 保留类型信息，保证反序列化时还原具体类型
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    /**
     * 声明 StringRedisTemplate 的 Key/Value 序列化器为字符串
     *
     * @param factory Redis 连接工厂
     * @return 字符串 Redis 模板
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

    /**
     * 声明通用 RedisTemplate
     * <p>
     * key 使用字符串序列化，value 使用 JSON 序列化（保留类型信息）。
     * 供 {@link com.xdzn.redis.RedisService} 使用，保证 key 的存储形式与
     * StringRedisTemplate 一致（纯字符串），避免默认 Jdk 序列化导致 key 乱码
     * 及跨模板操作（写入/删除）不一致的问题。
     *
     * @param factory Redis 连接工厂
     * @return 通用 Redis 模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        // key 与 Hash 字段名使用字符串序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        // value 与 Hash 值使用 JSON 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 声明 Redis 缓存管理器
     * <p>
     * 默认缓存 TTL 30 分钟；members/projects/techStack/timeline/testimonials 缓存 TTL 10 分钟，
     * dashboard 缓存 TTL 5 分钟。key 用字符串序列化，value 用 JSON 序列化（保留类型信息）。
     *
     * @param factory Redis 连接工厂
     * @return Redis 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("members",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .withCacheConfiguration("projects",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .withCacheConfiguration("techStack",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .withCacheConfiguration("timeline",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .withCacheConfiguration("testimonials",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .withCacheConfiguration("dashboard",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(stringSerializer))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)))
                .build();
    }
}

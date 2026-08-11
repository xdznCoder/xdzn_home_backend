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
     * 创建配置了 JSR-310 时间模块与类型信息的 ObjectMapper，
     * 供 Redis 值序列化使用（否则 LocalDateTime 等字段无法序列化）
     *
     * @return 配置完成的 ObjectMapper
     */
    private ObjectMapper redisObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // 支持 Java 8 日期时间类型（LocalDateTime 等）
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 保留类型信息，保证反序列化时还原具体类型
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return om;
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
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());
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
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper())));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("members",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .withCacheConfiguration("projects",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .withCacheConfiguration("techStack",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .withCacheConfiguration("timeline",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .withCacheConfiguration("testimonials",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .withCacheConfiguration("dashboard",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5))
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()))))
                .build();
    }
}

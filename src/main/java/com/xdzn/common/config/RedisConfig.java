package com.xdzn.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig
 * <p>
 * Redis 相关配置：
 * <ul>
 *     <li>声明通用 {@link RedisTemplate}（key 字符串序列化、value JSON 序列化，供 RedisService 使用）</li>
 *     <li>显式声明 {@link StringRedisTemplate}（键值均以字符串存取）</li>
 * </ul>
 * <p>
 * 项目内所有 Redis 读写（含业务缓存）统一通过 {@link com.xdzn.redis.RedisService}，
 * 业务缓存的 key / 过期时间由 {@link com.xdzn.redis.key.CacheRedisKey} 统一管理。
 *
 * @author xdzn
 */
@Configuration
public class RedisConfig {

    /**
     * 声明专供 Redis 使用的 ObjectMapper
     * <p>
     * 注册 JavaTimeModule 以支持 Java 8 日期时间类型（LocalDateTime 等），
     * 将日期序列化为字符串而非时间戳；并开启默认类型信息（default typing），
     * 保证 {@link GenericJackson2JsonRedisSerializer} 反序列化时能还原具体类型。
     * <p>
     * ⚠️ 必须为私有方法：Spring Boot 的 ObjectMapper 自动配置按「类型」判断
     * {@code @ConditionalOnMissingBean(ObjectMapper.class)}，任何 {@code @Bean ObjectMapper}
     * 都会覆盖全局序列化器，导致所有 HTTP JSON 响应带 {@code @class} 类型包装。
     * 仅 Redis 序列化器调用本方法即可。
     *
     * @return ObjectMapper 实例
     */
    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 保留类型信息，保证 Redis 反序列化还原具体类型（GenericJackson2JsonRedisSerializer 依赖）
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

}

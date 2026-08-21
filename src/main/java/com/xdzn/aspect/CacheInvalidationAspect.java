package com.xdzn.aspect;

import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.CacheRedisKey;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * CacheInvalidationAspect
 * <p>
 * 看板缓存失效切面：当任一业务服务的 create/update/delete 方法执行成功后，
 * 主动删除「管理看板」的统一缓存 key（{@link CacheRedisKey#DASHBOARD}）。
 * <p>
 * 看板数据是各业务表统计结果的聚合，任何数据变更都可能导致其过期，
 * 因此通过切面统一失效，避免在各业务实现中重复编写删除逻辑。
 *
 * @author xdzn
 */
@Aspect
@Component
public class CacheInvalidationAspect {

    /**
     * 统一 Redis 缓存服务
     */
    private final RedisService redisService;

    /**
     * 构造注入缓存服务
     *
     * @param redisService 统一 Redis 缓存服务
     */
    public CacheInvalidationAspect(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 后置通知：在任意 service 的 create/update/delete 方法执行后删除看板缓存
     *
     * @param joinPoint 连接点（此处未使用，仅用于触发通知）
     */
    @After("execution(* com.xdzn.service.*.create*(..)) || " +
           "execution(* com.xdzn.service.*.update*(..)) || " +
           "execution(* com.xdzn.service.*.delete*(..))")
    public void evictDashboard(JoinPoint joinPoint) {
        redisService.delete(CacheRedisKey.DASHBOARD, "summary");
    }
}

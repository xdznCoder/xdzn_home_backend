package com.xdzn.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * CacheInvalidationAspect
 * <p>
 * 缓存失效切面：当任一业务服务的 create/update/delete 方法执行成功后，
 * 主动清理名为 {@code dashboard} 的看板缓存。
 * <p>
 * 看板数据是各业务表统计结果的聚合，任何数据变更都可能导致其过期，
 * 因此通过切面统一失效，避免在各业务实现中重复编写清理逻辑。
 *
 * @author xdzn
 */
@Aspect
@Component
public class CacheInvalidationAspect {

    /**
     * Spring 缓存管理器，用于获取并清空指定缓存
     */
    private final CacheManager cacheManager;

    /**
     * 构造注入缓存管理器
     *
     * @param cacheManager 缓存管理器
     */
    public CacheInvalidationAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 后置通知：在任意 service 的 create/update/delete 方法执行后清空 dashboard 缓存
     *
     * @param joinPoint 连接点（此处未使用，仅用于触发通知）
     */
    @After("execution(* com.xdzn.service.*.create*(..)) || " +
           "execution(* com.xdzn.service.*.update*(..)) || " +
           "execution(* com.xdzn.service.*.delete*(..))")
    public void evictDashboard(JoinPoint joinPoint) {
        Cache cache = cacheManager.getCache("dashboard");
        if (cache != null) {
            cache.clear();
        }
    }
}

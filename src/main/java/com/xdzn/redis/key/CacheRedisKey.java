package com.xdzn.redis.key;

/**
 * CacheRedisKey
 * <p>
 * 业务缓存 key 枚举：统一管理各公开查询数据的缓存前缀与过期时间。
 * <p>
 * 过期时间约定：内容列表/详情 10 分钟，看板 5 分钟；
 * 实际写入时 {@code RedisService.set} 会在基础上叠加 0~300s 随机偏移（防雪崩）。
 * <p>
 * 缓存失效策略：各业务写操作（create/update/delete）后由 Service 手动删除对应 key
 * （列表 key + 详情 key），保持缓存一致。
 *
 * @author xdzn
 */
public enum CacheRedisKey implements KeyPrefix {

    /**
     * 成员列表（cache:members:all）
     */
    MEMBERS("cache:members:", 10 * 60),

    /**
     * 成员详情（cache:members:detail:{id}）
     */
    MEMBERS_DETAIL("cache:members:detail:", 10 * 60),

    /**
     * 项目列表
     */
    PROJECTS("cache:projects:", 10 * 60),

    /**
     * 项目详情
     */
    PROJECTS_DETAIL("cache:projects:detail:", 10 * 60),

    /**
     * 技术栈列表
     */
    TECH_STACK("cache:tech-stack:", 10 * 60),

    /**
     * 技术栈详情
     */
    TECH_STACK_DETAIL("cache:tech-stack:detail:", 10 * 60),

    /**
     * 时间线列表
     */
    TIMELINE("cache:timeline:", 10 * 60),

    /**
     * 时间线详情
     */
    TIMELINE_DETAIL("cache:timeline:detail:", 10 * 60),

    /**
     * 评价列表
     */
    TESTIMONIALS("cache:testimonials:", 10 * 60),

    /**
     * 评价详情
     */
    TESTIMONIALS_DETAIL("cache:testimonials:detail:", 10 * 60),

    /**
     * 管理看板（cache:dashboard:summary）
     */
    DASHBOARD("cache:dashboard:", 5 * 60),

    /**
     * 用户角色（cache:user-role:{userId}，鉴权频繁查询角色用）
     */
    USER_ROLE("cache:user-role:", 30 * 60);

    /**
     * key 前缀
     */
    private final String prefix;

    /**
     * 过期秒数（>0 时写入叠加随机偏移防雪崩）
     */
    private final int expireSeconds;

    /**
     * 构造枚举
     *
     * @param prefix         key 前缀
     * @param expireSeconds 过期秒数
     */
    CacheRedisKey(String prefix, int expireSeconds) {
        this.prefix = prefix;
        this.expireSeconds = expireSeconds;
    }

    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}

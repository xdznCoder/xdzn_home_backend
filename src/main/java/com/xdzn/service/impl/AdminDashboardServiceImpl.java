package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xdzn.mapper.FinanceRecordMapper;
import com.xdzn.mapper.JoinSubmissionMapper;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.mapper.ProjectMapper;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.mapper.TestimonialMapper;
import com.xdzn.mapper.TimelineEventMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.DashboardVO;
import com.xdzn.model.entity.FinanceRecord;
import com.xdzn.model.entity.JoinSubmission;
import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.CacheRedisKey;
import com.xdzn.service.AdminDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminDashboardServiceImpl
 * <p>
 * 管理后台看板服务实现。
 * <p>
 * 统计方式：
 * <ul>
 *     <li>各业务表数据量使用 MyBatis-Plus {@code selectCount} 直接统计</li>
 *     <li>报名状态分布通过一次 {@code GROUP BY status} 聚合查询得出，避免多次计数</li>
 *     <li>今日/本月报名按 {@code created_at} 时间范围过滤</li>
 * </ul>
 * 缓存策略：
 * <ul>
 *     <li>结果经 Spring Cache 缓存（缓存名 {@code dashboard}，TTL 5 分钟）</li>
 *     <li>内容/报名写操作由 {@link com.xdzn.aspect.CacheInvalidationAspect} 即时失效，
 *         保证数据更新后看板立即可见；TTL 作为兜底防数据长期过期</li>
 *     <li>{@code unless = "#result == null"} 避免缓存空结果（看板结果恒非空，为防御性保留）</li>
 * </ul>
 *
 * @author xdzn
 */
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    /**
     * 成员表 Mapper
     */
    private final MemberMapper memberMapper;

    /**
     * 项目表 Mapper
     */
    private final ProjectMapper projectMapper;

    /**
     * 技术栈表 Mapper
     */
    private final TechStackItemMapper techStackMapper;

    /**
     * 时间线表 Mapper
     */
    private final TimelineEventMapper timelineMapper;

    /**
     * 评价表 Mapper
     */
    private final TestimonialMapper testimonialMapper;

    /**
     * 报名表 Mapper
     */
    private final JoinSubmissionMapper joinMapper;

    /**
     * 用户表 Mapper
     */
    private final UserMapper userMapper;

    /**
     * 经费收支表 Mapper
     */
    private final FinanceRecordMapper financeMapper;

    /**
     * 统一 Redis 缓存服务
     */
    private final RedisService redisService;

    /**
     * 构造注入全部业务表 Mapper 与缓存服务
     *
     * @param memberMapper      成员表 Mapper
     * @param projectMapper     项目表 Mapper
     * @param techStackMapper   技术栈表 Mapper
     * @param timelineMapper    时间线表 Mapper
     * @param testimonialMapper 评价表 Mapper
     * @param joinMapper        报名表 Mapper
     * @param userMapper        用户表 Mapper
     * @param financeMapper     经费收支表 Mapper
     * @param redisService      统一 Redis 缓存服务
     */
    public AdminDashboardServiceImpl(MemberMapper memberMapper,
                                     ProjectMapper projectMapper,
                                     TechStackItemMapper techStackMapper,
                                     TimelineEventMapper timelineMapper,
                                     TestimonialMapper testimonialMapper,
                                     JoinSubmissionMapper joinMapper,
                                     UserMapper userMapper,
                                     FinanceRecordMapper financeMapper,
                                     RedisService redisService) {
        this.memberMapper = memberMapper;
        this.projectMapper = projectMapper;
        this.techStackMapper = techStackMapper;
        this.timelineMapper = timelineMapper;
        this.testimonialMapper = testimonialMapper;
        this.joinMapper = joinMapper;
        this.userMapper = userMapper;
        this.financeMapper = financeMapper;
        this.redisService = redisService;
    }

    /**
     * 获取看板汇总数据（结果缓存 5 分钟，写操作即时失效）
     *
     * @return 看板视图对象
     */
    @Override
    public DashboardVO getDashboard() {
        return redisService.getOrSet(CacheRedisKey.DASHBOARD, "summary", DashboardVO.class, this::doGetDashboard);
    }

    /**
     * 从数据库聚合看板数据，供缓存回填
     *
     * @return 看板视图对象
     */
    private DashboardVO doGetDashboard() {
        // ── 内容统计 ──
        long members = memberMapper.selectCount(null);
        long projects = projectMapper.selectCount(null);
        long techStack = techStackMapper.selectCount(null);
        long timeline = timelineMapper.selectCount(null);
        long testimonials = testimonialMapper.selectCount(null);
        long users = userMapper.selectCount(null);

        // ── 报名统计：一次 GROUP BY 聚合得各状态数量 ──
        List<Map<String, Object>> statusRows = joinMapper.selectMaps(
                new QueryWrapper<JoinSubmission>()
                        .select("status", "count(*) AS cnt")
                        .groupBy("status"));

        Map<String, Long> statusMap = new HashMap<>();
        for (Map<String, Object> row : statusRows) {
            Object status = row.get("status");
            Object cnt = row.get("cnt");
            if (status != null && cnt != null) {
                statusMap.put(String.valueOf(status), ((Number) cnt).longValue());
            }
        }
        long joinSubmissions = statusMap.values().stream().mapToLong(Long::longValue).sum();
        long pendingJoins = statusMap.getOrDefault("pending", 0L);
        long contactedJoins = statusMap.getOrDefault("contacted", 0L);
        long acceptedJoins = statusMap.getOrDefault("accepted", 0L);
        long rejectedJoins = statusMap.getOrDefault("rejected", 0L);

        // ── 时间维度：今日 / 本月报名 ──
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long todayJoins = joinMapper.selectCount(
                new LambdaQueryWrapper<JoinSubmission>().ge(JoinSubmission::getCreatedAt, startOfToday));
        long monthJoins = joinMapper.selectCount(
                new LambdaQueryWrapper<JoinSubmission>().ge(JoinSubmission::getCreatedAt, startOfMonth));

        // ── 最近 5 条报名 ──
        List<JoinSubmission> recentJoins = joinMapper.selectList(
                new LambdaQueryWrapper<JoinSubmission>()
                        .orderByDesc(JoinSubmission::getCreatedAt)
                        .last("LIMIT 5"));

        // ── 经费统计：一次 GROUP BY type 聚合出进账/支出，余额 = 进账 - 支出 ──
        BigDecimal fundBalance = BigDecimal.ZERO;
        List<Map<String, Object>> fundRows = financeMapper.selectMaps(
                new QueryWrapper<FinanceRecord>()
                        .select("type", "COALESCE(SUM(amount), 0) AS total")
                        .groupBy("type"));
        for (Map<String, Object> row : fundRows) {
            Object type = row.get("type");
            Object total = row.get("total");
            if (type == null || total == null) {
                continue;
            }
            BigDecimal amount = new BigDecimal(String.valueOf(total));
            if ("income".equals(type)) {
                fundBalance = fundBalance.add(amount);
            } else if ("expense".equals(type)) {
                fundBalance = fundBalance.subtract(amount);
            }
        }

        return DashboardVO.builder()
                .stats(DashboardVO.DashboardStats.builder()
                        .members(members)
                        .projects(projects)
                        .techStack(techStack)
                        .timeline(timeline)
                        .testimonials(testimonials)
                        .joinSubmissions(joinSubmissions)
                        .pendingJoins(pendingJoins)
                        .contactedJoins(contactedJoins)
                        .acceptedJoins(acceptedJoins)
                        .rejectedJoins(rejectedJoins)
                        .todayJoins(todayJoins)
                        .monthJoins(monthJoins)
                        .users(users)
                        .fundBalance(fundBalance)
                        .build())
                .recentJoins(recentJoins)
                .build();
    }
}

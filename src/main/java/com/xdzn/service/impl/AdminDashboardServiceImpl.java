package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xdzn.mapper.JoinSubmissionMapper;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.mapper.ProjectMapper;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.mapper.TestimonialMapper;
import com.xdzn.mapper.TimelineEventMapper;
import com.xdzn.model.dto.DashboardVO;
import com.xdzn.model.entity.JoinSubmission;
import com.xdzn.service.AdminDashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AdminDashboardServiceImpl
 * <p>
 * 管理后台看板服务实现，统计各业务表数据量并获取最近报名记录，
 * 结果通过 Spring Cache 缓存（缓存名 {@code dashboard}，TTL 5 分钟）。
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
     * 构造注入全部业务表 Mapper
     *
     * @param memberMapper       成员表 Mapper
     * @param projectMapper      项目表 Mapper
     * @param techStackMapper    技术栈表 Mapper
     * @param timelineMapper     时间线表 Mapper
     * @param testimonialMapper  评价表 Mapper
     * @param joinMapper         报名表 Mapper
     */
    public AdminDashboardServiceImpl(MemberMapper memberMapper,
                                     ProjectMapper projectMapper,
                                     TechStackItemMapper techStackMapper,
                                     TimelineEventMapper timelineMapper,
                                     TestimonialMapper testimonialMapper,
                                     JoinSubmissionMapper joinMapper) {
        this.memberMapper = memberMapper;
        this.projectMapper = projectMapper;
        this.techStackMapper = techStackMapper;
        this.timelineMapper = timelineMapper;
        this.testimonialMapper = testimonialMapper;
        this.joinMapper = joinMapper;
    }

    /**
     * 获取看板汇总数据
     * <p>
     * 统计各业务表总记录数、待处理报名数，并查询最近 5 条报名记录。
     *
     * @return 看板视图对象
     */
    @Override
    @Cacheable(value = "dashboard", key = "'summary'", unless = "#result == null")
    public DashboardVO getDashboard() {
        long members = memberMapper.selectCount(null);
        long projects = projectMapper.selectCount(null);
        long techStack = techStackMapper.selectCount(null);
        long timeline = timelineMapper.selectCount(null);
        long testimonials = testimonialMapper.selectCount(null);
        long joinSubmissions = joinMapper.selectCount(null);
        long pendingJoins = joinMapper.selectCount(
                new LambdaQueryWrapper<JoinSubmission>()
                        .eq(JoinSubmission::getStatus, "pending"));

        // 最近 5 条报名
        List<JoinSubmission> recentJoins = joinMapper.selectList(
                new LambdaQueryWrapper<JoinSubmission>()
                        .orderByDesc(JoinSubmission::getCreatedAt)
                        .last("LIMIT 5"));

        return DashboardVO.builder()
                .stats(DashboardVO.DashboardStats.builder()
                        .members(members)
                        .projects(projects)
                        .techStack(techStack)
                        .timeline(timeline)
                        .testimonials(testimonials)
                        .joinSubmissions(joinSubmissions)
                        .pendingJoins(pendingJoins)
                        .build())
                .recentJoins(recentJoins)
                .build();
    }
}

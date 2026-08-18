package com.xdzn.model.dto;

import com.xdzn.model.entity.JoinSubmission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DashboardVO
 * <p>
 * 管理后台首页看板数据 DTO，聚合各业务表的统计数据与最近报名记录。
 * <p>
 * 统计维度涵盖：内容规模（成员/项目/技术栈/时间线/评价）、
 * 报名规模与状态分布（待处理/已联系/已通过/已拒绝）、
 * 时间维度（今日/本月报名）、注册用户数。
 *
 * @author xdzn
 */
@Data
@Builder
public class DashboardVO {

    /**
     * 各业务维度的统计数据
     */
    private DashboardStats stats;

    /**
     * 最近 5 条招新报名记录
     */
    private List<JoinSubmission> recentJoins;

    /**
     * DashboardStats
     * <p>
     * 看板统计数据项，按内容 / 报名 / 用户三个维度组织。
     */
    @Data
    @Builder
    public static class DashboardStats {

        // ── 内容统计 ──

        /**
         * 团队成员总数
         */
        private long members;

        /**
         * 项目总数
         */
        private long projects;

        /**
         * 技术栈条目总数
         */
        private long techStack;

        /**
         * 时间线事件总数
         */
        private long timeline;

        /**
         * 用户评价总数
         */
        private long testimonials;

        // ── 报名统计 ──

        /**
         * 招新报名总数（所有状态之和）
         */
        private long joinSubmissions;

        /**
         * 待处理报名数
         */
        private long pendingJoins;

        /**
         * 已联系报名数
         */
        private long contactedJoins;

        /**
         * 已通过报名数
         */
        private long acceptedJoins;

        /**
         * 已拒绝报名数
         */
        private long rejectedJoins;

        /**
         * 今日新增报名数
         */
        private long todayJoins;

        /**
         * 本月新增报名数
         */
        private long monthJoins;

        // ── 用户统计 ──

        /**
         * 注册用户总数（含管理员）
         */
        private long users;
    }
}

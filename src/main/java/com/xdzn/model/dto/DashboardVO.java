package com.xdzn.model.dto;

import com.xdzn.model.entity.JoinSubmission;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DashboardVO
 * <p>
 * 管理后台首页看板数据 DTO，聚合各业务表的统计数据与最近报名记录。
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
     * 看板统计数据项，记录各业务表的总量与待处理数量。
     */
    @Data
    @Builder
    public static class DashboardStats {

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

        /**
         * 招新报名总数
         */
        private long joinSubmissions;

        /**
         * 待处理的招新报名数量
         */
        private long pendingJoins;
    }
}

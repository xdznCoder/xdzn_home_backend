package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.DashboardVO;
import com.xdzn.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminDashboardController
 * <p>
 * 管理后台看板接口：聚合展示各业务表统计数据与最近报名记录。
 * 接口路径受 Sa-Token 拦截，仅允许登录的管理员访问。
 *
 * @author xdzn
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    /**
     * 看板服务
     */
    private final AdminDashboardService dashboardService;

    /**
     * 构造注入看板服务
     *
     * @param dashboardService 看板服务
     */
    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取看板汇总数据
     *
     * @return 看板数据（统计信息 + 最近报名）
     */
    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return Result.ok(dashboardService.getDashboard());
    }
}

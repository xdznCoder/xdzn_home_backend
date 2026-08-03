package com.xdzn.service;

import com.xdzn.model.dto.DashboardVO;

/**
 * AdminDashboardService
 * <p>
 * 管理后台看板服务接口，定义看板汇总数据获取的契约。
 *
 * @author xdzn
 */
public interface AdminDashboardService {

    /**
     * 获取看板汇总数据（各业务表统计 + 最近报名记录）
     *
     * @return 看板视图对象
     */
    DashboardVO getDashboard();
}

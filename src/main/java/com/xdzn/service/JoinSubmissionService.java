package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.entity.JoinSubmission;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * JoinSubmissionService
 * <p>
 * 招新报名服务接口，定义报名创建、查询、状态更新与删除的契约。
 *
 * @author xdzn
 */
public interface JoinSubmissionService extends IService<JoinSubmission> {

    /**
     * 创建报名（状态初始化为 pending）
     *
     * @param submission 报名信息
     * @return 创建后的报名记录
     */
    JoinSubmission create(JoinSubmission submission);

    /**
     * 查询全部报名（按提交时间倒序）
     *
     * @return 报名记录列表
     */
    List<JoinSubmission> findAll();

    /**
     * 更新报名处理状态
     *
     * @param id     报名记录 id
     * @param status 目标状态
     * @return 更新后的报名记录；记录不存在时返回 null
     */
    JoinSubmission updateStatus(Long id, String status);

    /**
     * 删除报名记录
     *
     * @param id 报名记录 id
     */
    void delete(Long id);

    /**
     * 导出报名列表到 Excel
     *
     * @param response HTTP 响应
     */
    void export(HttpServletResponse response);
}

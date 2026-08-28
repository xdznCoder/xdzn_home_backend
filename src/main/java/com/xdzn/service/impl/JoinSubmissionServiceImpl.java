package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.JoinSubmissionMapper;
import com.xdzn.model.dto.JoinExcelRow;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.JoinSubmission;
import com.xdzn.service.JoinSubmissionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JoinSubmissionServiceImpl
 * <p>
 * 招新报名服务实现，提供报名创建、查询、状态更新与删除。
 * 新建报名默认状态为 {@code pending}（待处理）。
 *
 * @author xdzn
 */
@Service
public class JoinSubmissionServiceImpl extends ServiceImpl<JoinSubmissionMapper, JoinSubmission>
        implements JoinSubmissionService {

    /**
     * 通用 Excel 导出服务
     */
    private final ExcelService excelService;

    /**
     * 构造注入依赖
     *
     * @param excelService 通用 Excel 导出服务
     */
    public JoinSubmissionServiceImpl(ExcelService excelService) {
        this.excelService = excelService;
    }

    /**
     * 创建报名记录，状态初始化为 pending
     *
     * @param submission 报名信息
     * @return 创建后的报名记录
     */
    @Override
    public JoinSubmission create(JoinSubmission submission) {
        submission.setStatus("pending");
        save(submission);
        return submission;
    }

    /**
     * 查询全部报名记录（按提交时间倒序）
     *
     * @return 报名记录列表
     */
    @Override
    public List<JoinSubmission> findAll() {
        return lambdaQuery().orderByDesc(JoinSubmission::getCreatedAt).list();
    }

    /**
     * 分页查询报名记录（按提交时间倒序，支持状态筛选）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param status  状态筛选（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<JoinSubmission> findAllByPage(long current, long size, String status) {
        Page<JoinSubmission> page = new Page<>(current, size);
        LambdaQueryWrapper<JoinSubmission> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(JoinSubmission::getStatus, status);
        }
        wrapper.orderByDesc(JoinSubmission::getCreatedAt);
        Page<JoinSubmission> result = page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    /**
     * 更新报名处理状态
     *
     * @param id     报名记录 id
     * @param status 目标状态
     * @return 更新后的报名记录；记录不存在时返回 null
     */
    @Override
    public JoinSubmission updateStatus(Long id, String status) {
        JoinSubmission submission = getById(id);
        if (submission != null) {
            submission.setStatus(status);
            updateById(submission);
        }
        return submission;
    }

    /**
     * 删除报名记录
     *
     * @param id 报名记录 id
     */
    @Override
    public void delete(Long id) {
        removeById(id);
    }

    /**
     * 导出报名列表到 Excel
     *
     * @param response HTTP 响应
     */
    @Override
    public void export(HttpServletResponse response) {
        List<JoinSubmission> all = findAll();
        List<JoinExcelRow> rows = all.stream().map(this::toExcelRow).collect(Collectors.toList());
        excelService.export(response, rows, JoinExcelRow.class, "报名", "招新报名");
    }

    /**
     * 报名转导出行
     *
     * @param submission 报名实体
     * @return 导出行
     */
    private JoinExcelRow toExcelRow(JoinSubmission submission) {
        JoinExcelRow row = new JoinExcelRow();
        row.setName(submission.getName());
        row.setGrade(submission.getGrade());
        row.setDirection(submission.getDirection());
        row.setStatus(statusText(submission.getStatus()));
        row.setCreatedAt(submission.getCreatedAt() != null ? submission.getCreatedAt().toString() : "");
        return row;
    }

    /**
     * 报名状态转中文
     *
     * @param status 状态
     * @return 中文
     */
    private String statusText(String status) {
        return switch (status == null ? "pending" : status) {
            case "contacted" -> "已联系";
            case "accepted" -> "已通过";
            case "rejected" -> "已拒绝";
            default -> "待处理";
        };
    }
}

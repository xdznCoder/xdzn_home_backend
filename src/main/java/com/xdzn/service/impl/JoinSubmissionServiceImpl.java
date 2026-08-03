package com.xdzn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.JoinSubmissionMapper;
import com.xdzn.model.entity.JoinSubmission;
import com.xdzn.service.JoinSubmissionService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.mapper.TaskAssigneeMapper;
import com.xdzn.mapper.TaskMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TaskDto;
import com.xdzn.model.entity.Member;
import com.xdzn.model.entity.Task;
import com.xdzn.model.entity.TaskAssignee;
import com.xdzn.model.entity.User;
import com.xdzn.model.vo.TaskAssigneeVO;
import com.xdzn.model.vo.TaskVO;
import com.xdzn.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TaskServiceImpl
 * <p>
 * 任务服务实现，提供任务创建（指派单人/多人）、更新、删除、分页筛选、
 * 成员查看自己的任务与状态流转。
 * <p>
 * 权限约定：
 * <ul>
 *     <li>创建/更新/删除/全量分页：需 admin（路由层校验）</li>
 *     <li>我的任务：当前登录成员</li>
 *     <li>状态更新：负责人（admin）或该任务被指派成员</li>
 * </ul>
 *
 * @author xdzn
 */
@Service
public class TaskServiceImpl implements TaskService {

    /**
     * 任务表 Mapper
     */
    private final TaskMapper taskMapper;

    /**
     * 任务-成员关联表 Mapper
     */
    private final TaskAssigneeMapper assigneeMapper;

    /**
     * 成员表 Mapper（查成员姓名/头像、当前用户对应成员）
     */
    private final MemberMapper memberMapper;

    /**
     * 用户表 Mapper（查创建人姓名）
     */
    private final UserMapper userMapper;

    /**
     * 合法状态集合
     */
    private static final Set<String> VALID_STATUS = Set.of("todo", "in_progress", "done");

    /**
     * 构造注入依赖
     *
     * @param taskMapper      任务表 Mapper
     * @param assigneeMapper  任务-成员关联表 Mapper
     * @param memberMapper    成员表 Mapper
     * @param userMapper      用户表 Mapper
     */
    public TaskServiceImpl(TaskMapper taskMapper,
                           TaskAssigneeMapper assigneeMapper,
                           MemberMapper memberMapper,
                           UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.assigneeMapper = assigneeMapper;
        this.memberMapper = memberMapper;
        this.userMapper = userMapper;
    }

    /**
     * 创建任务并指派成员
     *
     * @param dto 任务DTO
     * @return 任务视图
     */
    @Override
    @Transactional
    public TaskVO create(TaskDto dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setCreatorId(StpUtil.getLoginIdAsLong());
        task.setStatus("todo");
        if (task.getPriority() == null) {
            task.setPriority("medium");
        }
        taskMapper.insert(task);
        syncAssignees(task.getId(), dto.getAssigneeIds());
        return toVO(taskMapper.selectById(task.getId()));
    }

    /**
     * 更新任务并重新指派成员
     *
     * @param id  任务 id
     * @param dto 任务DTO
     * @return 更新后的任务视图
     */
    @Override
    @Transactional
    public TaskVO update(Long id, TaskDto dto) {
        Task task = new Task();
        task.setId(id);
        BeanUtils.copyProperties(dto, task);
        taskMapper.updateById(task);
        syncAssignees(id, dto.getAssigneeIds());
        return toVO(taskMapper.selectById(id));
    }

    /**
     * 删除任务及其指派关系
     *
     * @param id 任务 id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        assigneeMapper.delete(new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getTaskId, id));
        taskMapper.deleteById(id);
    }

    /**
     * 根据 id 查询任务详情
     *
     * @param id 任务 id
     * @return 任务视图；不存在时返回 null
     */
    @Override
    public TaskVO findById(Long id) {
        Task task = taskMapper.selectById(id);
        return task == null ? null : toVO(task);
    }

    /**
     * 分页查询任务（按状态、指派成员筛选）
     *
     * @param current  当前页码
     * @param size     每页大小
     * @param status   任务状态（可选）
     * @param memberId 指派成员 id（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<TaskVO> findAllByPage(long current, long size, String status, Long memberId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Task::getStatus, status);
        }
        if (memberId != null) {
            List<Long> taskIds = assigneeMapper.selectList(
                            new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getMemberId, memberId))
                    .stream().map(TaskAssignee::getTaskId).toList();
            if (taskIds.isEmpty()) {
                return new PageResult<>(current, size, 0, 0, List.of());
            }
            wrapper.in(Task::getId, taskIds);
        }
        wrapper.orderByDesc(Task::getDueDate);

        Page<Task> result = taskMapper.selectPage(new Page<>(current, size), wrapper);
        List<TaskVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 查询当前登录成员被指派的任务
     *
     * @return 任务视图列表
     */
    @Override
    public List<TaskVO> findMyTasks() {
        Long myMemberId = getCurrentMemberId();
        if (myMemberId == null) {
            return List.of();
        }
        List<Long> taskIds = assigneeMapper.selectList(
                        new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getMemberId, myMemberId))
                .stream().map(TaskAssignee::getTaskId).toList();
        if (taskIds.isEmpty()) {
            return List.of();
        }
        return taskMapper.selectBatchIds(taskIds).stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 更新任务状态（负责人或该任务被指派成员可操作）
     *
     * @param id     任务 id
     * @param status 目标状态
     * @return 更新后的任务视图
     */
    @Override
    public TaskVO updateStatus(Long id, String status) {
        if (status == null || !VALID_STATUS.contains(status)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "状态仅允许 todo/in_progress/done");
        }
        // 权限：负责人（captain 队长）或该任务被指派成员
        if (!StpUtil.hasRole("captain")) {
            Long myMemberId = getCurrentMemberId();
            boolean isAssignee = myMemberId != null && assigneeMapper.selectCount(
                    new LambdaQueryWrapper<TaskAssignee>()
                            .eq(TaskAssignee::getTaskId, id)
                            .eq(TaskAssignee::getMemberId, myMemberId)) > 0;
            if (!isAssignee) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "无权操作该任务");
            }
        }
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        taskMapper.updateById(task);
        return toVO(taskMapper.selectById(id));
    }

    // ── 内部方法 ──────────────────────

    /**
     * 同步任务指派关系（先删旧再插新）
     *
     * @param taskId    任务 id
     * @param memberIds 指派成员 id 列表（可为空）
     */
    private void syncAssignees(Long taskId, List<Long> memberIds) {
        assigneeMapper.delete(new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getTaskId, taskId));
        if (memberIds != null && !memberIds.isEmpty()) {
            for (Long memberId : memberIds) {
                TaskAssignee ta = new TaskAssignee();
                ta.setTaskId(taskId);
                ta.setMemberId(memberId);
                assigneeMapper.insert(ta);
            }
        }
    }

    /**
     * 获取当前登录用户对应的成员 id（通过 members.user_id 关联）
     *
     * @return 成员 id；未关联成员时返回 null
     */
    private Long getCurrentMemberId() {
        Long userId = StpUtil.getLoginIdAsLong();
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getUserId, userId));
        return member == null ? null : member.getId();
    }

    /**
     * 将任务实体转换为视图对象（含创建人姓名与指派成员）
     *
     * @param task 任务实体
     * @return 任务视图
     */
    private TaskVO toVO(Task task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);

        // 创建人姓名
        User creator = userMapper.selectById(task.getCreatorId());
        vo.setCreatorName(creator != null ? creator.getName() : null);

        // 指派成员
        List<TaskAssignee> assigns = assigneeMapper.selectList(
                new LambdaQueryWrapper<TaskAssignee>().eq(TaskAssignee::getTaskId, task.getId()));
        if (assigns.isEmpty()) {
            vo.setAssignees(List.of());
        } else {
            List<Long> memberIds = assigns.stream().map(TaskAssignee::getMemberId).toList();
            Map<Long, Member> memberMap = memberMapper.selectBatchIds(memberIds).stream()
                    .collect(Collectors.toMap(Member::getId, m -> m));
            vo.setAssignees(assigns.stream().map(a -> {
                TaskAssigneeVO avo = new TaskAssigneeVO();
                avo.setMemberId(a.getMemberId());
                Member m = memberMap.get(a.getMemberId());
                if (m != null) {
                    avo.setMemberName(m.getName());
                    avo.setMemberAvatar(m.getAvatar());
                }
                return avo;
            }).collect(Collectors.toList()));
        }
        return vo;
    }
}

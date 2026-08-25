package com.xdzn.service;

import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TaskDto;
import com.xdzn.model.vo.TaskVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * TaskService
 * <p>
 * 任务服务接口，定义任务创建（支持指派多人）、更新、删除、查询与状态流转的契约。
 *
 * @author xdzn
 */
public interface TaskService {

    /**
     * 创建任务并指派成员（支持单人/多人）
     *
     * @param dto 任务DTO（含 assigneeIds）
     * @return 任务视图
     */
    TaskVO create(TaskDto dto);

    /**
     * 更新任务并重新指派成员
     *
     * @param id  任务 id
     * @param dto 任务DTO
     * @return 更新后的任务视图
     */
    TaskVO update(Long id, TaskDto dto);

    /**
     * 删除任务及其指派关系
     *
     * @param id 任务 id
     */
    void delete(Long id);

    /**
     * 根据 id 查询任务详情（含指派成员）
     *
     * @param id 任务 id
     * @return 任务视图；不存在时返回 null
     */
    TaskVO findById(Long id);

    /**
     * 分页查询任务（可按状态、指派成员筛选）
     *
     * @param current  当前页码
     * @param size     每页大小
     * @param status   任务状态（可选）
     * @param memberId 指派成员 id（可选）
     * @return 分页结果
     */
    PageResult<TaskVO> findAllByPage(long current, long size, String status, Long memberId);

    /**
     * 查询当前登录成员被指派的任务
     *
     * @return 任务视图列表
     */
    List<TaskVO> findMyTasks();

    /**
     * 更新任务状态（负责人或该任务被指派成员可操作）
     *
     * @param id     任务 id
     * @param status 目标状态（todo / in_progress / done）
     * @return 更新后的任务视图
     */
    TaskVO updateStatus(Long id, String status);

    /**
     * 导出任务列表到 Excel（支持筛选条件）
     *
     * @param response HTTP 响应
     * @param status   任务状态（可选）
     * @param memberId 指派成员 id（可选）
     */
    void export(HttpServletResponse response, String status, Long memberId);
}

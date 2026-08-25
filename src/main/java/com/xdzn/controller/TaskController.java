package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TaskDto;
import com.xdzn.model.vo.TaskVO;
import com.xdzn.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * TaskController
 * <p>
 * 团队成员任务管理接口：
 * <ul>
 *     <li>负责人：分页查看全部任务、创建/更新/删除任务（可指派单人/多人）</li>
 *     <li>成员：查看自己的任务、更新自己负责任务的状态</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "任务管理接口", description = "团队成员任务：负责人布置（单人/多人指派）、成员查看与更新状态")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    /**
     * 任务服务
     */
    private final TaskService taskService;

    /**
     * 构造注入任务服务
     *
     * @param taskService 任务服务
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 分页查询任务（负责人视角，可按状态/指派成员筛选）
     *
     * @param current  当前页码（≥1）
     * @param size     每页大小（1~100）
     * @param status   任务状态（可选）
     * @param memberId 指派成员 id（可选）
     * @return 分页结果
     */
    @Operation(summary = "分页查询任务", description = "负责人查看全部任务，可按状态/指派成员筛选，需 admin 权限")
    @GetMapping("/page")
    public Result<PageResult<TaskVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size,
            @Parameter(description = "任务状态筛选（todo/in_progress/done）")
            @RequestParam(required = false)
            @Pattern(regexp = "^(todo|in_progress|done)$", message = "状态仅允许 todo/in_progress/done") String status,
            @Parameter(description = "指派成员 id 筛选")
            @RequestParam(required = false) Long memberId) {
        return Result.ok(taskService.findAllByPage(current, size, status, memberId));
    }

    /**
     * 查看当前登录成员被指派的任务
     *
     * @return 任务视图列表
     */
    @Operation(summary = "我的任务", description = "查看当前登录成员被指派的任务")
    @GetMapping("/me")
    public Result<List<TaskVO>> findMyTasks() {
        return Result.ok(taskService.findMyTasks());
    }

    /**
     * 根据 id 查询任务详情（含指派成员）
     *
     * @param id 任务 id
     * @return 任务视图；不存在时返回 404
     */
    @Operation(summary = "任务详情", description = "根据 id 查询任务（含指派成员列表）")
    @GetMapping("/{id}")
    public Result<TaskVO> findById(
            @Parameter(description = "任务 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        TaskVO vo = taskService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 创建任务并指派成员（支持单人/多人）
     *
     * @param dto 任务DTO（含 assigneeIds）
     * @return 创建后的任务视图
     */
    @Operation(summary = "创建任务", description = "布置任务并指派成员（支持单人/多人），需 admin 权限")
    @PostMapping
    public Result<TaskVO> create(@Valid @RequestBody TaskDto dto) {
        return Result.ok(taskService.create(dto));
    }

    /**
     * 更新任务并重新指派成员
     *
     * @param id  任务 id
     * @param dto 任务DTO
     * @return 更新后的任务视图
     */
    @Operation(summary = "更新任务", description = "更新任务信息与指派成员，需 admin 权限")
    @PutMapping("/{id}")
    public Result<TaskVO> update(
            @Parameter(description = "任务 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody TaskDto dto) {
        return Result.ok(taskService.update(id, dto));
    }

    /**
     * 删除任务及其指派关系
     *
     * @param id 任务 id
     * @return 操作结果
     */
    @Operation(summary = "导出任务 Excel", description = "导出全部任务（可按状态/指派成员筛选），需 admin 权限")
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @Parameter(description = "任务状态筛选（todo/in_progress/done）")
            @RequestParam(required = false)
            @Pattern(regexp = "^(todo|in_progress|done)$", message = "状态仅允许 todo/in_progress/done") String status,
            @Parameter(description = "指派成员 id 筛选")
            @RequestParam(required = false) Long memberId) {
        taskService.export(response, status, memberId);
    }

    @Operation(summary = "删除任务", description = "删除任务及其指派关系，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "任务 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        taskService.delete(id);
        return Result.ok();
    }

    /**
     * 更新任务状态（负责人或被指派成员）
     *
     * @param id   任务 id
     * @param body 请求体：{ "status": "in_progress" }
     * @return 更新后的任务视图
     */
    @Operation(summary = "更新任务状态", description = "负责人或被指派成员更新状态（todo/in_progress/done）")
    @PatchMapping("/{id}/status")
    public Result<TaskVO> updateStatus(
            @Parameter(description = "任务 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @RequestBody Map<String, String> body) {
        return Result.ok(taskService.updateStatus(id, body.get("status")));
    }
}

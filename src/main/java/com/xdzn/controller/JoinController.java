package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.CreateJoinDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.UpdateJoinStatusDto;
import com.xdzn.model.entity.JoinSubmission;
import com.xdzn.service.JoinSubmissionService;
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

/**
 * JoinController
 * <p>
 * 招新报名相关接口：提交报名、报名列表、更新处理状态、删除报名。
 * 其中提交报名（POST /api/joins）对匿名用户开放，其余管理操作需管理员权限。
 *
 * @author xdzn
 */
@Tag(name = "招新报名接口", description = "招新报名提交与后台管理（提交对匿名开放，管理操作需 admin 权限）")
@RestController
@RequestMapping("/api/joins")
public class JoinController {

    /**
     * 报名服务
     */
    private final JoinSubmissionService joinService;

    /**
     * 构造注入报名服务
     *
     * @param joinService 报名服务
     */
    public JoinController(JoinSubmissionService joinService) {
        this.joinService = joinService;
    }

    /**
     * 提交招新报名（状态初始化为 pending）
     *
     * @param dto 报名参数（姓名、年级、方向）
     * @return 创建后的报名记录
     */
    @Operation(summary = "提交招新报名", description = "匿名可调用，状态初始化为 pending，供管理员后台审核")
    @PostMapping
    public Result<JoinSubmission> create(@RequestBody @Valid CreateJoinDto dto) {
        JoinSubmission submission = new JoinSubmission();
        submission.setName(dto.getName());
        submission.setGrade(dto.getGrade());
        submission.setDirection(dto.getDirection());
        return Result.ok(joinService.create(submission));
    }

    /**
     * 查询全部报名记录（按提交时间倒序）
     *
     * @return 报名记录列表
     */
    @Operation(summary = "查询全部报名", description = "按提交时间倒序返回全部报名记录，需 admin 权限")
    @GetMapping
    public Result<List<JoinSubmission>> findAll() {
        return Result.ok(joinService.findAll());
    }

    @Operation(summary = "分页查询报名", description = "按提交时间倒序返回报名记录，支持状态筛选，需 admin 权限")
    @GetMapping("/page")
    public Result<PageResult<JoinSubmission>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size,
            @Parameter(description = "状态筛选(pending/contacted/accepted/rejected)")
            @RequestParam(required = false)
            @Pattern(regexp = "^(pending|contacted|accepted|rejected)$", message = "状态仅允许 pending/contacted/accepted/rejected") String status) {
        return Result.ok(joinService.findAllByPage(current, size, status));
    }

    /**
     * 更新报名处理状态
     *
     * @param id  报名记录 id
     * @param dto 目标状态
     * @return 更新后的报名记录；不存在时返回 404
     */
    @Operation(summary = "更新报名状态", description = "更新报名处理状态（pending/contacted/accepted/rejected），需 admin 权限")
    @PatchMapping("/{id}")
    public Result<JoinSubmission> updateStatus(
            @Parameter(description = "报名记录 id", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateJoinStatusDto dto) {
        JoinSubmission updated = joinService.updateStatus(id, dto.getStatus());
        if (updated == null) return Result.notFound();
        return Result.ok(updated);
    }

    /**
     * 根据 id 查询报名记录
     *
     * @param id 报名记录 id
     * @return 报名记录；不存在时返回 404
     */
    @Operation(summary = "查询报名详情", description = "根据 id 查询单条报名记录，需 admin 权限")
    @GetMapping("/{id}")
    public Result<JoinSubmission> findById(
            @Parameter(description = "报名记录 id", required = true, example = "1")
            @PathVariable Long id) {
        JoinSubmission byId = joinService.getById(id);
        if (byId == null) return Result.notFound();
        return Result.ok(byId);
    }

    /**
     * 删除报名记录
     *
     * @param id 报名记录 id
     * @return 操作结果
     */
    @Operation(summary = "删除报名", description = "删除报名记录，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        joinService.delete(id);
        return Result.ok();
    }

    /**
     * 导出报名列表到 Excel（需 admin 权限）
     *
     * @param response HTTP 响应
     */
    @Operation(summary = "导出报名 Excel", description = "导出全部招新报名，需 admin 权限")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        joinService.export(response);
    }
}

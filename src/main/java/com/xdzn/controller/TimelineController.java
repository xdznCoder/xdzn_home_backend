package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TimelineDto;
import com.xdzn.model.vo.TimelineVO;
import com.xdzn.service.TimelineEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TimelineController
 * <p>
 * 时间线（大事记）相关接口：事件列表、详情、增删改。
 * 返回统一为公开视图 {@link TimelineVO}（脱敏，不含审计字段）。
 *
 * @author xdzn
 */
@Validated
@Tag(name = "时间线接口", description = "官网团队历程（大事记）展示与管理的增删改查（写操作需 admin 权限）")
@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    /**
     * 时间线服务
     */
    private final TimelineEventService timelineService;

    /**
     * 构造注入时间线服务
     *
     * @param timelineService 时间线服务
     */
    public TimelineController(TimelineEventService timelineService) {
        this.timelineService = timelineService;
    }

    /**
     * 查询全部时间线事件
     *
     * @return 事件公开视图列表
     */
    @Operation(summary = "查询全部时间线", description = "返回全部团队历程事件（按排序号升序），结果经 Spring Cache 缓存 10 分钟")
    @GetMapping
    public Result<List<TimelineVO>> findAll() {
        return Result.ok(timelineService.findAll());
    }

    /**
     * 分页查询时间线事件
     *
     * @param current 当前页码（≥1）
     * @param size    每页大小（1~100）
     * @return 分页结果（公开视图）
     */
    @Operation(summary = "分页查询时间线", description = "分页查询团队历程事件（按排序号升序）")
    @GetMapping("/page")
    public Result<PageResult<TimelineVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size) {
        return Result.ok(timelineService.findAllByPage(current, size));
    }

    /**
     * 根据 id 查询时间线事件详情
     *
     * @param id 事件 id
     * @return 事件公开视图；不存在时返回 404
     */
    @Operation(summary = "查询时间线详情", description = "根据 id 查询单个时间线事件")
    @GetMapping("/{id}")
    public Result<TimelineVO> findById(
            @Parameter(description = "事件 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        TimelineVO vo = timelineService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 创建时间线事件
     *
     * @param dto 事件DTO
     * @return 创建后的公开视图
     */
    @Operation(summary = "创建时间线事件", description = "新增团队历程事件，需 admin 权限")
    @PostMapping
    public Result<TimelineVO> create(@Valid @RequestBody TimelineDto dto) {
        return Result.ok(timelineService.create(dto));
    }

    /**
     * 更新时间线事件
     *
     * @param id  事件 id
     * @param dto 事件DTO
     * @return 更新后的公开视图
     */
    @Operation(summary = "更新时间线事件", description = "按 id 更新时间线事件，需 admin 权限")
    @PutMapping("/{id}")
    public Result<TimelineVO> update(
            @Parameter(description = "事件 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody TimelineDto dto) {
        return Result.ok(timelineService.update(id, dto));
    }

    /**
     * 删除时间线事件
     *
     * @param id 事件 id
     * @return 操作结果
     */
    @Operation(summary = "删除时间线事件", description = "删除时间线事件，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "事件 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        timelineService.delete(id);
        return Result.ok();
    }
}

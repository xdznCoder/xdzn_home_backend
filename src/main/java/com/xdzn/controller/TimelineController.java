package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.entity.TimelineEvent;
import com.xdzn.service.TimelineEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TimelineController
 * <p>
 * 时间线（大事记）相关接口：事件列表、详情、增删改。
 *
 * @author xdzn
 */
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
     * @return 事件列表
     */
    @GetMapping
    public Result<List<TimelineEvent>> findAll() {
        return Result.ok(timelineService.findAll());
    }

    /**
     * 根据 id 查询时间线事件详情
     *
     * @param id 事件 id
     * @return 事件信息；不存在时返回 404
     */
    @GetMapping("/{id}")
    public Result<TimelineEvent> findById(@PathVariable Long id) {
        TimelineEvent event = timelineService.findById(id);
        if (event == null) return Result.notFound();
        return Result.ok(event);
    }

    /**
     * 创建时间线事件
     *
     * @param event 事件信息
     * @return 创建后的事件
     */
    @PostMapping
    public Result<TimelineEvent> create(@RequestBody TimelineEvent event) {
        return Result.ok(timelineService.create(event));
    }

    /**
     * 更新时间线事件
     *
     * @param id    事件 id
     * @param event 事件信息
     * @return 更新后的事件
     */
    @PutMapping("/{id}")
    public Result<TimelineEvent> update(@PathVariable Long id, @RequestBody TimelineEvent event) {
        return Result.ok(timelineService.update(id, event));
    }

    /**
     * 删除时间线事件
     *
     * @param id 事件 id
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        timelineService.delete(id);
        return Result.ok();
    }
}

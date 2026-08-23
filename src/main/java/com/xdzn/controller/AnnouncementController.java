package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.vo.AnnouncementVO;
import com.xdzn.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AnnouncementController
 * <p>
 * 公告接口（站外通信 + 站内展示）：
 * <ul>
 *     <li>分页 / 详情：登录成员可读（站内公告展示）</li>
 *     <li>发布 / 编辑 / 删除：仅 captain（发布时可选目标 QQ 群、发邮箱）</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "公告接口", description = "站外通信：公告发布（针对不同 QQ 群 + 成员邮箱）与站内展示")
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    /**
     * 公告服务
     */
    private final AnnouncementService announcementService;

    /**
     * 构造注入
     *
     * @param announcementService 公告服务
     */
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 分页查询公告（登录成员可读）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询公告", description = "登录成员可读，按发布时间倒序")
    @GetMapping("/page")
    public Result<PageResult<AnnouncementVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size) {
        return Result.ok(announcementService.findAllByPage(current, size));
    }

    /**
     * 查询公告详情（登录成员可读）
     *
     * @param id 公告 id
     * @return 公告视图；不存在时返回 404
     */
    @Operation(summary = "公告详情", description = "根据 id 查询公告（含目标群）")
    @GetMapping("/{id}")
    public Result<AnnouncementVO> findById(
            @Parameter(description = "公告 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        AnnouncementVO vo = announcementService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 发布公告（仅 captain，可选目标群 + 发邮箱）
     *
     * @param dto 公告 DTO
     * @return 创建后的公告视图
     */
    @Operation(summary = "发布公告", description = "发布公告并推送到选定的 QQ 群 / 成员邮箱，需 captain 权限")
    @PostMapping
    public Result<AnnouncementVO> create(@Valid @RequestBody AnnouncementDto dto) {
        return Result.ok(announcementService.create(dto));
    }

    /**
     * 更新公告（仅 captain）
     *
     * @param id  公告 id
     * @param dto 公告 DTO
     * @return 更新后的公告视图
     */
    @Operation(summary = "更新公告", description = "更新公告并重设目标群，需 captain 权限")
    @PutMapping("/{id}")
    public Result<AnnouncementVO> update(
            @Parameter(description = "公告 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody AnnouncementDto dto) {
        return Result.ok(announcementService.update(id, dto));
    }

    /**
     * 删除公告（仅 captain）
     *
     * @param id 公告 id
     * @return 操作结果
     */
    @Operation(summary = "删除公告", description = "删除公告及其目标群关联，需 captain 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "公告 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        announcementService.delete(id);
        return Result.ok();
    }
}

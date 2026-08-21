package com.xdzn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.xdzn.common.Result;
import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Announcement;
import com.xdzn.model.vo.AnnouncementVO;
import com.xdzn.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AnnouncementController
 * <p>
 * 公告相关接口：公开查看、管理员CRUD。
 *
 * @author xdzn
 */
@Tag(name = "公告接口", description = "站内通知，支持管理员CRUD和成员查看已发布公告")
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 获取已发布的公告列表（公开接口）
     */
    @Operation(summary = "获取已发布公告列表", description = "返回状态为published的公告，按置顶和发布时间排序")
    @GetMapping
    public Result<List<AnnouncementVO>> findPublished() {
        try {
            List<AnnouncementVO> list = announcementService.findPublished();
            return Result.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取公告详情（公开接口）
     */
    @Operation(summary = "获取公告详情", description = "根据id查询公告详情")
    @GetMapping("/{id}")
    public Result<AnnouncementVO> findById(
            @Parameter(description = "公告id", required = true, example = "1")
            @PathVariable Long id) {
        AnnouncementVO vo = announcementService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 分页查询公告（管理员接口）
     */
    @Operation(summary = "分页查询公告", description = "管理员分页查询公告，支持状态筛选")
    @GetMapping("/admin/page")
    @SaCheckRole("admin")
    public Result<PageResult<AnnouncementVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "状态筛选")
            @RequestParam(required = false) String status) {
        PageResult<AnnouncementVO> result = announcementService.findAllByPage(page, size, status);
        return Result.ok(result);
    }

    /**
     * 创建公告（管理员接口）
     */
    @Operation(summary = "创建公告", description = "新增公告，需admin权限")
    @PostMapping
    @SaCheckRole("admin")
    public Result<Void> create(@Valid @RequestBody AnnouncementDto dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        announcementService.create(dto, userId);
        return Result.ok();
    }

    /**
     * 更新公告（管理员接口）
     */
    @Operation(summary = "更新公告", description = "按id更新公告，需admin权限")
    @PutMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Void> update(
            @Parameter(description = "公告id", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDto dto) {
        announcementService.update(id, dto);
        return Result.ok();
    }

    /**
     * 删除公告（管理员接口）
     */
    @Operation(summary = "删除公告", description = "删除公告，需admin权限")
    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Void> delete(
            @Parameter(description = "公告id", required = true, example = "1")
            @PathVariable Long id) {
        announcementService.delete(id);
        return Result.ok();
    }
}

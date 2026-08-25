package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ResourceDto;
import com.xdzn.model.vo.ResourceVO;
import com.xdzn.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ResourceController
 * <p>
 * 资源分享接口（内部团队）：
 * <ul>
 *     <li>上传 / 检索 / 查看：所有登录成员（member/alumni/captain）</li>
 *     <li>编辑 / 删除：上传人本人或 captain（服务内校验）</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "资源分享接口", description = "团队内部资源：成员上传/检索资源帖子，编辑删除仅上传人或队长")
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    /**
     * 资源服务
     */
    private final ResourceService resourceService;

    /**
     * 构造注入资源服务
     *
     * @param resourceService 资源服务
     */
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * 分页检索资源（关键词匹配标题/分类/标签/上传人，可按分类筛选）
     *
     * @param current    当前页码（≥1）
     * @param size       每页大小（1~100）
     * @param keyword    关键词（可选）
     * @param categoryId 分类 id（可选）
     * @return 分页结果
     */
    @Operation(summary = "分页检索资源", description = "关键词匹配标题/分类/标签/上传人，可按分类筛选，所有登录成员可读")
    @GetMapping("/page")
    public Result<PageResult<ResourceVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size,
            @Parameter(description = "关键词（匹配标题/分类/标签/上传人）")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "分类 id 筛选")
            @RequestParam(required = false) Long categoryId) {
        return Result.ok(resourceService.findAllByPage(current, size, keyword, categoryId));
    }

    /**
     * 根据 id 查询资源详情
     *
     * @param id 资源 id
     * @return 资源视图；不存在时返回 404
     */
    @Operation(summary = "资源详情", description = "根据 id 查询单个资源（含分类名、上传人）")
    @GetMapping("/{id}")
    public Result<ResourceVO> findById(
            @Parameter(description = "资源 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        ResourceVO vo = resourceService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 上传资源（所有登录成员）
     *
     * @param dto 资源 DTO
     * @return 创建后的资源视图
     */
    @Operation(summary = "上传资源", description = "上传资源帖子（标题/分类/标签/附件等），所有登录成员可操作")
    @PostMapping
    public Result<ResourceVO> create(@Valid @RequestBody ResourceDto dto) {
        return Result.ok(resourceService.create(dto));
    }

    /**
     * 更新资源（上传人本人或 captain）
     *
     * @param id  资源 id
     * @param dto 资源 DTO
     * @return 更新后的资源视图
     */
    @Operation(summary = "更新资源", description = "更新资源（上传人本人或队长）")
    @PutMapping("/{id}")
    public Result<ResourceVO> update(
            @Parameter(description = "资源 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody ResourceDto dto) {
        return Result.ok(resourceService.update(id, dto));
    }

    /**
     * 删除资源（上传人本人或 captain）
     *
     * @param id 资源 id
     * @return 操作结果
     */
    @Operation(summary = "导出资源 Excel", description = "导出全部资源，登录成员可调用")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        resourceService.export(response);
    }

    @Operation(summary = "删除资源", description = "删除资源（上传人本人或队长）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "资源 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        resourceService.delete(id);
        return Result.ok();
    }
}

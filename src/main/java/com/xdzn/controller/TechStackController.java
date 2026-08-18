package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TechStackDto;
import com.xdzn.model.vo.TechStackVO;
import com.xdzn.service.TechStackItemService;
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
 * TechStackController
 * <p>
 * 技术栈相关接口：技术栈列表、详情、增删改。
 * 返回统一为公开视图 {@link TechStackVO}（脱敏，不含审计字段）。
 *
 * @author xdzn
 */
@Validated
@Tag(name = "技术栈接口", description = "官网技术栈展示与管理的增删改查（写操作需 admin 权限）")
@RestController
@RequestMapping("/api/tech-stack")
public class TechStackController {

    /**
     * 技术栈服务
     */
    private final TechStackItemService techStackService;

    /**
     * 构造注入技术栈服务
     *
     * @param techStackService 技术栈服务
     */
    public TechStackController(TechStackItemService techStackService) {
        this.techStackService = techStackService;
    }

    /**
     * 查询全部技术栈条目
     *
     * @return 技术栈公开视图列表
     */
    @Operation(summary = "查询全部技术栈", description = "返回全部技术栈条目（按排序号升序），结果经 Spring Cache 缓存 10 分钟")
    @GetMapping
    public Result<List<TechStackVO>> findAll() {
        return Result.ok(techStackService.findAll());
    }

    /**
     * 分页查询技术栈条目
     *
     * @param current 当前页码（≥1）
     * @param size    每页大小（1~100）
     * @return 分页结果（公开视图）
     */
    @Operation(summary = "分页查询技术栈", description = "分页查询技术栈条目（按排序号升序）")
    @GetMapping("/page")
    public Result<PageResult<TechStackVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size) {
        return Result.ok(techStackService.findAllByPage(current, size));
    }

    /**
     * 根据 id 查询技术栈条目详情
     *
     * @param id 技术栈条目 id
     * @return 技术栈公开视图；不存在时返回 404
     */
    @Operation(summary = "查询技术栈详情", description = "根据 id 查询单个技术栈条目")
    @GetMapping("/{id}")
    public Result<TechStackVO> findById(
            @Parameter(description = "技术栈条目 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        TechStackVO vo = techStackService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 创建技术栈条目
     *
     * @param dto 技术栈DTO
     * @return 创建后的公开视图
     */
    @Operation(summary = "创建技术栈", description = "新增技术栈条目，需 admin 权限")
    @PostMapping
    public Result<TechStackVO> create(@Valid @RequestBody TechStackDto dto) {
        return Result.ok(techStackService.create(dto));
    }

    /**
     * 更新技术栈条目
     *
     * @param id  技术栈条目 id
     * @param dto 技术栈DTO
     * @return 更新后的公开视图
     */
    @Operation(summary = "更新技术栈", description = "按 id 更新技术栈条目，需 admin 权限")
    @PutMapping("/{id}")
    public Result<TechStackVO> update(
            @Parameter(description = "技术栈条目 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody TechStackDto dto) {
        return Result.ok(techStackService.update(id, dto));
    }

    /**
     * 删除技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 操作结果
     */
    @Operation(summary = "删除技术栈", description = "删除技术栈条目，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "技术栈条目 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        techStackService.delete(id);
        return Result.ok();
    }
}

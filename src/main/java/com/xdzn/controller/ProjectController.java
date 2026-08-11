package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ProjectDto;
import com.xdzn.model.dto.ProjectVO;
import com.xdzn.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController
 * <p>
 * 项目相关接口：项目列表、详情、增删改。
 *
 * @author xdzn
 */
@Tag(name = "项目接口", description = "官网项目展示与管理的增删改查（写操作需 admin 权限）")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    /**
     * 项目服务
     */
    private final ProjectService projectService;

    /**
     * 构造注入项目服务
     *
     * @param projectService 项目服务
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 查询全部项目（含技术栈信息）
     *
     * @return 项目视图对象列表
     */
    @Operation(summary = "查询全部项目", description = "返回全部项目（含技术栈名称列表），结果经 Spring Cache 缓存 10 分钟")
    @GetMapping
    public Result<List<ProjectVO>> findAll() {
        return Result.ok(projectService.findAll());
    }

    /**
     * 分页查询项目（含技术栈信息）
     *
     * @param current 当前页码，默认 1
     * @param size    每页大小，默认 10
     * @return 分页结果
     */
    @Operation(summary = "分页查询项目", description = "分页查询项目（含技术栈名称列表，按排序号升序）")
    @GetMapping("/page")
    public Result<PageResult<ProjectVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(projectService.findAllByPage(current, size));
    }

    /**
     * 根据 id 查询项目详情
     *
     * @param id 项目 id
     * @return 项目视图对象；不存在时返回 404
     */
    @Operation(summary = "查询项目详情", description = "根据 id 查询单个项目（含技术栈名称列表）")
    @GetMapping("/{id}")
    public Result<ProjectVO> findById(
            @Parameter(description = "项目 id", required = true, example = "1")
            @PathVariable Long id) {
        ProjectVO vo = projectService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 创建项目
     *
     * @param dto 项目DTO
     * @return 创建后的项目视图对象
     */
    @Operation(summary = "创建项目", description = "创建项目并同步其技术栈关联，需 admin 权限")
    @PostMapping
    public Result<ProjectVO> create(@Valid @RequestBody ProjectDto dto) {
        return Result.ok(projectService.create(dto));
    }

    /**
     * 更新项目
     *
     * @param id  项目 id
     * @param dto 项目DTO
     * @return 更新后的项目视图对象
     */
    @Operation(summary = "更新项目", description = "按 id 更新项目并同步其技术栈关联，需 admin 权限")
    @PutMapping("/{id}")
    public Result<ProjectVO> update(
            @Parameter(description = "项目 id", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProjectDto dto) {
        return Result.ok(projectService.update(id, dto));
    }

    /**
     * 删除项目（同时删除其技术栈关联关系）
     *
     * @param id 项目 id
     * @return 操作结果
     */
    @Operation(summary = "删除项目", description = "删除项目及其技术栈关联，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "项目 id", required = true, example = "1")
            @PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }
}

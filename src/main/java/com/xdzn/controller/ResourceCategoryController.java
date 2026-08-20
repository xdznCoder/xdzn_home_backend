package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.ResourceCategoryDto;
import com.xdzn.model.entity.ResourceCategory;
import com.xdzn.service.ResourceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ResourceCategoryController
 * <p>
 * 资源分类接口（内部团队）：
 * <ul>
 *     <li>查询分类：登录成员可读（供上传时选择）</li>
 *     <li>新增/修改/删除分类：仅 captain（队长）</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "资源分类接口", description = "资源分类：队长管理分类条目，成员上传时选择")
@RestController
@RequestMapping("/api/resource-categories")
public class ResourceCategoryController {

    /**
     * 分类服务
     */
    private final ResourceCategoryService categoryService;

    /**
     * 构造注入分类服务
     *
     * @param categoryService 分类服务
     */
    public ResourceCategoryController(ResourceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询全部分类（登录成员可读）
     *
     * @return 分类列表
     */
    @Operation(summary = "查询资源分类", description = "返回全部分类（按排序升序），登录成员可读")
    @GetMapping
    public Result<List<ResourceCategory>> list() {
        return Result.ok(categoryService.list());
    }

    /**
     * 新增分类（仅 captain）
     *
     * @param dto 分类 DTO
     * @return 创建后的分类
     */
    @Operation(summary = "新增分类", description = "新增资源分类，需 captain 权限")
    @PostMapping
    public Result<ResourceCategory> create(@Valid @RequestBody ResourceCategoryDto dto) {
        return Result.ok(categoryService.create(dto));
    }

    /**
     * 更新分类（仅 captain）
     *
     * @param id  分类 id
     * @param dto 分类 DTO
     * @return 更新后的分类
     */
    @Operation(summary = "更新分类", description = "更新资源分类，需 captain 权限")
    @PutMapping("/{id}")
    public Result<ResourceCategory> update(
            @Parameter(description = "分类 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody ResourceCategoryDto dto) {
        return Result.ok(categoryService.update(id, dto));
    }

    /**
     * 删除分类（仅 captain）
     *
     * @param id 分类 id
     * @return 操作结果
     */
    @Operation(summary = "删除分类", description = "删除资源分类，需 captain 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "分类 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}

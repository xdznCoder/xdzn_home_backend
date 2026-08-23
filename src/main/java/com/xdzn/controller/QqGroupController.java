package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.QqGroupDto;
import com.xdzn.model.entity.QqGroup;
import com.xdzn.service.QqGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QqGroupController
 * <p>
 * QQ 群配置接口（站外通信）：
 * <ul>
 *     <li>查询群列表：登录成员可读（公告发布时选群）</li>
 *     <li>新增/修改/删除：仅 captain（队长）</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "QQ 群配置接口", description = "站外通信：QQ 群条目管理，队长维护，公告发布时选择目标群")
@RestController
@RequestMapping("/api/qq-groups")
public class QqGroupController {

    /**
     * QQ 群服务
     */
    private final QqGroupService qqGroupService;

    /**
     * 构造注入
     *
     * @param qqGroupService QQ 群服务
     */
    public QqGroupController(QqGroupService qqGroupService) {
        this.qqGroupService = qqGroupService;
    }

    /**
     * 查询全部 QQ 群（登录成员可读）
     *
     * @return QQ 群列表
     */
    @Operation(summary = "查询 QQ 群列表", description = "返回全部 QQ 群，登录成员可读（供发布公告选群）")
    @GetMapping
    public Result<List<QqGroup>> list() {
        return Result.ok(qqGroupService.list());
    }

    /**
     * 新增 QQ 群（仅 captain）
     *
     * @param dto 群配置 DTO
     * @return 创建后的群
     */
    @Operation(summary = "新增 QQ 群", description = "新增 QQ 群配置，需 captain 权限")
    @PostMapping
    public Result<QqGroup> create(@Valid @RequestBody QqGroupDto dto) {
        return Result.ok(qqGroupService.create(dto));
    }

    /**
     * 更新 QQ 群（仅 captain）
     *
     * @param id  群 id
     * @param dto 群配置 DTO
     * @return 更新后的群
     */
    @Operation(summary = "更新 QQ 群", description = "更新 QQ 群配置，需 captain 权限")
    @PutMapping("/{id}")
    public Result<QqGroup> update(
            @Parameter(description = "群 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody QqGroupDto dto) {
        return Result.ok(qqGroupService.update(id, dto));
    }

    /**
     * 删除 QQ 群（仅 captain）
     *
     * @param id 群 id
     * @return 操作结果
     */
    @Operation(summary = "删除 QQ 群", description = "删除 QQ 群配置，需 captain 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "群 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        qqGroupService.delete(id);
        return Result.ok();
    }
}

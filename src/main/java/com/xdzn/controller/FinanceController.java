package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.FinanceRecordDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.vo.FinanceRecordVO;
import com.xdzn.model.vo.FinanceSummaryVO;
import com.xdzn.service.FinanceService;
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

/**
 * FinanceController
 * <p>
 * 经费管理接口（内部团队管理）：
 * <ul>
 *     <li>admin：记账（进账/支出）、修改、删除</li>
 *     <li>登录成员（member/admin）：分页查看明细、汇总余额、详情</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "经费管理接口", description = "经费收支：admin 记账/修改/删除，登录成员只读查看明细与余额")
@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    /**
     * 经费管理服务
     */
    private final FinanceService financeService;

    /**
     * 构造注入经费管理服务
     *
     * @param financeService 经费管理服务
     */
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * 分页查询收支明细（登录成员可读，可按类型/分类筛选）
     *
     * @param current  当前页码（≥1）
     * @param size     每页大小（1~100）
     * @param type     收支类型（income/expense，可选）
     * @param category 分类（可选，模糊匹配）
     * @return 分页结果
     */
    @Operation(summary = "分页查询收支明细", description = "登录成员可读，可按收支类型/分类筛选，按发生时间倒序")
    @GetMapping("/page")
    public Result<PageResult<FinanceRecordVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于 1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于 1")
            @Max(value = 100, message = "每页大小不能超过 100") long size,
            @Parameter(description = "收支类型筛选（income/expense）")
            @RequestParam(required = false)
            @Pattern(regexp = "^(income|expense)$", message = "收支类型仅允许 income/expense") String type,
            @Parameter(description = "分类筛选（模糊匹配）")
            @RequestParam(required = false) String category) {
        return Result.ok(financeService.findAllByPage(current, size, type, category));
    }

    /**
     * 汇总经费余额（登录成员可读）
     *
     * @return 汇总视图（累计进账/累计支出/余额）
     */
    @Operation(summary = "经费汇总", description = "实时聚合累计进账、累计支出与经费余额")
    @GetMapping("/summary")
    public Result<FinanceSummaryVO> summary() {
        return Result.ok(financeService.summary());
    }

    /**
     * 根据 id 查询记录详情（登录成员可读）
     *
     * @param id 记录 id
     * @return 记录视图；不存在时返回 404
     */
    @Operation(summary = "收支记录详情", description = "根据 id 查询单条收支记录（含操作人姓名）")
    @GetMapping("/{id}")
    public Result<FinanceRecordVO> findById(
            @Parameter(description = "记录 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        FinanceRecordVO vo = financeService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 记账（进账或支出），仅 admin
     *
     * @param dto 收支 DTO
     * @return 创建后的记录视图
     */
    @Operation(summary = "记账", description = "记录一笔进账(income)或支出(expense)，需 admin 权限")
    @PostMapping
    public Result<FinanceRecordVO> create(@Valid @RequestBody FinanceRecordDto dto) {
        return Result.ok(financeService.create(dto));
    }

    /**
     * 修改收支记录，仅 admin
     *
     * @param id  记录 id
     * @param dto 收支 DTO
     * @return 修改后的记录视图
     */
    @Operation(summary = "修改收支记录", description = "修改收支记录的类型/金额/分类/说明/发生时间，需 admin 权限")
    @PutMapping("/{id}")
    public Result<FinanceRecordVO> update(
            @Parameter(description = "记录 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody FinanceRecordDto dto) {
        return Result.ok(financeService.update(id, dto));
    }

    /**
     * 导出经费收支明细（仅 admin，支持筛选）
     *
     * @param response HTTP 响应
     * @param type     收支类型（可选）
     * @param category 分类（可选）
     */
    @Operation(summary = "导出经费明细 Excel", description = "导出全部收支明细（可按类型/分类筛选），需 admin 权限")
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @Parameter(description = "收支类型筛选（income/expense）")
            @RequestParam(required = false)
            @Pattern(regexp = "^(income|expense)$", message = "收支类型仅允许 income/expense") String type,
            @Parameter(description = "分类筛选")
            @RequestParam(required = false) String category) {
        financeService.export(response, type, category);
    }

    /**
     * 删除收支记录，仅 admin
     *
     * @param id 记录 id
     * @return 操作结果
     */
    @Operation(summary = "删除收支记录", description = "删除单条收支记录，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "记录 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        financeService.delete(id);
        return Result.ok();
    }
}

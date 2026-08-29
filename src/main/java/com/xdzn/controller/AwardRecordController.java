package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.AwardRecordDto;
import com.xdzn.model.vo.AwardRecordVO;
import com.xdzn.service.AwardRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AwardRecordController
 * <p>
 * 获奖记录接口（成员一对多获奖）：
 * <ul>
 *     <li>查询成员获奖：登录成员可读</li>
 *     <li>增改删、导出：仅 captain</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "获奖记录接口", description = "成员获奖记录（一对多），支持按时间/获奖人/比赛导出 Excel")
@RestController
@RequestMapping("/api/award-records")
public class AwardRecordController {

    private final AwardRecordService awardService;

    public AwardRecordController(AwardRecordService awardService) {
        this.awardService = awardService;
    }

    @Operation(summary = "查询获奖记录列表", description = "可按获奖人/比赛筛选，登录成员可读")
    @GetMapping
    public Result<List<AwardRecordVO>> list(
            @Parameter(description = "获奖人成员 id")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "比赛名（模糊）")
            @RequestParam(required = false) String competition) {
        return Result.ok(awardService.list(memberId, competition));
    }

    @Operation(summary = "查询成员获奖记录", description = "按成员查询其全部获奖记录，登录成员可读")
    @GetMapping("/member/{memberId}")
    public Result<List<AwardRecordVO>> listByMember(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long memberId) {
        return Result.ok(awardService.listByMember(memberId));
    }

    @Operation(summary = "新增获奖记录", description = "新增一条获奖，需 captain 权限")
    @PostMapping
    public Result<AwardRecordVO> create(@Valid @RequestBody AwardRecordDto dto) {
        return Result.ok(awardService.create(dto));
    }

    @Operation(summary = "修改获奖记录", description = "修改获奖记录，需 captain 权限")
    @PutMapping("/{id}")
    public Result<AwardRecordVO> update(
            @Parameter(description = "获奖 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody AwardRecordDto dto) {
        return Result.ok(awardService.update(id, dto));
    }

    @Operation(summary = "删除获奖记录", description = "删除获奖记录，需 captain 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "获奖 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        awardService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "导出获奖记录 Excel", description = "按时间范围/获奖人/比赛导出获奖记录，需 captain 权限")
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "获奖人成员 id")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "比赛名（模糊）")
            @RequestParam(required = false) String competition) {
        awardService.export(response, startTime, endTime, memberId, competition);
    }
}

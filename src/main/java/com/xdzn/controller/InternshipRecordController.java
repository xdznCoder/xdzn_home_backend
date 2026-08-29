package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.InternshipRecordDto;
import com.xdzn.model.vo.InternshipRecordVO;
import com.xdzn.service.InternshipRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * InternshipRecordController
 * <p>
 * 实习记录接口（成员一对多实习）：
 * <ul>
 *     <li>查询成员实习：登录成员可读</li>
 *     <li>增改删、导出：仅 captain</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "实习记录接口", description = "成员实习记录（一对多），支持导出 Excel")
@RestController
@RequestMapping("/api/internship-records")
public class InternshipRecordController {

    private final InternshipRecordService internshipService;

    public InternshipRecordController(InternshipRecordService internshipService) {
        this.internshipService = internshipService;
    }

    @Operation(summary = "查询实习记录列表", description = "可按成员/公司筛选，登录成员可读")
    @GetMapping
    public Result<List<InternshipRecordVO>> list(
            @Parameter(description = "成员 id")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "公司名（模糊）")
            @RequestParam(required = false) String company) {
        return Result.ok(internshipService.list(memberId, company));
    }

    @Operation(summary = "查询成员实习记录", description = "按成员查询其全部实习记录，登录成员可读")
    @GetMapping("/member/{memberId}")
    public Result<List<InternshipRecordVO>> listByMember(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long memberId) {
        return Result.ok(internshipService.listByMember(memberId));
    }

    @Operation(summary = "新增实习记录", description = "新增一条实习，需 captain 权限")
    @PostMapping
    public Result<InternshipRecordVO> create(@Valid @RequestBody InternshipRecordDto dto) {
        return Result.ok(internshipService.create(dto));
    }

    @Operation(summary = "修改实习记录", description = "修改实习记录，需 captain 权限")
    @PutMapping("/{id}")
    public Result<InternshipRecordVO> update(
            @Parameter(description = "实习 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            @Valid @RequestBody InternshipRecordDto dto) {
        return Result.ok(internshipService.update(id, dto));
    }

    @Operation(summary = "删除实习记录", description = "删除实习记录，需 captain 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "实习 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        internshipService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "导出实习记录 Excel", description = "按成员/公司导出实习记录，需 captain 权限")
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @Parameter(description = "成员 id")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "公司名（模糊）")
            @RequestParam(required = false) String company) {
        internshipService.export(response, memberId, company);
    }
}

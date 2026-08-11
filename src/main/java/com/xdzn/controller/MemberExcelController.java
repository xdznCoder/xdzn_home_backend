package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.MemberImportResult;
import com.xdzn.model.entity.Member;
import com.xdzn.service.MemberExcelService;
import com.xdzn.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * MemberExcelController
 * <p>
 * 成员信息 Excel 导入导出接口：
 * <ul>
 *     <li>导出成员名单 / 下载导入模板（GET，需 admin）</li>
 *     <li>批量导入成员：无 ID 新增、有 ID 更新，返回逐行结果（POST，需 admin）</li>
 * </ul>
 *
 * @author xdzn
 */
@Tag(name = "成员 Excel 接口", description = "成员信息的 Excel 导入导出（均需 admin 权限）")
@RestController
@RequestMapping("/api/members")
public class MemberExcelController {

    /**
     * 成员 Excel 服务
     */
    private final MemberExcelService memberExcelService;

    /**
     * 成员服务（用于查询待导出的成员列表）
     */
    private final MemberService memberService;

    /**
     * 构造注入依赖
     *
     * @param memberExcelService 成员 Excel 服务
     * @param memberService      成员服务
     */
    public MemberExcelController(MemberExcelService memberExcelService, MemberService memberService) {
        this.memberExcelService = memberExcelService;
        this.memberService = memberService;
    }

    /**
     * 导出全部成员为 Excel 文件
     *
     * @param response HTTP 响应（用于写出 .xlsx 下载）
     */
    @Operation(summary = "导出成员 Excel", description = "导出全部成员为 .xlsx 文件下载，需 admin 权限")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<Member> members = memberService.findAll();
        memberExcelService.exportMembers(response, members);
    }

    /**
     * 下载成员导入模板（含表头与示例行）
     *
     * @param response HTTP 响应
     */
    @Operation(summary = "下载导入模板", description = "下载成员导入模板（含表头与一行示例），需 admin 权限")
    @GetMapping("/import/template")
    public void importTemplate(HttpServletResponse response) {
        memberExcelService.exportTemplate(response);
    }

    /**
     * 批量导入成员（新增 + 按 ID 更新）
     *
     * @param file 上传的 .xlsx 文件
     * @return 逐行导入结果（总数 / 成功 / 失败 / 错误明细）
     */
    @Operation(summary = "导入成员 Excel", description = "上传 .xlsx 批量导入：无 ID 则新增，有 ID 且存在则更新；返回逐行结果，需 admin 权限")
    @PostMapping("/import")
    public Result<MemberImportResult> importMembers(
            @Parameter(description = "Excel 文件（.xlsx）", required = true)
            @RequestParam("file") MultipartFile file) {
        return Result.ok(memberExcelService.importMembers(file));
    }
}

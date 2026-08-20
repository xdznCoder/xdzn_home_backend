package com.xdzn.service;

import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.model.dto.MemberExcelRow;
import com.xdzn.model.dto.MemberImportResult;
import com.xdzn.model.entity.Member;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * MemberExcelService
 * <p>
 * 成员信息 Excel 导入导出服务，基于通用 {@link ExcelService} 封装，
 * 提供成员导出、导入模板下载与批量导入（新增 + 按 ID 更新）。
 * <p>
 * 导入策略：
 * <ul>
 *     <li>Excel 行含 ID 且数据库中已存在 → 更新该成员</li>
 *     <li>Excel 行无 ID → 新增成员</li>
 *     <li>逐行校验与落库，失败行记录原因不影响后续行（不开启整体事务，成功行保留）</li>
 * </ul>
 *
 * @author xdzn
 */
@Service
public class MemberExcelService {

    /**
     * 通用 Excel 导入导出封装
     */
    private final ExcelService excelService;

    /**
     * 成员表 Mapper
     */
    private final MemberMapper memberMapper;

    /**
     * 构造注入依赖
     *
     * @param excelService 通用 Excel 服务
     * @param memberMapper 成员表 Mapper
     */
    public MemberExcelService(ExcelService excelService, MemberMapper memberMapper) {
        this.excelService = excelService;
        this.memberMapper = memberMapper;
    }

    /**
     * 导出全部成员为 Excel（触发浏览器下载）
     *
     * @param response HTTP 响应
     * @param members  成员列表
     */
    public void exportMembers(HttpServletResponse response, List<Member> members) {
        List<MemberExcelRow> rows = members.stream().map(this::toRow).toList();
        excelService.export(response, rows, MemberExcelRow.class, "成员", "团队成员名单");
    }

    /**
     * 导出导入模板（含表头与一行示例数据）
     *
     * @param response HTTP 响应
     */
    public void exportTemplate(HttpServletResponse response) {
        List<MemberExcelRow> example = List.of(new MemberExcelRow(
                null, "张三", "https://example.com/avatar.png", "后端", 2025, 2021,
                "20230101", "软件工程", "核心成员", "示例实习经历", "示例获奖经历", 1));
        excelService.export(response, example, MemberExcelRow.class, "导入模板", "成员导入模板");
    }

    /**
     * 批量导入成员（新增 + 按 ID 更新），返回逐行结果
     *
     * @param file 上传的 Excel 文件（.xlsx）
     * @return 导入结果（总数 / 成功 / 失败 / 逐行错误）
     */
    public MemberImportResult importMembers(MultipartFile file) {
        List<MemberExcelRow> rows = excelService.importExcel(file, MemberExcelRow.class);

        MemberImportResult result = new MemberImportResult();
        result.setTotal(rows.size());

        // 逐行处理：校验 + 新增/更新；失败行记录原因，不影响后续行
        for (int i = 0; i < rows.size(); i++) {
            // Excel 第 1 行为表头，数据从第 2 行起
            int excelRow = i + 2;
            try {
                MemberExcelRow row = rows.get(i);
                validateRow(row);
                saveOrUpdate(row);
                result.setSuccess(result.getSuccess() + 1);
            } catch (Exception e) {
                result.setFail(result.getFail() + 1);
                result.getErrors().add(new MemberImportResult.ErrorRow(excelRow, e.getMessage()));
            }
        }
        return result;
    }

    // ── 内部方法 ──────────────────────

    /**
     * 校验单行数据：姓名、方向必填
     *
     * @param row 待校验的行
     */
    private void validateRow(MemberExcelRow row) {
        if (row.getName() == null || row.getName().isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (row.getDirection() == null || row.getDirection().isBlank()) {
            throw new IllegalArgumentException("方向不能为空");
        }
    }

    /**
     * 保存或更新：有 ID 且存在则更新，否则新增
     *
     * @param row Excel 行数据
     */
    private void saveOrUpdate(MemberExcelRow row) {
        if (row.getId() != null) {
            Member member = memberMapper.selectById(row.getId());
            if (member == null) {
                throw new IllegalArgumentException("ID " + row.getId() + " 不存在，无法更新");
            }
            applyRow(member, row);
            memberMapper.updateById(member);
        } else {
            Member member = new Member();
            applyRow(member, row);
            memberMapper.insert(member);
        }
    }

    /**
     * 将 Excel 行数据填充到成员实体（不含 ID）
     *
     * @param member 成员实体
     * @param row    Excel 行数据
     */
    private void applyRow(Member member, MemberExcelRow row) {
        member.setName(row.getName());
        member.setAvatar(row.getAvatar());
        member.setDirection(row.getDirection());
        member.setGraduationYear(row.getGraduationYear());
        member.setGrade(row.getGrade());
        member.setStudentNo(row.getStudentNo());
        member.setMajor(row.getMajor());
        member.setTeamRole(row.getTeamRole());
        member.setInternship(row.getInternship());
        member.setAwards(row.getAwards());
        member.setOrder(row.getOrder());
    }

    /**
     * 将成员实体转换为导出行对象
     *
     * @param member 成员实体
     * @return Excel 行对象
     */
    private MemberExcelRow toRow(Member member) {
        return new MemberExcelRow(
                member.getId(),
                member.getName(),
                member.getAvatar(),
                member.getDirection(),
                member.getGraduationYear(),
                member.getGrade(),
                member.getStudentNo(),
                member.getMajor(),
                member.getTeamRole(),
                member.getInternship(),
                member.getAwards(),
                member.getOrder());
    }
}

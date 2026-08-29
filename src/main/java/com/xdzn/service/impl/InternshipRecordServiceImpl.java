package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xdzn.common.BusinessException;
import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.InternshipRecordMapper;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.model.dto.InternshipExcelRow;
import com.xdzn.model.dto.InternshipRecordDto;
import com.xdzn.model.entity.InternshipRecord;
import com.xdzn.model.entity.Member;
import com.xdzn.model.vo.InternshipRecordVO;
import com.xdzn.service.InternshipRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InternshipRecordServiceImpl
 * <p>
 * 实习记录服务实现（成员一对多实习，导出 Excel）。
 *
 * @author xdzn
 */
@Service
public class InternshipRecordServiceImpl implements InternshipRecordService {

    private final InternshipRecordMapper internshipMapper;
    private final MemberMapper memberMapper;
    private final ExcelService excelService;

    public InternshipRecordServiceImpl(InternshipRecordMapper internshipMapper, MemberMapper memberMapper, ExcelService excelService) {
        this.internshipMapper = internshipMapper;
        this.memberMapper = memberMapper;
        this.excelService = excelService;
    }

    @Override
    public List<InternshipRecordVO> list(Long memberId, String company) {
        LambdaQueryWrapper<InternshipRecord> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) wrapper.eq(InternshipRecord::getMemberId, memberId);
        if (company != null && !company.isBlank()) {
            wrapper.like(InternshipRecord::getCompany, company);
        }
        wrapper.orderByDesc(InternshipRecord::getStartDate);
        return internshipMapper.selectList(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<InternshipRecordVO> listByMember(Long memberId) {
        return internshipMapper.selectList(new LambdaQueryWrapper<InternshipRecord>()
                        .eq(InternshipRecord::getMemberId, memberId)
                        .orderByDesc(InternshipRecord::getStartDate))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InternshipRecordVO create(InternshipRecordDto dto) {
        InternshipRecord record = new InternshipRecord();
        BeanUtils.copyProperties(dto, record);
        internshipMapper.insert(record);
        return toVO(internshipMapper.selectById(record.getId()));
    }

    @Override
    @Transactional
    public InternshipRecordVO update(Long id, InternshipRecordDto dto) {
        if (internshipMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "实习记录不存在");
        }
        InternshipRecord record = new InternshipRecord();
        record.setId(id);
        BeanUtils.copyProperties(dto, record);
        internshipMapper.updateById(record);
        return toVO(internshipMapper.selectById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (internshipMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "实习记录不存在");
        }
        internshipMapper.deleteById(id);
    }

    @Override
    public void export(HttpServletResponse response, Long memberId, String company) {
        LambdaQueryWrapper<InternshipRecord> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) wrapper.eq(InternshipRecord::getMemberId, memberId);
        if (company != null && !company.isBlank()) {
            wrapper.like(InternshipRecord::getCompany, company);
        }
        wrapper.orderByDesc(InternshipRecord::getStartDate);
        List<InternshipRecord> records = internshipMapper.selectList(wrapper);
        List<InternshipExcelRow> rows = records.stream().map(this::toExcelRow).collect(Collectors.toList());
        excelService.export(response, rows, InternshipExcelRow.class, "实习记录", "实习记录");
    }

    private InternshipRecordVO toVO(InternshipRecord record) {
        InternshipRecordVO vo = new InternshipRecordVO();
        BeanUtils.copyProperties(record, vo);
        Member member = memberMapper.selectById(record.getMemberId());
        vo.setMemberName(member != null ? member.getName() : null);
        return vo;
    }

    private InternshipExcelRow toExcelRow(InternshipRecord record) {
        InternshipRecordVO vo = toVO(record);
        InternshipExcelRow row = new InternshipExcelRow();
        row.setMemberName(vo.getMemberName());
        row.setCompany(record.getCompany());
        row.setPosition(record.getPosition());
        row.setStartDate(record.getStartDate() != null ? record.getStartDate().toString() : "");
        row.setEndDate(record.getEndDate() != null ? record.getEndDate().toString() : "");
        row.setDescription(record.getDescription());
        return row;
    }
}

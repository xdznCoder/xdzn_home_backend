package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xdzn.common.BusinessException;
import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.AwardRecordMapper;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.model.dto.AwardExcelRow;
import com.xdzn.model.dto.AwardRecordDto;
import com.xdzn.model.entity.AwardRecord;
import com.xdzn.model.entity.Member;
import com.xdzn.model.vo.AwardRecordVO;
import com.xdzn.service.AwardRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AwardRecordServiceImpl
 * <p>
 * 获奖记录服务实现（成员一对多获奖，导出按时间范围/获奖人/比赛筛选）。
 *
 * @author xdzn
 */
@Service
public class AwardRecordServiceImpl implements AwardRecordService {

    private final AwardRecordMapper awardMapper;
    private final MemberMapper memberMapper;
    private final ExcelService excelService;

    public AwardRecordServiceImpl(AwardRecordMapper awardMapper, MemberMapper memberMapper, ExcelService excelService) {
        this.awardMapper = awardMapper;
        this.memberMapper = memberMapper;
        this.excelService = excelService;
    }

    @Override
    public List<AwardRecordVO> list(Long memberId, String competition) {
        LambdaQueryWrapper<AwardRecord> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) wrapper.eq(AwardRecord::getMemberId, memberId);
        if (competition != null && !competition.isBlank()) {
            wrapper.like(AwardRecord::getCompetition, competition);
        }
        wrapper.orderByDesc(AwardRecord::getAwardTime);
        return awardMapper.selectList(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<AwardRecordVO> listByMember(Long memberId) {
        return awardMapper.selectList(new LambdaQueryWrapper<AwardRecord>()
                        .eq(AwardRecord::getMemberId, memberId)
                        .orderByDesc(AwardRecord::getAwardTime))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AwardRecordVO create(AwardRecordDto dto) {
        AwardRecord record = new AwardRecord();
        BeanUtils.copyProperties(dto, record);
        awardMapper.insert(record);
        return toVO(awardMapper.selectById(record.getId()));
    }

    @Override
    @Transactional
    public AwardRecordVO update(Long id, AwardRecordDto dto) {
        if (awardMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "获奖记录不存在");
        }
        AwardRecord record = new AwardRecord();
        record.setId(id);
        // updateById 忽略 null：dto 中 awardTime/level 等传 null 时保持原值
        BeanUtils.copyProperties(dto, record);
        awardMapper.updateById(record);
        return toVO(awardMapper.selectById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (awardMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "获奖记录不存在");
        }
        awardMapper.deleteById(id);
    }

    @Override
    public void export(HttpServletResponse response, LocalDateTime startTime, LocalDateTime endTime,
                       Long memberId, String competition) {
        LambdaQueryWrapper<AwardRecord> wrapper = new LambdaQueryWrapper<>();
        if (startTime != null) wrapper.ge(AwardRecord::getAwardTime, startTime);
        if (endTime != null) wrapper.le(AwardRecord::getAwardTime, endTime);
        if (memberId != null) wrapper.eq(AwardRecord::getMemberId, memberId);
        if (competition != null && !competition.isBlank()) {
            wrapper.like(AwardRecord::getCompetition, competition);
        }
        wrapper.orderByDesc(AwardRecord::getAwardTime);
        List<AwardRecord> records = awardMapper.selectList(wrapper);
        List<AwardExcelRow> rows = records.stream().map(this::toExcelRow).collect(Collectors.toList());
        excelService.export(response, rows, AwardExcelRow.class, "获奖记录", "获奖记录");
    }

    private AwardRecordVO toVO(AwardRecord record) {
        AwardRecordVO vo = new AwardRecordVO();
        BeanUtils.copyProperties(record, vo);
        Member member = memberMapper.selectById(record.getMemberId());
        vo.setMemberName(member != null ? member.getName() : null);
        return vo;
    }

    private AwardExcelRow toExcelRow(AwardRecord record) {
        AwardRecordVO vo = toVO(record);
        AwardExcelRow row = new AwardExcelRow();
        row.setMemberName(vo.getMemberName());
        row.setCompetition(record.getCompetition());
        row.setAwardTime(record.getAwardTime() != null ? record.getAwardTime().toString() : "");
        row.setLevel(levelText(record.getLevel()));
        row.setRank(record.getRank());
        row.setCertificate(record.getCertificate());
        return row;
    }

    private String levelText(String level) {
        return switch (level == null ? "" : level) {
            case "national" -> "国家级";
            case "provincial" -> "省级";
            default -> level;
        };
    }
}

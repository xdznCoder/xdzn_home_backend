package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * InternshipRecordVO
 * <p>
 * 实习记录视图对象，返回单条实习（含成员姓名）。
 *
 * @author xdzn
 */
@Data
public class InternshipRecordVO {

    private Long id;
    private Long memberId;
    private String memberName;
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AwardRecordVO
 * <p>
 * 获奖记录视图对象，返回单条获奖（含获奖人姓名）。
 *
 * @author xdzn
 */
@Data
public class AwardRecordVO {

    private Long id;
    private Long memberId;
    private String memberName;
    private String competition;
    private LocalDateTime awardTime;
    private String level;
    private String rank;
    private String certificate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

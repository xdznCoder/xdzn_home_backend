package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AwardRecordDto
 * <p>
 * 获奖记录请求 DTO（成员一条获奖，关联 memberId）。
 *
 * @author xdzn
 */
@Data
public class AwardRecordDto {

    /**
     * 获奖人成员 ID
     */
    private Long memberId;

    /**
     * 比赛/竞赛名称，必填
     */
    @NotBlank(message = "比赛名称不能为空")
    @Size(max = 255, message = "比赛名称不能超过 255 字符")
    private String competition;

    /**
     * 获奖时间
     */
    private LocalDateTime awardTime;

    /**
     * 级别：national（国家级）/ provincial（省级）
     */
    @Pattern(regexp = "^(national|provincial)$", message = "级别仅允许 national/provincial")
    private String level;

    /**
     * 获奖等级（一等奖/二等奖等）
     */
    @Size(max = 32, message = "获奖等级不能超过 32 字符")
    private String rank;

    /**
     * 获奖证明 URL（附件）
     */
    @Size(max = 512, message = "证明 URL 过长")
    private String certificate;
}

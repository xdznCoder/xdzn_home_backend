package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * InternshipRecordDto
 * <p>
 * 实习记录请求 DTO（成员一条实习，关联 memberId）。
 *
 * @author xdzn
 */
@Data
public class InternshipRecordDto {

    /**
     * 成员 ID
     */
    private Long memberId;

    /**
     * 实习公司，必填
     */
    @NotBlank(message = "公司名称不能为空")
    @Size(max = 255, message = "公司名称不能超过 255 字符")
    private String company;

    /**
     * 岗位
     */
    @Size(max = 255, message = "岗位不能超过 255 字符")
    private String position;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 实习描述/成果
     */
    private String description;
}

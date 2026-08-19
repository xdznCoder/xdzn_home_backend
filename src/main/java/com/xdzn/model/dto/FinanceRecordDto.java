package com.xdzn.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FinanceRecordDto
 * <p>
 * 经费收支请求 DTO，用于记账（create）与修改（update）。
 * {@code operatorId} 由服务层从当前登录用户写入，前端不传。
 *
 * @author xdzn
 */
@Data
public class FinanceRecordDto {

    /**
     * 收支类型，仅允许 income（进账）/ expense（支出）
     */
    @NotBlank(message = "收支类型不能为空")
    @Pattern(regexp = "^(income|expense)$", message = "收支类型仅允许 income/expense")
    private String type;

    /**
     * 金额（元），必须大于 0，最多两位小数
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "金额最多两位小数")
    private BigDecimal amount;

    /**
     * 分类（如 团费/报销/赞助/物资/活动）
     */
    @Size(max = 32, message = "分类不能超过 32 字符")
    private String category;

    /**
     * 说明
     */
    @Size(max = 255, message = "说明不能超过 255 字符")
    private String description;

    /**
     * 发生时间（可选；不传时服务层补当前时间；更新时为空则保留原时间）
     */
    private LocalDateTime occurredAt;
}

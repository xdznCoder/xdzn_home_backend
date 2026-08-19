package com.xdzn.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FinanceRecordVO
 * <p>
 * 经费收支记录视图对象，返回明细分页/详情（含操作人姓名）。
 *
 * @author xdzn
 */
@Data
public class FinanceRecordVO {

    /**
     * 记录 id
     */
    private Long id;

    /**
     * 收支类型：income / expense
     */
    private String type;

    /**
     * 金额（元）
     */
    private BigDecimal amount;

    /**
     * 分类
     */
    private String category;

    /**
     * 说明
     */
    private String description;

    /**
     * 操作人 id（关联 users）
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

package com.xdzn.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * FinanceSummaryVO
 * <p>
 * 经费汇总视图对象，实时聚合全部收支记录：
 * {@code balance = totalIncome - totalExpense}，空表时三项均为 0。
 *
 * @author xdzn
 */
@Data
public class FinanceSummaryVO {

    /**
     * 累计进账（SUM income）
     */
    private BigDecimal totalIncome;

    /**
     * 累计支出（SUM expense）
     */
    private BigDecimal totalExpense;

    /**
     * 经费余额（进账 - 支出）
     */
    private BigDecimal balance;
}

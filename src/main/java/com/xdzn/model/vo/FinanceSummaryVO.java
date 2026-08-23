package com.xdzn.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * FinanceSummaryVO
 * <p>
 * 经费汇总视图对象，聚合全部收支记录：
 * <ul>
 *     <li>累计：{@code totalIncome / totalExpense / balance}</li>
 *     <li>本月：{@code monthlyIncome / monthlyExpense}</li>
 *     <li>按类：{@code categorySummaries}（每类汇总金额）</li>
 * </ul>
 * 空表时金额项均为 0。
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

    /**
     * 本月进账
     */
    private BigDecimal monthlyIncome;

    /**
     * 本月支出
     */
    private BigDecimal monthlyExpense;

    /**
     * 按分类汇总明细
     */
    private List<CategorySummary> categorySummaries;

    /**
     * CategorySummary
     * <p>
     * 单个分类的汇总：类型 + 分类名称 + 合计金额。
     */
    @Data
    public static class CategorySummary {
        /**
         * 分类名称
         */
        private String category;

        /**
         * 收支类型
         */
        private String type;

        /**
         * 合计金额
         */
        private BigDecimal totalAmount;
    }
}

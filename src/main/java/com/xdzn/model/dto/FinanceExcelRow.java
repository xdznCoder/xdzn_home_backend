package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * FinanceExcelRow
 * <p>
 * 经费收支明细导出表头行对象，通过 {@code @ExcelProperty} 定义列名与顺序。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceExcelRow {

    /**
     * 发生时间
     */
    @ExcelProperty("发生时间")
    private String occurredAt;

    /**
     * 类型
     */
    @ExcelProperty("类型")
    private String type;

    /**
     * 分类
     */
    @ExcelProperty("分类")
    private String category;

    /**
     * 金额（元）
     */
    @ExcelProperty("金额")
    private BigDecimal amount;

    /**
     * 说明
     */
    @ExcelProperty("说明")
    private String description;

    /**
     * 操作人
     */
    @ExcelProperty("操作人")
    private String operatorName;
}

package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InternshipExcelRow
 * <p>
 * 实习记录导出表头行。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipExcelRow {

    @ExcelProperty("成员")
    private String memberName;

    @ExcelProperty("公司")
    private String company;

    @ExcelProperty("岗位")
    private String position;

    @ExcelProperty("开始时间")
    private String startDate;

    @ExcelProperty("结束时间")
    private String endDate;

    @ExcelProperty("描述")
    private String description;
}

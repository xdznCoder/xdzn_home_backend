package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AwardExcelRow
 * <p>
 * 获奖记录导出表头行。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwardExcelRow {

    @ExcelProperty("获奖人")
    private String memberName;

    @ExcelProperty("比赛名称")
    private String competition;

    @ExcelProperty("获奖时间")
    private String awardTime;

    @ExcelProperty("级别")
    private String level;

    @ExcelProperty("获奖等级")
    private String rank;

    @ExcelProperty("获奖证明")
    private String certificate;
}

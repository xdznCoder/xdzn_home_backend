package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JoinExcelRow
 * <p>
 * 招新报名导出表头行对象。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinExcelRow {

    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 年级
     */
    @ExcelProperty("年级")
    private String grade;

    /**
     * 方向
     */
    @ExcelProperty("方向")
    private String direction;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 报名时间
     */
    @ExcelProperty("报名时间")
    private String createdAt;
}

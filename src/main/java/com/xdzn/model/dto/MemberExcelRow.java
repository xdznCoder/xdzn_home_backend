package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MemberExcelRow
 * <p>
 * 成员 Excel 导入导出用的表头行对象，通过 {@code @ExcelProperty} 定义列名与顺序。
 * <p>
 * 导入约定：ID 列可选——有 ID 且存在则更新该成员，无 ID 则新增。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberExcelRow {

    /**
     * 成员 ID（导入时可选：有值则更新，为空则新增）
     */
    @ExcelProperty("ID")
    private Long id;

    /**
     * 姓名（必填）
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 头像地址
     */
    @ExcelProperty("头像URL")
    private String avatar;

    /**
     * 所属方向（必填）
     */
    @ExcelProperty("方向")
    private String direction;

    /**
     * 毕业年份
     */
    @ExcelProperty("毕业年份")
    private Integer graduationYear;

    /**
     * 当前所在公司
     */
    @ExcelProperty("当前公司")
    private String currentCompany;

    /**
     * 当前职位
     */
    @ExcelProperty("当前职位")
    private String currentRole;

    /**
     * 展示排序号
     */
    @ExcelProperty("排序")
    private Integer order;
}

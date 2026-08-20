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
     * 年级
     */
    @ExcelProperty("年级")
    private Integer grade;

    /**
     * 学号
     */
    @ExcelProperty("学号")
    private String studentNo;

    /**
     * 专业
     */
    @ExcelProperty("专业")
    private String major;

    /**
     * 团队职务
     */
    @ExcelProperty("团队职务")
    private String teamRole;

    /**
     * 实习经历
     */
    @ExcelProperty("实习经历")
    private String internship;

    /**
     * 获奖经历
     */
    @ExcelProperty("获奖经历")
    private String awards;

    /**
     * 展示排序号
     */
    @ExcelProperty("排序")
    private Integer order;
}

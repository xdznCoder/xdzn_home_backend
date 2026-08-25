package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TaskExcelRow
 * <p>
 * 任务导出表头行对象。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExcelRow {

    /**
     * 任务标题
     */
    @ExcelProperty("任务标题")
    private String title;

    /**
     * 优先级
     */
    @ExcelProperty("优先级")
    private String priority;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 截止时间
     */
    @ExcelProperty("截止时间")
    private String dueDate;

    /**
     * 指派成员
     */
    @ExcelProperty("指派成员")
    private String assignees;

    /**
     * 布置人
     */
    @ExcelProperty("布置人")
    private String creatorName;

    /**
     * 说明
     */
    @ExcelProperty("说明")
    private String description;
}

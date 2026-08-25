package com.xdzn.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResourceExcelRow
 * <p>
 * 资源分享导出表头行对象。
 *
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceExcelRow {

    /**
     * 标题
     */
    @ExcelProperty("标题")
    private String title;

    /**
     * 分类
     */
    @ExcelProperty("分类")
    private String categoryName;

    /**
     * 标签
     */
    @ExcelProperty("标签")
    private String tags;

    /**
     * 上传人
     */
    @ExcelProperty("上传人")
    private String uploaderName;

    /**
     * 附件
     */
    @ExcelProperty("附件")
    private String attachmentName;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createdAt;

    /**
     * 说明
     */
    @ExcelProperty("说明")
    private String description;
}

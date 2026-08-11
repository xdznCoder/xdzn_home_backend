package com.xdzn.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * MemberImportResult
 * <p>
 * 成员 Excel 批量导入结果，汇总成功/失败条数，并记录每行失败原因，
 * 便于调用方定位并修正数据后重导。
 *
 * @author xdzn
 */
@Data
public class MemberImportResult {

    /**
     * 总行数（不含表头）
     */
    private int total;

    /**
     * 成功导入条数
     */
    private int success;

    /**
     * 失败条数
     */
    private int fail;

    /**
     * 失败明细（行号 + 原因）
     */
    private List<ErrorRow> errors = new ArrayList<>();

    /**
     * ErrorRow
     * <p>
     * 单行失败信息。
     */
    @Data
    @AllArgsConstructor
    public static class ErrorRow {

        /**
         * Excel 数据行号（第 1 行为表头，数据从第 2 行起）
         */
        private int row;

        /**
         * 失败原因
         */
        private String message;
    }
}

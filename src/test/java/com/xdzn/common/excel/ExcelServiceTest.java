package com.xdzn.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ExcelServiceTest
 * <p>
 * 验证 {@link ExcelService} 的导出（toBytes）→ 导入（fromBytes）闭环。
 *
 * @author xdzn
 */
class ExcelServiceTest {

    /**
     * 测试用表头类：通过 {@code @ExcelProperty} 定义列名与顺序
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestRow {

        @ExcelProperty("ID")
        private Long id;

        @ExcelProperty("姓名")
        private String name;

        @ExcelProperty("方向")
        private String direction;
    }

    private final ExcelService excelService = new ExcelService();

    /**
     * 导出列表 → 字节 → 导入回读，验证数据一致
     */
    @Test
    void roundTripExportImport() {
        List<TestRow> data = List.of(
                new TestRow(1L, "张三", "后端"),
                new TestRow(2L, "李四", "前端"));

        byte[] bytes = excelService.toBytes(data, TestRow.class, "成员");
        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "生成的 Excel 字节不应为空");

        List<TestRow> back = excelService.fromBytes(bytes, TestRow.class);
        assertNotNull(back);
        assertEquals(2, back.size(), "应读回 2 行数据");
        assertEquals(1L, back.get(0).getId());
        assertEquals("张三", back.get(0).getName());
        assertEquals("后端", back.get(0).getDirection());
        assertEquals("李四", back.get(1).getName());
    }

    /**
     * 空列表导出后仅含表头，读回应为空列表
     */
    @Test
    void exportEmptyList() {
        byte[] bytes = excelService.toBytes(List.of(), TestRow.class, "空列表");
        assertNotNull(bytes);

        List<TestRow> back = excelService.fromBytes(bytes, TestRow.class);
        assertNotNull(back);
        assertTrue(back.isEmpty(), "空列表导出后读回应为空");
    }
}

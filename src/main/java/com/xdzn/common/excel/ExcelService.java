package com.xdzn.common.excel;

import com.alibaba.excel.EasyExcel;
import com.xdzn.common.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ExcelService
 * <p>
 * 基于 EasyExcel 的通用 Excel 导入导出封装，屏蔽底层 POI 细节，
 * 业务方只需定义「表头类」（用 {@code @ExcelProperty} 注解声明列名与顺序）即可复用。
 *
 * <pre>{@code
 * // 表头类示例
 * @Data
 * public class MemberExcelRow {
 *     @ExcelProperty("姓名")
 *     private String name;
 *     @ExcelProperty("方向")
 *     private String direction;
 * }
 * }</pre>
 *
 * 主要能力：
 * <ul>
 *     <li>{@link #export}：将对象列表导出为 Excel 并触发浏览器下载（自动处理中文文件名）</li>
 *     <li>{@link #toBytes}：导出为字节数组（供测试或二次封装复用）</li>
 *     <li>{@link #importExcel}：读取上传的 Excel 文件为对象列表</li>
 *     <li>{@link #fromBytes}：从字节数组解析为对象列表（供测试或二次封装复用）</li>
 * </ul>
 *
 * @author xdzn
 */
@Component
public class ExcelService {

    /**
     * Excel 2007+（.xlsx）的 MIME 类型
     */
    private static final String CONTENT_TYPE_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * 导出 Excel 到 HTTP 响应，触发浏览器下载
     *
     * @param response   HTTP 响应
     * @param data       数据列表
     * @param headClass  表头类（通过 {@code @ExcelProperty} 定义列）
     * @param sheetName  Sheet 名称
     * @param fileName   下载文件名（不含扩展名，自动追加 .xlsx；中文名自动做 URL 编码）
     * @param <T>        行数据类型
     */
    public <T> void export(HttpServletResponse response, List<T> data, Class<T> headClass,
                           String sheetName, String fileName) {
        // 生成文件字节（异常统一抛 BusinessException，由全局异常处理器兜底）
        byte[] bytes = toBytes(data, headClass, sheetName);

        // 设置响应头：Excel 类型 + 附件下载（RFC 5987 支持中文文件名）
        response.setContentType(CONTENT_TYPE_XLSX);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedName + ".xlsx");

        try {
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "写出 Excel 响应失败: " + e.getMessage());
        }
    }

    /**
     * 将对象列表导出为 Excel 字节数组（内存中生成，不依赖 HTTP 环境）
     *
     * @param data      数据列表
     * @param headClass 表头类
     * @param sheetName Sheet 名称
     * @param <T>       行数据类型
     * @return .xlsx 文件字节
     */
    public <T> byte[] toBytes(List<T> data, Class<T> headClass, String sheetName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out, headClass)
                    .sheet(sheetName)
                    .doWrite(data);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "生成 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * 读取上传的 Excel 文件为对象列表
     * <p>
     * 表头类字段应与 Excel 列一一对应（按 {@code @ExcelProperty} 顺序），
     * 多余列默认忽略，缺失列对应字段为 null。
     *
     * @param file      上传的 Excel 文件（.xlsx）
     * @param headClass 表头类
     * @param <T>       行数据类型
     * @return 解析后的对象列表
     */
    public <T> List<T> importExcel(MultipartFile file, Class<T> headClass) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }
        try (InputStream in = file.getInputStream()) {
            return fromBytes(in.readAllBytes(), headClass);
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "读取 Excel 文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 Excel 字节数组解析为对象列表（同步读取第一个 Sheet）
     *
     * @param bytes     .xlsx 文件字节
     * @param headClass 表头类
     * @param <T>       行数据类型
     * @return 解析后的对象列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> fromBytes(byte[] bytes, Class<T> headClass) {
        try {
            // EasyExcel 同步读取：以 headClass 作为表头映射，返回 List
            return (List<T>) EasyExcel.read(new java.io.ByteArrayInputStream(bytes))
                    .head(headClass)
                    .sheet()
                    .doReadSync();
        } catch (RuntimeException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "解析 Excel 失败: " + e.getMessage());
        }
    }
}

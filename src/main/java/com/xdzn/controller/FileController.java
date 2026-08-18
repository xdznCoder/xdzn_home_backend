package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.vo.FileVO;
import com.xdzn.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileController
 * <p>
 * 通用文件上传下载接口（需登录）：
 * <ul>
 *     <li>上传任意常见格式文件（图片/文档/压缩包等），返回访问地址供业务方（如任务附件）使用</li>
 *     <li>按 id 下载/访问文件，支持中文文件名</li>
 *     <li>删除文件</li>
 * </ul>
 *
 * @author xdzn
 */
@Validated
@Tag(name = "文件接口", description = "通用文件上传 / 下载 / 删除（需登录）")
@RestController
@RequestMapping("/api/files")
public class FileController {

    /**
     * 文件服务
     */
    private final FileService fileService;

    /**
     * 构造注入文件服务
     *
     * @param fileService 文件服务
     */
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传文件
     *
     * @param file 上传文件（表单字段 file）
     * @return 文件视图（含访问地址 url）
     */
    @Operation(summary = "上传文件", description = "上传任意常见格式文件（单文件最大 20MB），返回访问地址 url 供业务方使用")
    @PostMapping
    public Result<FileVO> upload(
            @Parameter(description = "上传文件", required = true)
            @RequestParam("file") MultipartFile file) {
        return Result.ok(fileService.upload(file));
    }

    /**
     * 下载/访问文件
     *
     * @param id       文件记录 id
     * @param response HTTP 响应
     */
    @Operation(summary = "下载/访问文件", description = "根据 id 下载文件，按 MIME 类型展示或下载，支持中文文件名")
    @GetMapping("/{id}")
    public void download(
            @Parameter(description = "文件记录 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id,
            HttpServletResponse response) {
        fileService.download(id, response);
    }

    /**
     * 删除文件
     *
     * @param id 文件记录 id
     * @return 操作结果
     */
    @Operation(summary = "删除文件", description = "删除文件记录与磁盘文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "文件记录 id", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "id 不合法") Long id) {
        fileService.delete(id);
        return Result.ok();
    }
}

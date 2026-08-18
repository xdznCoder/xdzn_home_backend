package com.xdzn.controller;

import com.xdzn.common.BusinessException;
import com.xdzn.common.Result;
import com.xdzn.service.ImageHostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * ImageController
 * <p>
 * 图床接口：上传图片到图床服务，返回公开可访问的 URL。
 * 供成员头像、项目封面等官网展示图片使用（用户选图上传，无需手动粘贴图床链接）。
 *
 * @author xdzn
 */
@Tag(name = "图床接口", description = "上传图片到图床，返回公开可访问 URL（供成员头像、项目封面等使用，需 admin 权限）")
@RestController
@RequestMapping("/api/images")
public class ImageController {

    /**
     * 图床服务
     */
    private final ImageHostService imageHostService;

    /**
     * 构造注入图床服务
     *
     * @param imageHostService 图床服务
     */
    public ImageController(ImageHostService imageHostService) {
        this.imageHostService = imageHostService;
    }

    /**
     * 上传图片到图床
     *
     * @param file 图片文件（jpg/png/gif/webp 等，≤5MB）
     * @return 图床公开 URL
     */
    @Operation(summary = "上传图片到图床", description = "上传图片（仅图片类型，≤5MB），返回图床公开 URL，需 admin 权限")
    @PostMapping
    public Result<Map<String, String>> upload(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "图片不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "仅支持图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "图片大小不能超过 5MB");
        }
        try {
            String url = imageHostService.uploadImage(file.getBytes(), file.getOriginalFilename());
            return Result.ok(Map.of("url", url));
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "读取图片失败");
        }
    }
}

package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ResourceDto
 * <p>
 * 资源分享请求 DTO（成员上传/编辑资源用）。
 *
 * @author xdzn
 */
@Data
public class ResourceDto {

    /**
     * 标题，必填
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题不能超过 255 字符")
    private String title;

    /**
     * 分类 ID（可选）
     */
    private Long categoryId;

    /**
     * 描述
     */
    @Size(max = 2000, message = "描述过长")
    private String description;

    /**
     * 标签（逗号分隔，可多个）
     */
    @Size(max = 255, message = "标签过长")
    private String tags;

    /**
     * 附件 URL
     */
    @Size(max = 512, message = "附件 URL 过长")
    private String attachmentUrl;

    /**
     * 附件文件名
     */
    @Size(max = 255, message = "附件名过长")
    private String attachmentName;
}

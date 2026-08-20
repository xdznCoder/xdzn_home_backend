package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ResourceVO
 * <p>
 * 资源分享视图对象，返回资源详情/列表（含分类名、上传人姓名）。
 *
 * @author xdzn
 */
@Data
public class ResourceVO {

    /**
     * 资源 id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 附件 URL
     */
    private String attachmentUrl;

    /**
     * 附件文件名
     */
    private String attachmentName;

    /**
     * 上传人 ID
     */
    private Long uploaderId;

    /**
     * 上传人姓名
     */
    private String uploaderName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

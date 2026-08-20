package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Resource
 * <p>
 * 团队内部资源分享实体，对应数据库表 {@code resources}。
 * 成员可上传资源帖子（标题/分类/标签/附件等），并通过关键词检索共享。
 *
 * @author xdzn
 */
@Data
@TableName("resources")
public class Resource {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类 ID（关联 resource_categories）
     */
    private Long categoryId;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签（逗号分隔，可多个）
     */
    private String tags;

    /**
     * 附件 URL
     */
    private String attachmentUrl;

    /**
     * 附件文件名（展示用）
     */
    private String attachmentName;

    /**
     * 上传人 ID（关联 users）
     */
    private Long uploaderId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

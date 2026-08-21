package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AnnouncementVO
 * <p>
 * 公告视图对象，用于返回给前端。
 * 包含扩展字段creatorName(创建人姓名)。
 *
 * @author xdzn
 */
@Data
public class AnnouncementVO {

    private Long id;

    private String title;

    private String content;

    private Integer isTop;

    private String status;

    private LocalDateTime publishedAt;

    private Long createdBy;

    /**
     * 创建人姓名
     */
    private String creatorName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

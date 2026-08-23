package com.xdzn.model.vo;

import com.xdzn.model.entity.QqGroup;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AnnouncementVO
 * <p>
 * 公告视图对象，返回公告详情/列表（含发布人姓名、目标 QQ 群）。
 *
 * @author xdzn
 */
@Data
public class AnnouncementVO {

    /**
     * 公告 id
     */
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布人 ID
     */
    private Long authorId;

    /**
     * 发布人姓名
     */
    private String authorName;

    /**
     * 是否发送到成员邮箱（1 是 / 0 否）
     */
    private Integer sendEmail;

    /**
     * 目标 QQ 群列表（区分针对不同群发布）
     */
    private List<QqGroup> targetGroups;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Announcement
 * <p>
 * 公告实体，对应数据库表 {@code announcements}。
 * 公告可针对不同 QQ 群发布（关联 {@link AnnouncementTarget}），并可选择发送到成员邮箱。
 *
 * @author xdzn
 */
@Data
@TableName("announcements")
public class Announcement {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 是否置顶（1 置顶 / 0 普通）
     */
    private Integer isTop;

    /**
     * 状态：draft（草稿）/ published（已发布）/ archived（归档）
     */
    private String status;

    /**
     * 发布人 ID（关联 users）
     */
    private Long authorId;

    /**
     * 是否发送到成员邮箱（1 是 / 0 否）
     */
    private Integer sendEmail;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

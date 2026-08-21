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
 * 用于站内通知，支持管理员CRUD和成员查看已发布公告。
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
     * 是否置顶(0否1是)
     */
    private Integer isTop;

    /**
     * 状态(draft/published/archived)
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建人ID(关联users.id)
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TimelineEvent
 * <p>
 * 大事记/时间线事件实体，对应数据库表 {@code timeline_events}。
 * 记录团队发展历程中的重要节点（年份、标题、描述），用于官网时间线展示。
 *
 * @author xdzn
 */
@Data
@TableName("timeline_events")
public class TimelineEvent {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 事件发生年份（如：2023）
     */
    private String year;

    /**
     * 事件标题
     */
    private String title;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 展示排序号（升序排列，值越小越靠前）；order 为 MySQL 保留字，需反引号转义
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

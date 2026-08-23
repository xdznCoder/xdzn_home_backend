package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * QqGroup
 * <p>
 * QQ 群配置实体，对应数据库表 {@code qq_groups}。
 * 公告发布时可选择目标 QQ 群（多对多关联，见 {@link AnnouncementTarget}）。
 *
 * @author xdzn
 */
@Data
@TableName("qq_groups")
public class QqGroup {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * QQ 群号（唯一）
     */
    private String groupNo;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 用途（如 新生群/核心群/比赛群）
     */
    private String purpose;

    /**
     * 是否启用（1 启用 / 0 停用）
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

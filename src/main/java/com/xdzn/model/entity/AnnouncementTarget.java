package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AnnouncementTarget
 * <p>
 * 公告-目标 QQ 群关联实体，对应数据库表 {@code announcement_targets}（复合主键）。
 * 一条公告可关联多个 QQ 群，实现「区分针对不同群聊发布公告」。
 *
 * @author xdzn
 */
@Data
@TableName("announcement_targets")
public class AnnouncementTarget {

    /**
     * 公告 ID
     */
    private Long announcementId;

    /**
     * 目标 QQ 群 ID（关联 qq_groups）
     */
    private Long qqGroupId;
}

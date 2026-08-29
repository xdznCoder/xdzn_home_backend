package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AwardRecord
 * <p>
 * 获奖记录实体，对应数据库表 {@code award_records}。
 * 与成员为一对多关系（{@code member_id} 关联 members），
 * 每条获奖单独存储（比赛名/获奖时间/级别/等级/证明）。
 *
 * @author xdzn
 */
@Data
@TableName("award_records")
public class AwardRecord {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 获奖人成员 ID（关联 members）
     */
    private Long memberId;

    /**
     * 比赛/竞赛名称
     */
    private String competition;

    /**
     * 获奖时间
     */
    private LocalDateTime awardTime;

    /**
     * 级别：national（国家级）/ provincial（省级）
     */
    private String level;

    /**
     * 获奖等级（一等奖/二等奖等）
     */
    @TableField(value = "`rank`")
    private String rank;

    /**
     * 获奖证明 URL（附件）
     */
    private String certificate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

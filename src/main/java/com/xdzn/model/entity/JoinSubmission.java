package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JoinSubmission
 * <p>
 * 招新报名实体，对应数据库表 {@code join_submissions}。
 * 记录用户提交的加入申请及其处理状态，供管理员在后台审核跟进。
 *
 * @author xdzn
 */
@Data
@TableName("join_submissions")
public class JoinSubmission {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 报名者姓名
     */
    private String name;

    /**
     * 报名者年级
     */
    private String grade;

    /**
     * 报名方向
     */
    private String direction;

    /**
     * 处理状态：pending（待处理）/ contacted（已联系）/ accepted（已通过）/ rejected（已拒绝）
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

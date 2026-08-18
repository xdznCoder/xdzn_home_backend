package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Task
 * <p>
 * 任务实体，对应数据库表 {@code tasks}。
 * 由团队负责人创建并指派给一个或多个成员（指派关系见 {@link TaskAssignee}）。
 *
 * @author xdzn
 */
@Data
@TableName("tasks")
public class Task {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 附件 URL（预留文件上传能力）
     */
    private String attachment;

    /**
     * 创建人 id（团队负责人，关联 users）
     */
    private Long creatorId;

    /**
     * 起始时间
     */
    private LocalDateTime startDate;

    /**
     * 截止时间
     */
    private LocalDateTime dueDate;

    /**
     * 任务状态：todo（待办）/ in_progress（进行中）/ done（已完成）
     */
    private String status;

    /**
     * 优先级：high / medium / low
     */
    private String priority;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

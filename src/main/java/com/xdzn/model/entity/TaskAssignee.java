package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * TaskAssignee
 * <p>
 * 任务-成员关联实体，对应数据库表 {@code task_assignees}。
 * 记录任务与成员的多对多指派关系（组合主键），支持一个任务指派给多人。
 *
 * @author xdzn
 */
@Data
@TableName("task_assignees")
public class TaskAssignee {

    /**
     * 任务 id，关联 {@link Task#getId()}
     */
    private Long taskId;

    /**
     * 被指派成员 id，关联 members 表
     */
    private Long memberId;
}

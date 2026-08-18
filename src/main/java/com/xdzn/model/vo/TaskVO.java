package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskVO
 * <p>
 * 任务视图对象，返回任务详情及其指派成员列表（供负责人/成员查看）。
 *
 * @author xdzn
 */
@Data
public class TaskVO {

    /**
     * 任务 id
     */
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
     * 附件 URL（预留文件上传）
     */
    private String attachment;

    /**
     * 创建人 id（关联 users）
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 起始时间
     */
    private LocalDateTime startDate;

    /**
     * 截止时间
     */
    private LocalDateTime dueDate;

    /**
     * 任务状态：todo / in_progress / done
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

    /**
     * 被指派成员列表（可多个）
     */
    private List<TaskAssigneeVO> assignees;
}

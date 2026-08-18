package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskDto
 * <p>
 * 任务请求 DTO，用于创建和更新任务，并携带指派成员 id 列表（支持多人）。
 *
 * @author xdzn
 */
@Data
public class TaskDto {

    /**
     * 任务标题，必填，最长 255
     */
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 255, message = "标题不能超过 255 字符")
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
     * 起始时间
     */
    private LocalDateTime startDate;

    /**
     * 截止时间，必填
     */
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime dueDate;

    /**
     * 优先级，仅允许 high / medium / low，缺省时由服务层补默认值
     */
    @Pattern(regexp = "^(high|medium|low)$", message = "优先级仅允许 high/medium/low")
    private String priority;

    /**
     * 指派成员 id 列表（members.id，可多个），对应「单体或多人布置任务」
     */
    private List<Long> assigneeIds;
}

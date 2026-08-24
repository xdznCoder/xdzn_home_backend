package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * AnnouncementDto
 * <p>
 * 公告请求 DTO：发布/编辑公告，可指定多个目标 QQ 群并选择是否发送到成员邮箱。
 *
 * @author xdzn
 */
@Data
public class AnnouncementDto {

    /**
     * 公告标题，必填
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 255, message = "标题不能超过 255 字符")
    private String title;

    /**
     * 公告内容，必填
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 是否置顶（1 置顶 / 0 普通）
     */
    private Integer isTop;

    /**
     * 状态：draft（草稿）/ published（已发布）/ archived（归档）
     */
    @Pattern(regexp = "^(draft|published|archived)$", message = "状态仅允许 draft/published/archived")
    private String status;

    /**
     * 目标 QQ 群 id 列表（可多个，用于「区分针对不同群聊发布公告」）
     */
    private List<Long> targetGroupIds;

    /**
     * 是否同时发送到成员邮箱（默认 false）
     */
    private Boolean sendEmail;
}

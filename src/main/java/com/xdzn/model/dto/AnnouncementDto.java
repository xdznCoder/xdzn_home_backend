package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AnnouncementDto
 * <p>
 * 公告请求DTO，用于创建和更新公告信息。
 * 包含参数校验注解，确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class AnnouncementDto {

    /**
     * 公告标题，必填
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题不能超过200个字符")
    private String title;

    /**
     * 公告内容，必填
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 是否置顶(0否1是)
     */
    private Integer isTop;

    /**
     * 状态(draft/published/archived)
     */
    private String status;
}

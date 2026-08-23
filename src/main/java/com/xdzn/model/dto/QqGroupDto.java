package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * QqGroupDto
 * <p>
 * QQ 群配置请求 DTO（队长管理群条目用）。
 *
 * @author xdzn
 */
@Data
public class QqGroupDto {

    /**
     * QQ 群号，必填
     */
    @NotBlank(message = "QQ 群号不能为空")
    @Size(max = 32, message = "群号不能超过 32 字符")
    private String groupNo;

    /**
     * 群名称，必填
     */
    @NotBlank(message = "群名称不能为空")
    @Size(max = 64, message = "群名称不能超过 64 字符")
    private String groupName;

    /**
     * 用途（如 新生群/核心群/比赛群）
     */
    @Size(max = 128, message = "用途不能超过 128 字符")
    private String purpose;

    /**
     * 是否启用（1 启用 / 0 停用）
     */
    private Integer enabled;
}

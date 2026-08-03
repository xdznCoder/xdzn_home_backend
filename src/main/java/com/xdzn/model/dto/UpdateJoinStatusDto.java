package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * UpdateJoinStatusDto
 * <p>
 * 更新招新报名状态的请求 DTO，仅允许设置枚举范围内的状态值。
 *
 * @author xdzn
 */
@Data
public class UpdateJoinStatusDto {

    /**
     * 目标状态，仅允许 pending / contacted / accepted / rejected 四种取值
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(pending|contacted|accepted|rejected)$", message = "状态值非法")
    private String status;
}

package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CreateJoinDto
 * <p>
 * 创建招新报名的请求 DTO，携带报名表单的必填字段并做参数校验。
 *
 * @author xdzn
 */
@Data
public class CreateJoinDto {

    /**
     * 报名者姓名，必填
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 报名者年级，必填
     */
    @NotBlank(message = "年级不能为空")
    private String grade;

    /**
     * 报名方向，必填
     */
    @NotBlank(message = "方向不能为空")
    private String direction;
}

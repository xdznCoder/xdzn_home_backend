package com.xdzn.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RegisterDto
 * <p>
 * 注册请求 DTO，携带用户名、邮箱与密码并做参数校验。
 *
 * @author xdzn
 */
@Data
public class RegisterDto {

    /**
     * 用户名，必填
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 注册邮箱，必填且需符合邮箱格式
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 注册密码，必填且长度至少 6 位
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
}

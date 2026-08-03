package com.xdzn.model.dto;

import com.xdzn.model.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * AuthUser
 * <p>
 * 认证用户信息 DTO，返回给前端的安全用户信息（不含密码等敏感字段）。
 *
 * @author xdzn
 */
@Data
@Builder
public class AuthUser {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色（admin / member）
     */
    private String role;

    /**
     * 从 {@link User} 实体转换为安全用户 DTO（剔除密码等敏感字段）
     *
     * @param user 用户实体
     * @return 认证用户 DTO
     */
    public static AuthUser from(User user) {
        return AuthUser.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

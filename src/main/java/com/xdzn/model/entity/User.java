package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User
 * <p>
 * 用户实体，对应数据库表 {@code users}。
 * 管理平台注册用户，包含普通用户与管理员两种角色。
 *
 * @author xdzn
 */
@Data
@TableName("users")
public class User {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 邮箱（登录账号，全局唯一）
     */
    private String email;

    /**
     * 密码（BCrypt 加密存储）
     */
    private String password;

    /**
     * 用户名
     */
    private String name;

    /**
     * 角色：admin（管理员）/ member（普通用户）
     */
    private String role;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记：0 未删除，1 已删除
     */
    @TableLogic
    private Integer deleted;
}

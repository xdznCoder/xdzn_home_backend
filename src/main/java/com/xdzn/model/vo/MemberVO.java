package com.xdzn.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * MemberVO
 * <p>
 * 内部成员视图，包含敏感字段（userId、phone等）。
 * 仅用于内部自助服务接口返回。
 */
@Data
public class MemberVO {
    private Long id;
    private String name;
    private String avatar;
    private String direction;
    private Integer graduationYear;
    private Integer grade;
    private String studentNo;
    private String major;
    private String teamRole;
    private String internship;
    private String awards;
    private Integer order;

    /** 关联用户ID */
    private Long userId;

    /** 手机号 */
    private String phone;

    /** 联系邮箱 */
    private String emailContact;

    /** 技能标签 */
    private String skills;

    /** 个人简介 */
    private String bio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
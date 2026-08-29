package com.xdzn.model.vo;

import lombok.Data;

/**
 * MemberPublicVO
 * <p>
 * 成员公开视图，用于官网展示（不包含内部敏感信息）。
 *
 * @author xdzn
 */
@Data
public class MemberPublicVO {
    
    private Long id;
    
    private String name;
    
    private String avatar;
    
    private String direction;
    
    private Integer graduationYear;

    private Integer grade;
    
    private String major;

    private String teamRole;

    private Integer order;

    /** 登录账号身份（captain 队长 / alumni 已毕业 / member 普通；无账号为 null） */
    private String role;
}
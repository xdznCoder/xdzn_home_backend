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

    private String internship;

    private String awards;
    
    private Integer order;
}
package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * MemberDto
 * <p>
 * 成员请求DTO,用于创建和更新成员信息。
 * 包含参数校验注解,确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class MemberDto {

    /**
     * 成员姓名,必填
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 成员头像地址
     */
    private String avatar;

    /**
     * 所属方向(如:后端、前端、算法等),必填
     */
    @NotBlank(message = "方向不能为空")
    private String direction;

    /**
     * 毕业年份,必填
     */
    @NotNull(message = "毕业年份不能为空")
    private Integer graduationYear;

    /**
     * 当前所在公司
     */
    private String currentCompany;

    /**
     * 当前担任职位
     */
    private String currentRole;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 联系邮箱
     */
    private String emailContact;

    /**
     * 技能标签
     */
    private String skills;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 展示排序号(升序排列,值越小越靠前)
     */
    private Integer order;
}
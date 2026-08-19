package com.xdzn.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    /**
     * 是否同时创建登录账号（仅新增成员时生效；为 true 时 accountEmail/accountPassword 必填）
     */
    private Boolean createAccount;

    /**
     * 登录账号邮箱（创建账号时必填，将写入 users 表并关联成员）
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱过长")
    private String accountEmail;

    /**
     * 登录账号密码（创建账号时必填，BCrypt 加密存储）
     */
    @Size(min = 6, max = 64, message = "密码长度需在 6-64 位")
    private String accountPassword;
}
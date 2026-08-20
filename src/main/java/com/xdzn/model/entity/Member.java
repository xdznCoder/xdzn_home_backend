package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Member
 * <p>
 * 团队成员实体，对应数据库表 {@code members}。
 * 用于官网展示团队成员信息（姓名、头像、方向、就职情况等）。
 *
 * @author xdzn
 */
@Data
@TableName("members")
public class Member {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 成员姓名
     */
    private String name;

    /**
     * 成员头像地址
     */
    private String avatar;

    /**
     * 所属方向（如：后端、前端、算法等）
     */
    private String direction;

    /**
     * 毕业年份
     */
    private Integer graduationYear;

    /**
     * 专业
     */
    private String major;

    /**
     * 团队职务（如 负责人/核心成员/成员）
     */
    private String teamRole;

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
     * 展示排序号（升序排列，值越小越靠前）
     */
    @TableField(value = "`order`")
    private Integer order;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

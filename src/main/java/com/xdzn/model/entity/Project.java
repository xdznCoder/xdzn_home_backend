package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Project
 * <p>
 * 项目实体，对应数据库表 {@code projects}。
 * 用于官网展示团队做过的项目信息，项目与 {@link TechStackItem} 通过
 * {@link ProjectTechStack} 关联表建立多对多关系。
 *
 * @author xdzn
 */
@Data
@TableName("projects")
public class Project {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 项目标题
     */
    private String title;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目主题色
     */
    private String color;

    /**
     * 项目外链地址
     */
    private String link;

    /**
     * 项目封面图片地址
     */
    private String image;

    /**
     * 展示排序号（升序排列，值越小越靠前）
     */
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

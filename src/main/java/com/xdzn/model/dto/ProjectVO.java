package com.xdzn.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProjectVO
 * <p>
 * 项目视图对象 DTO，在 {@link com.xdzn.model.entity.Project} 基础上
 * 追加该项目使用的技术栈名称列表，用于官网项目展示。
 *
 * @author xdzn
 */
@Data
public class ProjectVO {

    /**
     * 项目 id
     */
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
     * 展示排序号
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

    /**
     * 项目使用的技术栈名称列表
     */
    private List<String> techStack;
}

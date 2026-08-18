package com.xdzn.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * ProjectDto
 * <p>
 * 项目请求DTO,用于创建和更新项目信息。
 * 包含参数校验注解,确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class ProjectDto {

    /**
     * 项目标题,必填
     */
    @NotBlank(message = "项目标题不能为空")
    private String title;

    /**
     * 项目描述,必填
     */
    @NotBlank(message = "项目描述不能为空")
    private String description;

    /**
     * 项目主题色,必填
     */
    @NotBlank(message = "主题色不能为空")
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
     * 展示排序号(升序排列,值越小越靠前)
     */
    @Min(value = 0, message = "排序号不能为负数")
    @Max(value = 9999, message = "排序号不能超过 9999")
    private Integer order;

    /**
     * 技术栈id列表,用于关联技术栈
     */
    private List<Long> techStackIds;
}
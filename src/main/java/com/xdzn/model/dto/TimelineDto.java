package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TimelineDto
 * <p>
 * 时间线事件请求DTO,用于创建和更新时间线事件。
 * 包含参数校验注解,确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class TimelineDto {

    /**
     * 事件发生年份,必填
     */
    @NotBlank(message = "年份不能为空")
    private String year;

    /**
     * 事件标题,必填
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 事件描述,必填
     */
    @NotBlank(message = "描述不能为空")
    private String description;

    /**
     * 展示排序号(升序排列,值越小越靠前)
     */
    private Integer order;
}
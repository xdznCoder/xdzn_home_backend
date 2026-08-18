package com.xdzn.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TechStackDto
 * <p>
 * 技术栈请求DTO,用于创建和更新技术栈信息。
 * 包含参数校验注解,确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class TechStackDto {

    /**
     * 技术栈名称,必填
     */
    @NotBlank(message = "技术栈名称不能为空")
    private String name;

    /**
     * 展示颜色,必填
     */
    @NotBlank(message = "颜色不能为空")
    private String color;

    /**
     * 使用数量/统计值,必填
     */
    @NotNull(message = "使用数量不能为空")
    private Integer count;

    /**
     * 描述说明,必填
     */
    @NotBlank(message = "描述不能为空")
    private String desc;

    /**
     * 展示排序号(升序排列,值越小越靠前)
     */
    @Min(value = 0, message = "排序号不能为负数")
    @Max(value = 9999, message = "排序号不能超过 9999")
    private Integer order;
}
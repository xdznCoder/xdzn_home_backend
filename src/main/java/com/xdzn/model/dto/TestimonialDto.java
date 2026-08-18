package com.xdzn.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TestimonialDto
 * <p>
 * 用户评价请求DTO,用于创建和更新评价信息。
 * 包含参数校验注解,确保请求数据的合法性。
 *
 * @author xdzn
 */
@Data
public class TestimonialDto {

    /**
     * 评价者姓名,必填
     */
    @NotBlank(message = "评价者姓名不能为空")
    private String name;

    /**
     * 评价者头像地址
     */
    private String avatar;

    /**
     * 评价者所属方向,必填
     */
    @NotBlank(message = "方向不能为空")
    private String direction;

    /**
     * 评价内容,必填
     */
    @NotBlank(message = "评价内容不能为空")
    private String quote;

    /**
     * 毕业年份,必填
     */
    @NotNull(message = "毕业年份不能为空")
    @Min(value = 1970, message = "毕业年份不合法")
    @Max(value = 2100, message = "毕业年份不合法")
    private Integer graduationYear;

    /**
     * 展示排序号(升序排列,值越小越靠前)
     */
    @Min(value = 0, message = "排序号不能为负数")
    @Max(value = 9999, message = "排序号不能超过 9999")
    private Integer order;
}
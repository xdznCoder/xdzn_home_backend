package com.xdzn.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ResourceCategoryDto
 * <p>
 * 资源分类请求 DTO（队长管理分类用）。
 *
 * @author xdzn
 */
@Data
public class ResourceCategoryDto {

    /**
     * 分类名，必填，最长 64
     */
    @NotBlank(message = "分类名不能为空")
    @Size(max = 64, message = "分类名不能超过 64 字符")
    private String name;

    /**
     * 排序（升序）
     */
    private Integer sort;
}

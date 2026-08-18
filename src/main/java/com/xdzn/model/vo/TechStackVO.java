package com.xdzn.model.vo;

import lombok.Data;

/**
 * TechStackVO
 * <p>
 * 技术栈公开视图对象，仅返回官网展示所需字段（不含创建/更新时间等审计字段）。
 *
 * @author xdzn
 */
@Data
public class TechStackVO {

    /**
     * 技术栈条目 id
     */
    private Long id;

    /**
     * 技术栈名称
     */
    private String name;

    /**
     * 展示颜色
     */
    private String color;

    /**
     * 使用数量/统计值
     */
    private Integer count;

    /**
     * 描述说明
     */
    private String desc;

    /**
     * 展示排序号
     */
    private Integer order;
}

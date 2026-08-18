package com.xdzn.model.vo;

import lombok.Data;

/**
 * TestimonialVO
 * <p>
 * 用户评价公开视图对象，仅返回官网展示所需字段（不含创建/更新时间等审计字段）。
 *
 * @author xdzn
 */
@Data
public class TestimonialVO {

    /**
     * 评价 id
     */
    private Long id;

    /**
     * 评价者姓名
     */
    private String name;

    /**
     * 评价者头像地址
     */
    private String avatar;

    /**
     * 评价者所属方向
     */
    private String direction;

    /**
     * 评价内容
     */
    private String quote;

    /**
     * 毕业年份
     */
    private Integer graduationYear;

    /**
     * 展示排序号
     */
    private Integer order;
}

package com.xdzn.model.vo;

import lombok.Data;

/**
 * TimelineVO
 * <p>
 * 时间线事件公开视图对象，仅返回官网展示所需字段（不含创建/更新时间等审计字段）。
 *
 * @author xdzn
 */
@Data
public class TimelineVO {

    /**
     * 事件 id
     */
    private Long id;

    /**
     * 事件发生年份
     */
    private String year;

    /**
     * 事件标题
     */
    private String title;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 展示排序号
     */
    private Integer order;
}

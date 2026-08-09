package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TechStackItem
 * <p>
 * 技术栈条目实体，对应数据库表 {@code tech_stack_items}。
 * 记录官网展示的技术栈名称、颜色、使用数量与描述等信息。
 *
 * @author xdzn
 */
@Data
@TableName("tech_stack_items")
public class TechStackItem {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 技术栈名称（如：Java、Vue、Spring Boot）
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
    @TableField(value = "`desc`")
    private String desc;

    /**
     * 展示排序号（升序排列，值越小越靠前）
     */
    @TableField(value = "`order`")
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

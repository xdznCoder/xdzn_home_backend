package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Testimonial
 * <p>
 * 用户评价实体，对应数据库表 {@code testimonials}。
 * 记录官网展示的成员/用户评价，用于提升团队可信度。
 *
 * @author xdzn
 */
@Data
@TableName("testimonials")
public class Testimonial {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
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

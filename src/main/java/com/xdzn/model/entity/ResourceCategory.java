package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ResourceCategory
 * <p>
 * 资源分类实体，对应数据库表 {@code resource_categories}。
 * 分类由队长管理，成员上传资源时从分类中选择。
 *
 * @author xdzn
 */
@Data
@TableName("resource_categories")
public class ResourceCategory {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分类名（唯一）
     */
    private String name;

    /**
     * 排序（升序）
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

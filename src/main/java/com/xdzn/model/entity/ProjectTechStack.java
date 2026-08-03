package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ProjectTechStack
 * <p>
 * 项目-技术栈关联实体，对应数据库表 {@code project_tech_stack}。
 * 记录项目与 {@link TechStackItem} 之间的多对多关系（组合主键）。
 *
 * @author xdzn
 */
@Data
@TableName("project_tech_stack")
public class ProjectTechStack {

    /**
     * 项目 id，关联 {@link Project#getId()}
     */
    private Long projectId;

    /**
     * 技术栈条目 id，关联 {@link TechStackItem#getId()}
     */
    private Long techStackId;
}

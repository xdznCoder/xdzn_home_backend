package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.ProjectTechStack;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProjectTechStackMapper
 * <p>
 * 项目-技术栈关联表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link ProjectTechStack} 关联实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface ProjectTechStackMapper extends BaseMapper<ProjectTechStack> {
}

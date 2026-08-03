package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProjectMapper
 * <p>
 * 项目表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link Project} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}

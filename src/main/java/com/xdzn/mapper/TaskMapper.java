package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * TaskMapper
 * <p>
 * 任务表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link Task} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}

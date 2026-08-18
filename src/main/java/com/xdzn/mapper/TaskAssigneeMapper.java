package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.TaskAssignee;
import org.apache.ibatis.annotations.Mapper;

/**
 * TaskAssigneeMapper
 * <p>
 * 任务-成员关联表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link TaskAssignee} 关联实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface TaskAssigneeMapper extends BaseMapper<TaskAssignee> {
}

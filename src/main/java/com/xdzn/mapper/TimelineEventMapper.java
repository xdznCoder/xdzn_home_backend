package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.TimelineEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * TimelineEventMapper
 * <p>
 * 时间线事件表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link TimelineEvent} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface TimelineEventMapper extends BaseMapper<TimelineEvent> {
}

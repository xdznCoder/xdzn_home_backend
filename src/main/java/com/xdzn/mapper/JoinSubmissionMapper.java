package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.JoinSubmission;
import org.apache.ibatis.annotations.Mapper;

/**
 * JoinSubmissionMapper
 * <p>
 * 招新报名表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link JoinSubmission} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface JoinSubmissionMapper extends BaseMapper<JoinSubmission> {
}

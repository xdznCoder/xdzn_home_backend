package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * MemberMapper
 * <p>
 * 团队成员表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link Member} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}

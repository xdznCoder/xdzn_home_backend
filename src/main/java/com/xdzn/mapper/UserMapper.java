package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 * <p>
 * 用户表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link User} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

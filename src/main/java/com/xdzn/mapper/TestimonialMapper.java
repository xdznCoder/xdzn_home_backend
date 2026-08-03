package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Testimonial;
import org.apache.ibatis.annotations.Mapper;

/**
 * TestimonialMapper
 * <p>
 * 用户评价表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link Testimonial} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface TestimonialMapper extends BaseMapper<Testimonial> {
}

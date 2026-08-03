package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.TechStackItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * TechStackItemMapper
 * <p>
 * 技术栈条目表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link TechStackItem} 实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface TechStackItemMapper extends BaseMapper<TechStackItem> {
}

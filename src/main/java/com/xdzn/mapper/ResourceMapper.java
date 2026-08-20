package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

/**
 * ResourceMapper
 * <p>
 * 资源分享表 Mapper。
 *
 * @author xdzn
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
}

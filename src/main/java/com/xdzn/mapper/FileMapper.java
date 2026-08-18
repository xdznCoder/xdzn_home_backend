package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * FileMapper
 * <p>
 * 文件表 Mapper，继承 MyBatis-Plus 的 {@link BaseMapper}，
 * 提供 {@link FileEntity} 元数据实体的增删改查能力。
 *
 * @author xdzn
 */
@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {
}

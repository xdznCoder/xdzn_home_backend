package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.AnnouncementTarget;
import org.apache.ibatis.annotations.Mapper;

/**
 * AnnouncementTargetMapper
 * <p>
 * 公告-目标 QQ 群关联表 Mapper。
 *
 * @author xdzn
 */
@Mapper
public interface AnnouncementTargetMapper extends BaseMapper<AnnouncementTarget> {
}

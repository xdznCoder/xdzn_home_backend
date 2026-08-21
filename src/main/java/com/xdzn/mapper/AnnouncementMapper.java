package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * AnnouncementMapper
 * <p>
 * 公告数据访问接口。
 *
 * @author xdzn
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}

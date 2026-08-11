package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TimelineEventMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TimelineDto;
import com.xdzn.model.entity.TimelineEvent;
import com.xdzn.service.TimelineEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TimelineEventServiceImpl
 * <p>
 * 时间线事件服务实现，提供事件增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code timeline}，TTL 10 分钟），
 * 写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class TimelineEventServiceImpl extends ServiceImpl<TimelineEventMapper, TimelineEvent>
        implements TimelineEventService {

    /**
     * 查询全部时间线事件（按排序号升序），结果缓存 10 分钟
     *
     * @return 事件列表
     */
    @Override
    @Cacheable(value = "timeline", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<TimelineEvent> findAll() {
        return lambdaQuery().orderByAsc(TimelineEvent::getOrder).list();
    }

    /**
     * 分页查询时间线事件（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<TimelineEvent> findAllByPage(long current, long size) {
        Page<TimelineEvent> page = new Page<>(current, size);
        Page<TimelineEvent> result = page(page, new LambdaQueryWrapper<TimelineEvent>().orderByAsc(TimelineEvent::getOrder));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    /**
     * 根据 id 查询时间线事件
     *
     * @param id 事件 id
     * @return 事件信息；不存在时返回 null
     */
    @Override
    public TimelineEvent findById(Long id) {
        return getById(id);
    }

    /**
     * 创建时间线事件，并失效时间线列表缓存
     *
     * @param dto 时间线DTO
     * @return 创建后的事件
     */
    @Override
    @CacheEvict(value = "timeline", key = "'all'")
    public TimelineEvent create(TimelineDto dto) {
        TimelineEvent event = new TimelineEvent();
        BeanUtils.copyProperties(dto, event);
        save(event);
        return event;
    }

    /**
     * 更新时间线事件，并失效时间线列表缓存
     *
     * @param id  事件 id
     * @param dto 时间线DTO
     * @return 更新后的事件
     */
    @Override
    @CacheEvict(value = "timeline", key = "'all'")
    public TimelineEvent update(Long id, TimelineDto dto) {
        TimelineEvent event = new TimelineEvent();
        event.setId(id);
        BeanUtils.copyProperties(dto, event);
        updateById(event);
        return getById(id);
    }

    /**
     * 删除时间线事件，并失效时间线列表缓存
     *
     * @param id 事件 id
     */
    @Override
    @CacheEvict(value = "timeline", key = "'all'")
    public void delete(Long id) {
        removeById(id);
    }
}

package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TimelineEventMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TimelineDto;
import com.xdzn.model.entity.TimelineEvent;
import com.xdzn.model.vo.TimelineVO;
import com.xdzn.service.TimelineEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TimelineEventServiceImpl
 * <p>
 * 时间线事件服务实现，提供事件增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code timeline}，TTL 10 分钟），
 * 写操作自动失效缓存；返回统一为 {@link TimelineVO}（脱敏）。
 *
 * @author xdzn
 */
@Service
public class TimelineEventServiceImpl extends ServiceImpl<TimelineEventMapper, TimelineEvent>
        implements TimelineEventService {

    /**
     * 查询全部时间线事件（按排序号升序），结果缓存 10 分钟
     *
     * @return 事件公开视图列表
     */
    @Override
    @Cacheable(value = "timeline", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<TimelineVO> findAll() {
        return lambdaQuery().orderByAsc(TimelineEvent::getOrder).list().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询时间线事件（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果（公开视图）
     */
    @Override
    public PageResult<TimelineVO> findAllByPage(long current, long size) {
        Page<TimelineEvent> page = new Page<>(current, size);
        Page<TimelineEvent> result = page(page,
                new LambdaQueryWrapper<TimelineEvent>().orderByAsc(TimelineEvent::getOrder));
        List<TimelineVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 根据 id 查询时间线事件
     *
     * @param id 事件 id
     * @return 公开视图；不存在时返回 null
     */
    @Override
    public TimelineVO findById(Long id) {
        TimelineEvent event = getById(id);
        return event == null ? null : toVO(event);
    }

    /**
     * 创建时间线事件，并失效时间线列表缓存
     *
     * @param dto 事件DTO
     * @return 创建后的公开视图
     */
    @Override
    @CacheEvict(value = "timeline", key = "'all'")
    public TimelineVO create(TimelineDto dto) {
        TimelineEvent event = new TimelineEvent();
        BeanUtils.copyProperties(dto, event);
        save(event);
        return toVO(event);
    }

    /**
     * 更新时间线事件，并失效时间线列表缓存
     *
     * @param id  事件 id
     * @param dto 事件DTO
     * @return 更新后的公开视图
     */
    @Override
    @CacheEvict(value = "timeline", key = "'all'")
    public TimelineVO update(Long id, TimelineDto dto) {
        TimelineEvent event = new TimelineEvent();
        event.setId(id);
        BeanUtils.copyProperties(dto, event);
        updateById(event);
        return toVO(getById(id));
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

    // ── 内部方法 ──────────────────────

    /**
     * 将实体转换为公开视图对象（剔除审计字段）
     *
     * @param event 时间线事件实体
     * @return 公开视图
     */
    private TimelineVO toVO(TimelineEvent event) {
        TimelineVO vo = new TimelineVO();
        BeanUtils.copyProperties(event, vo);
        return vo;
    }
}

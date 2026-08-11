package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TimelineDto;
import com.xdzn.model.entity.TimelineEvent;

import java.util.List;

/**
 * TimelineEventService
 * <p>
 * 时间线事件服务接口，定义事件增删改查的契约。
 *
 * @author xdzn
 */
public interface TimelineEventService extends IService<TimelineEvent> {

    /**
     * 查询全部时间线事件（按排序号升序）
     *
     * @return 事件列表
     */
    List<TimelineEvent> findAll();

    /**
     * 分页查询时间线事件（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResult<TimelineEvent> findAllByPage(long current, long size);

    /**
     * 根据 id 查询时间线事件
     *
     * @param id 事件 id
     * @return 事件信息；不存在时返回 null
     */
    TimelineEvent findById(Long id);

    /**
     * 创建时间线事件
     *
     * @param dto 时间线DTO
     * @return 创建后的事件
     */
    TimelineEvent create(TimelineDto dto);

    /**
     * 更新时间线事件
     *
     * @param id  事件 id
     * @param dto 时间线DTO
     * @return 更新后的事件
     */
    TimelineEvent update(Long id, TimelineDto dto);

    /**
     * 删除时间线事件
     *
     * @param id 事件 id
     */
    void delete(Long id);
}

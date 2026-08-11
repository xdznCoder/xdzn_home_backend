package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TechStackDto;
import com.xdzn.model.entity.TechStackItem;
import com.xdzn.service.TechStackItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * TechStackItemServiceImpl
 * <p>
 * 技术栈服务实现，提供技术栈条目增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code techStack}，TTL 10 分钟），
 * 写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class TechStackItemServiceImpl extends ServiceImpl<TechStackItemMapper, TechStackItem>
        implements TechStackItemService {

    /**
     * 查询全部技术栈条目（按排序号升序），结果缓存 10 分钟
     *
     * @return 技术栈条目列表
     */
    @Override
    @Cacheable(value = "techStack", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<TechStackItem> findAll() {
        return lambdaQuery().orderByAsc(TechStackItem::getOrder).list();
    }

    /**
     * 分页查询技术栈条目（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<TechStackItem> findAllByPage(long current, long size) {
        Page<TechStackItem> page = new Page<>(current, size);
        Page<TechStackItem> result = page(page, new LambdaQueryWrapper<TechStackItem>().orderByAsc(TechStackItem::getOrder));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    /**
     * 根据 id 查询技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 技术栈条目；不存在时返回 null
     */
    @Override
    public TechStackItem findById(Long id) {
        return getById(id);
    }

    /**
     * 创建技术栈条目，并失效技术栈列表缓存
     * <p>
     * 如果已存在相同名称的技术栈，则更新该条目而不是创建新的。
     *
     * @param dto 技术栈DTO
     * @return 创建或更新后的技术栈条目
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackItem create(TechStackDto dto) {
        // 检查是否已存在相同名称的技术栈
        TechStackItem existing = lambdaQuery()
                .eq(TechStackItem::getName, dto.getName())
                .one();
        
        if (existing != null) {
            // 如果已存在，则更新现有条目
            BeanUtils.copyProperties(dto, existing);
            updateById(existing);
            return existing;
        }
        
        // 不存在则创建新条目
        TechStackItem item = new TechStackItem();
        BeanUtils.copyProperties(dto, item);
        save(item);
        return item;
    }

    /**
     * 更新技术栈条目，并失效技术栈列表缓存
     *
     * @param id   技术栈条目 id
     * @param dto  技术栈DTO
     * @return 更新后的技术栈条目
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackItem update(Long id, TechStackDto dto) {
        TechStackItem item = new TechStackItem();
        item.setId(id);
        BeanUtils.copyProperties(dto, item);
        updateById(item);
        return getById(id);
    }

    /**
     * 删除技术栈条目，并失效技术栈列表缓存
     *
     * @param id 技术栈条目 id
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public void delete(Long id) {
        removeById(id);
    }
}

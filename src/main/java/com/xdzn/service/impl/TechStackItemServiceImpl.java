package com.xdzn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.model.entity.TechStackItem;
import com.xdzn.service.TechStackItemService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
     *
     * @param item 技术栈条目信息
     * @return 创建后的技术栈条目
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackItem create(TechStackItem item) {
        save(item);
        return item;
    }

    /**
     * 更新技术栈条目，并失效技术栈列表缓存
     *
     * @param id   技术栈条目 id
     * @param item 技术栈条目信息
     * @return 更新后的技术栈条目
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackItem update(Long id, TechStackItem item) {
        item.setId(id);
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

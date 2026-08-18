package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TechStackDto;
import com.xdzn.model.entity.TechStackItem;
import com.xdzn.model.vo.TechStackVO;
import com.xdzn.service.TechStackItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TechStackItemServiceImpl
 * <p>
 * 技术栈服务实现，提供技术栈条目增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code techStack}，TTL 10 分钟），
 * 写操作自动失效缓存；返回统一为 {@link TechStackVO}（脱敏）。
 *
 * @author xdzn
 */
@Service
public class TechStackItemServiceImpl extends ServiceImpl<TechStackItemMapper, TechStackItem>
        implements TechStackItemService {

    /**
     * 查询全部技术栈条目（按排序号升序），结果缓存 10 分钟
     *
     * @return 技术栈公开视图列表
     */
    @Override
    @Cacheable(value = "techStack", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<TechStackVO> findAll() {
        return lambdaQuery().orderByAsc(TechStackItem::getOrder).list().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询技术栈条目（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果（公开视图）
     */
    @Override
    public PageResult<TechStackVO> findAllByPage(long current, long size) {
        Page<TechStackItem> page = new Page<>(current, size);
        Page<TechStackItem> result = page(page,
                new LambdaQueryWrapper<TechStackItem>().orderByAsc(TechStackItem::getOrder));
        List<TechStackVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 根据 id 查询技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 公开视图；不存在时返回 null
     */
    @Override
    public TechStackVO findById(Long id) {
        TechStackItem item = getById(id);
        return item == null ? null : toVO(item);
    }

    /**
     * 创建技术栈条目，并失效技术栈列表缓存
     *
     * @param dto 技术栈DTO
     * @return 创建后的公开视图
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackVO create(TechStackDto dto) {
        TechStackItem item = new TechStackItem();
        BeanUtils.copyProperties(dto, item);
        save(item);
        return toVO(item);
    }

    /**
     * 更新技术栈条目，并失效技术栈列表缓存
     *
     * @param id  技术栈条目 id
     * @param dto 技术栈DTO
     * @return 更新后的公开视图
     */
    @Override
    @CacheEvict(value = "techStack", key = "'all'")
    public TechStackVO update(Long id, TechStackDto dto) {
        TechStackItem item = new TechStackItem();
        item.setId(id);
        BeanUtils.copyProperties(dto, item);
        updateById(item);
        return toVO(getById(id));
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

    // ── 内部方法 ──────────────────────

    /**
     * 将实体转换为公开视图对象（剔除审计字段）
     *
     * @param item 技术栈实体
     * @return 公开视图
     */
    private TechStackVO toVO(TechStackItem item) {
        TechStackVO vo = new TechStackVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }
}

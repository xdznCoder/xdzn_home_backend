package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TestimonialMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TestimonialDto;
import com.xdzn.model.entity.Testimonial;
import com.xdzn.service.TestimonialService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TestimonialServiceImpl
 * <p>
 * 用户评价服务实现，提供评价增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code testimonials}，TTL 10 分钟），
 * 写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class TestimonialServiceImpl extends ServiceImpl<TestimonialMapper, Testimonial>
        implements TestimonialService {

    /**
     * 查询全部评价（按排序号升序），结果缓存 10 分钟
     *
     * @return 评价列表
     */
    @Override
    @Cacheable(value = "testimonials", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<Testimonial> findAll() {
        return lambdaQuery().orderByAsc(Testimonial::getOrder).list();
    }

    /**
     * 分页查询评价（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<Testimonial> findAllByPage(long current, long size) {
        Page<Testimonial> page = new Page<>(current, size);
        Page<Testimonial> result = page(page, new LambdaQueryWrapper<Testimonial>().orderByAsc(Testimonial::getOrder));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    /**
     * 根据 id 查询评价
     *
     * @param id 评价 id
     * @return 评价信息；不存在时返回 null
     */
    @Override
    public Testimonial findById(Long id) {
        return getById(id);
    }

    /**
     * 创建评价，并失效评价列表缓存
     *
     * @param dto 评价DTO
     * @return 创建后的评价
     */
    @Override
    @CacheEvict(value = "testimonials", key = "'all'")
    public Testimonial create(TestimonialDto dto) {
        Testimonial testimonial = new Testimonial();
        BeanUtils.copyProperties(dto, testimonial);
        save(testimonial);
        return testimonial;
    }

    /**
     * 更新评价，并失效评价列表缓存
     *
     * @param id  评价 id
     * @param dto 评价DTO
     * @return 更新后的评价
     */
    @Override
    @CacheEvict(value = "testimonials", key = "'all'")
    public Testimonial update(Long id, TestimonialDto dto) {
        Testimonial testimonial = new Testimonial();
        testimonial.setId(id);
        BeanUtils.copyProperties(dto, testimonial);
        updateById(testimonial);
        return getById(id);
    }

    /**
     * 删除评价，并失效评价列表缓存
     *
     * @param id 评价 id
     */
    @Override
    @CacheEvict(value = "testimonials", key = "'all'")
    public void delete(Long id) {
        removeById(id);
    }
}

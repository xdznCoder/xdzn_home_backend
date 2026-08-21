package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.TestimonialMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TestimonialDto;
import com.xdzn.model.entity.Testimonial;
import com.xdzn.model.vo.TestimonialVO;
import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.CacheRedisKey;
import com.xdzn.service.TestimonialService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TestimonialServiceImpl
 * <p>
 * 用户评价服务实现，提供评价增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code testimonials}，TTL 10 分钟），
 * 写操作自动失效缓存；返回统一为 {@link TestimonialVO}（脱敏）。
 *
 * @author xdzn
 */
@Service
public class TestimonialServiceImpl extends ServiceImpl<TestimonialMapper, Testimonial>
        implements TestimonialService {

    /**
     * 统一 Redis 缓存服务
     */
    private final RedisService redisService;

    /**
     * 构造注入缓存服务
     *
     * @param redisService 统一 Redis 缓存服务
     */
    public TestimonialServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 查询全部评价（按排序号升序），结果缓存 10 分钟
     *
     * @return 评价公开视图列表
     */
    @Override
    public List<TestimonialVO> findAll() {
        return redisService.getOrSetList(CacheRedisKey.TESTIMONIALS, "all", TestimonialVO.class, this::loadAllTestimonials);
    }

    /**
     * 从数据库加载全部评价，供缓存回填
     *
     * @return 评价公开视图列表
     */
    private List<TestimonialVO> loadAllTestimonials() {
        return lambdaQuery().orderByAsc(Testimonial::getOrder).list().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询评价（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果（公开视图）
     */
    @Override
    public PageResult<TestimonialVO> findAllByPage(long current, long size) {
        Page<Testimonial> page = new Page<>(current, size);
        Page<Testimonial> result = page(page,
                new LambdaQueryWrapper<Testimonial>().orderByAsc(Testimonial::getOrder));
        List<TestimonialVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 根据 id 查询评价
     *
     * @param id 评价 id
     * @return 公开视图；不存在时返回 null
     */
    @Override
    public TestimonialVO findById(Long id) {
        return redisService.getOrSet(CacheRedisKey.TESTIMONIALS_DETAIL, String.valueOf(id), TestimonialVO.class, () -> loadTestimonial(id));
    }

    /**
     * 从数据库加载单个评价，供缓存回填
     *
     * @param id 评价 id
     * @return 公开视图；不存在时返回 null
     */
    private TestimonialVO loadTestimonial(Long id) {
        Testimonial testimonial = getById(id);
        return testimonial == null ? null : toVO(testimonial);
    }

    /**
     * 创建评价，并失效评价列表缓存
     *
     * @param dto 评价DTO
     * @return 创建后的公开视图
     */
    @Override
    public TestimonialVO create(TestimonialDto dto) {
        Testimonial testimonial = new Testimonial();
        BeanUtils.copyProperties(dto, testimonial);
        save(testimonial);
        redisService.delete(CacheRedisKey.TESTIMONIALS, "all");
        return toVO(testimonial);
    }

    /**
     * 更新评价，并失效评价列表缓存
     *
     * @param id  评价 id
     * @param dto 评价DTO
     * @return 更新后的公开视图
     */
    @Override
    public TestimonialVO update(Long id, TestimonialDto dto) {
        Testimonial testimonial = new Testimonial();
        testimonial.setId(id);
        BeanUtils.copyProperties(dto, testimonial);
        updateById(testimonial);
        redisService.delete(CacheRedisKey.TESTIMONIALS, "all");
        redisService.delete(CacheRedisKey.TESTIMONIALS_DETAIL, String.valueOf(id));
        return toVO(getById(id));
    }

    /**
     * 删除评价，并失效评价列表缓存
     *
     * @param id 评价 id
     */
    @Override
    public void delete(Long id) {
        removeById(id);
        redisService.delete(CacheRedisKey.TESTIMONIALS, "all");
        redisService.delete(CacheRedisKey.TESTIMONIALS_DETAIL, String.valueOf(id));
    }

    // ── 内部方法 ──────────────────────

    /**
     * 将实体转换为公开视图对象（剔除审计字段）
     *
     * @param testimonial 评价实体
     * @return 公开视图
     */
    private TestimonialVO toVO(Testimonial testimonial) {
        TestimonialVO vo = new TestimonialVO();
        BeanUtils.copyProperties(testimonial, vo);
        return vo;
    }
}

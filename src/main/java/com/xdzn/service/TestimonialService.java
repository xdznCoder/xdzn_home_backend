package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TestimonialDto;
import com.xdzn.model.entity.Testimonial;
import com.xdzn.model.vo.TestimonialVO;

import java.util.List;

/**
 * TestimonialService
 * <p>
 * 用户评价服务接口，定义评价增删改查的契约。
 * 查询结果统一返回公开视图对象 {@link TestimonialVO}（脱敏，不含审计字段）。
 *
 * @author xdzn
 */
public interface TestimonialService extends IService<Testimonial> {

    /**
     * 查询全部评价（按排序号升序）
     *
     * @return 评价公开视图列表
     */
    List<TestimonialVO> findAll();

    /**
     * 分页查询评价（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果（公开视图）
     */
    PageResult<TestimonialVO> findAllByPage(long current, long size);

    /**
     * 根据 id 查询评价
     *
     * @param id 评价 id
     * @return 公开视图；不存在时返回 null
     */
    TestimonialVO findById(Long id);

    /**
     * 创建评价
     *
     * @param dto 评价DTO
     * @return 创建后的公开视图
     */
    TestimonialVO create(TestimonialDto dto);

    /**
     * 更新评价
     *
     * @param id  评价 id
     * @param dto 评价DTO
     * @return 更新后的公开视图
     */
    TestimonialVO update(Long id, TestimonialDto dto);

    /**
     * 删除评价
     *
     * @param id 评价 id
     */
    void delete(Long id);
}

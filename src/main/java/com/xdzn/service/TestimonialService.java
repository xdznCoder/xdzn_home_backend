package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.entity.Testimonial;

import java.util.List;

/**
 * TestimonialService
 * <p>
 * 用户评价服务接口，定义评价增删改查的契约。
 *
 * @author xdzn
 */
public interface TestimonialService extends IService<Testimonial> {

    /**
     * 查询全部评价（按排序号升序）
     *
     * @return 评价列表
     */
    List<Testimonial> findAll();

    /**
     * 根据 id 查询评价
     *
     * @param id 评价 id
     * @return 评价信息；不存在时返回 null
     */
    Testimonial findById(Long id);

    /**
     * 创建评价
     *
     * @param testimonial 评价信息
     * @return 创建后的评价
     */
    Testimonial create(Testimonial testimonial);

    /**
     * 更新评价
     *
     * @param id          评价 id
     * @param testimonial 评价信息
     * @return 更新后的评价
     */
    Testimonial update(Long id, Testimonial testimonial);

    /**
     * 删除评价
     *
     * @param id 评价 id
     */
    void delete(Long id);
}

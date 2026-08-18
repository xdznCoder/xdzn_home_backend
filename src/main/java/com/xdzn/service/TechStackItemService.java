package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TechStackDto;
import com.xdzn.model.entity.TechStackItem;
import com.xdzn.model.vo.TechStackVO;

import java.util.List;

/**
 * TechStackItemService
 * <p>
 * 技术栈服务接口，定义技术栈条目增删改查的契约。
 * 查询结果统一返回公开视图对象 {@link TechStackVO}（脱敏，不含审计字段）。
 *
 * @author xdzn
 */
public interface TechStackItemService extends IService<TechStackItem> {

    /**
     * 查询全部技术栈条目（按排序号升序）
     *
     * @return 技术栈公开视图列表
     */
    List<TechStackVO> findAll();

    /**
     * 分页查询技术栈条目（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果（公开视图）
     */
    PageResult<TechStackVO> findAllByPage(long current, long size);

    /**
     * 根据 id 查询技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 公开视图；不存在时返回 null
     */
    TechStackVO findById(Long id);

    /**
     * 创建技术栈条目
     *
     * @param dto 技术栈DTO
     * @return 创建后的公开视图
     */
    TechStackVO create(TechStackDto dto);

    /**
     * 更新技术栈条目
     *
     * @param id  技术栈条目 id
     * @param dto 技术栈DTO
     * @return 更新后的公开视图
     */
    TechStackVO update(Long id, TechStackDto dto);

    /**
     * 删除技术栈条目
     *
     * @param id 技术栈条目 id
     */
    void delete(Long id);
}

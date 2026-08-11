package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.TechStackDto;
import com.xdzn.model.entity.TechStackItem;

import java.util.List;

/**
 * TechStackItemService
 * <p>
 * 技术栈服务接口，定义技术栈条目增删改查的契约。
 *
 * @author xdzn
 */
public interface TechStackItemService extends IService<TechStackItem> {

    /**
     * 查询全部技术栈条目（按排序号升序）
     *
     * @return 技术栈条目列表
     */
    List<TechStackItem> findAll();

    /**
     * 分页查询技术栈条目（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResult<TechStackItem> findAllByPage(long current, long size);

    /**
     * 根据 id 查询技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 技术栈条目；不存在时返回 null
     */
    TechStackItem findById(Long id);

    /**
     * 创建技术栈条目
     *
     * @param dto 技术栈DTO
     * @return 创建后的技术栈条目
     */
    TechStackItem create(TechStackDto dto);

    /**
     * 更新技术栈条目
     *
     * @param id  技术栈条目 id
     * @param dto 技术栈DTO
     * @return 更新后的技术栈条目
     */
    TechStackItem update(Long id, TechStackDto dto);

    /**
     * 删除技术栈条目
     *
     * @param id 技术栈条目 id
     */
    void delete(Long id);
}

package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 根据 id 查询技术栈条目
     *
     * @param id 技术栈条目 id
     * @return 技术栈条目；不存在时返回 null
     */
    TechStackItem findById(Long id);

    /**
     * 创建技术栈条目
     *
     * @param item 技术栈条目信息
     * @return 创建后的技术栈条目
     */
    TechStackItem create(TechStackItem item);

    /**
     * 更新技术栈条目
     *
     * @param id   技术栈条目 id
     * @param item 技术栈条目信息
     * @return 更新后的技术栈条目
     */
    TechStackItem update(Long id, TechStackItem item);

    /**
     * 删除技术栈条目
     *
     * @param id 技术栈条目 id
     */
    void delete(Long id);
}

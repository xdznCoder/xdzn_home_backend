package com.xdzn.service;

import com.xdzn.model.dto.ResourceCategoryDto;
import com.xdzn.model.entity.ResourceCategory;

import java.util.List;

/**
 * ResourceCategoryService
 * <p>
 * 资源分类服务接口（分类由队长管理，成员上传时选择）。
 *
 * @author xdzn
 */
public interface ResourceCategoryService {

    /**
     * 查询全部分类（按排序升序）
     *
     * @return 分类列表
     */
    List<ResourceCategory> list();

    /**
     * 新增分类
     *
     * @param dto 分类 DTO
     * @return 创建后的分类
     */
    ResourceCategory create(ResourceCategoryDto dto);

    /**
     * 更新分类
     *
     * @param id  分类 id
     * @param dto 分类 DTO
     * @return 更新后的分类
     */
    ResourceCategory update(Long id, ResourceCategoryDto dto);

    /**
     * 删除分类
     *
     * @param id 分类 id
     */
    void delete(Long id);
}

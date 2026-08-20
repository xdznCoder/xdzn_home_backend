package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.ResourceCategoryMapper;
import com.xdzn.model.dto.ResourceCategoryDto;
import com.xdzn.model.entity.ResourceCategory;
import com.xdzn.service.ResourceCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ResourceCategoryServiceImpl
 * <p>
 * 资源分类服务实现（队长管理分类，分类名唯一）。
 *
 * @author xdzn
 */
@Service
public class ResourceCategoryServiceImpl implements ResourceCategoryService {

    /**
     * 分类表 Mapper
     */
    private final ResourceCategoryMapper categoryMapper;

    /**
     * 构造注入依赖
     *
     * @param categoryMapper 分类表 Mapper
     */
    public ResourceCategoryServiceImpl(ResourceCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 查询全部分类（按排序、id 升序）
     *
     * @return 分类列表
     */
    @Override
    public List<ResourceCategory> list() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<ResourceCategory>()
                        .orderByAsc(ResourceCategory::getSort)
                        .orderByAsc(ResourceCategory::getId));
    }

    /**
     * 新增分类（分类名唯一校验）
     *
     * @param dto 分类 DTO
     * @return 创建后的分类
     */
    @Override
    public ResourceCategory create(ResourceCategoryDto dto) {
        checkNameUnique(dto.getName(), null);
        ResourceCategory category = new ResourceCategory();
        BeanUtils.copyProperties(dto, category);
        categoryMapper.insert(category);
        return categoryMapper.selectById(category.getId());
    }

    /**
     * 更新分类（分类名唯一校验，排除自身）
     *
     * @param id  分类 id
     * @param dto 分类 DTO
     * @return 更新后的分类
     */
    @Override
    public ResourceCategory update(Long id, ResourceCategoryDto dto) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "分类不存在");
        }
        checkNameUnique(dto.getName(), id);
        ResourceCategory category = new ResourceCategory();
        category.setId(id);
        BeanUtils.copyProperties(dto, category);
        categoryMapper.updateById(category);
        return categoryMapper.selectById(id);
    }

    /**
     * 删除分类
     *
     * @param id 分类 id
     */
    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    /**
     * 校验分类名唯一（排除指定 id）
     *
     * @param name 分类名
     * @param excludeId 排除的分类 id（更新时传自身，新增传 null）
     */
    private void checkNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<ResourceCategory> w = new LambdaQueryWrapper<ResourceCategory>()
                .eq(ResourceCategory::getName, name);
        if (excludeId != null) {
            w.ne(ResourceCategory::getId, excludeId);
        }
        Long exist = categoryMapper.selectCount(w);
        if (exist != null && exist > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "分类已存在");
        }
    }
}

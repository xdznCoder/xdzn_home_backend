package com.xdzn.service;

import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ResourceDto;
import com.xdzn.model.vo.ResourceVO;

/**
 * ResourceService
 * <p>
 * 资源分享服务接口。
 * <p>
 * 权限约定（配合 Sa-Token 路由层，读写均需登录）：
 * <ul>
 *     <li>所有登录成员可上传 / 检索 / 查看资源</li>
 *     <li>编辑 / 删除：上传人本人或 captain（服务内校验）</li>
 * </ul>
 *
 * @author xdzn
 */
public interface ResourceService {

    /**
     * 分页检索资源（关键词匹配标题/分类/标签/上传人，可按分类筛选）
     *
     * @param current   当前页码
     * @param size      每页大小
     * @param keyword   关键词（可选）
     * @param categoryId 分类 id（可选）
     * @return 分页结果
     */
    PageResult<ResourceVO> findAllByPage(long current, long size, String keyword, Long categoryId);

    /**
     * 上传资源
     *
     * @param dto 资源 DTO
     * @return 创建后的资源视图
     */
    ResourceVO create(ResourceDto dto);

    /**
     * 更新资源（上传人本人或 captain）
     *
     * @param id  资源 id
     * @param dto 资源 DTO
     * @return 更新后的资源视图
     */
    ResourceVO update(Long id, ResourceDto dto);

    /**
     * 删除资源（上传人本人或 captain）
     *
     * @param id 资源 id
     */
    void delete(Long id);

    /**
     * 根据 id 查询资源详情
     *
     * @param id 资源 id
     * @return 资源视图；不存在时返回 null
     */
    ResourceVO findById(Long id);
}

package com.xdzn.service;

import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.vo.AnnouncementVO;

import java.util.List;

/**
 * AnnouncementService
 * <p>
 * 公告服务接口。
 * <p>
 * 权限约定（配合 Sa-Token 路由层）：
 * <ul>
 *     <li>分页 / 详情：登录成员可读（站内公告展示）</li>
 *     <li>发布 / 编辑 / 删除：仅 captain（全局写操作规则）</li>
 * </ul>
 *
 * @author xdzn
 */
public interface AnnouncementService {

    /**
     * 分页查询公告（按创建时间倒序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResult<AnnouncementVO> findAllByPage(long current, long size);

    /**
     * 根据 id 查询公告详情（含目标群）
     *
     * @param id 公告 id
     * @return 公告视图；不存在时返回 null
     */
    AnnouncementVO findById(Long id);

    /**
     * 发布公告（存公告 + 关联目标群 + 站外推送：QQ 群 / 成员邮箱）
     *
     * @param dto 公告 DTO
     * @return 创建后的公告视图
     */
    AnnouncementVO create(AnnouncementDto dto);

    /**
     * 更新公告并重设目标群（不重复站外推送）
     *
     * @param id  公告 id
     * @param dto 公告 DTO
     * @return 更新后的公告视图
     */
    AnnouncementVO update(Long id, AnnouncementDto dto);

    /**
     * 删除公告及其目标群关联
     *
     * @param id 公告 id
     */
    void delete(Long id);

    /**
     * 切换公告置顶状态
     *
     * @param id    公告 id
     * @param isTop 是否置顶(1 置顶 / 0 普通)
     */
    void toggleTop(Long id, Integer isTop);

    /**
     * 切换公告发布状态
     *
     * @param id     公告 id
     * @param status 新状态(draft/published/archived)
     */
    void updateStatus(Long id, String status);

    /**
     * 分页查询公告(支持按状态筛选)
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param status  状态筛选(可选)
     * @return 分页结果
     */
    PageResult<AnnouncementVO> findAllByPageWithStatus(long current, long size, String status);

    /**
     * 批量删除公告
     *
     * @param ids 公告 id 列表
     */
    void deleteBatch(List<Long> ids);
}

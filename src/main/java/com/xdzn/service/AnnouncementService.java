package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Announcement;
import com.xdzn.model.vo.AnnouncementVO;

import java.util.List;

/**
 * AnnouncementService
 * <p>
 * 公告服务接口，定义公告增删改查的契约。
 *
 * @author xdzn
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告（支持状态筛选，置顶排序）
     *
     * @param page 当前页码
     * @param size 每页大小
     * @param status 状态筛选(可选)
     * @return 分页结果
     */
    PageResult<AnnouncementVO> findAllByPage(int page, int size, String status);

    /**
     * 根据id查询公告
     *
     * @param id 公告id
     * @return 公告信息
     */
    AnnouncementVO findById(Long id);

    /**
     * 创建公告
     *
     * @param dto 公告DTO
     * @param userId 操作用户ID
     * @return 创建后的公告
     */
    Announcement create(AnnouncementDto dto, Long userId);

    /**
     * 更新公告
     *
     * @param id 公告id
     * @param dto 公告DTO
     */
    void update(Long id, AnnouncementDto dto);

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    void delete(Long id);

    /**
     * 获取已发布的公告列表（公开接口）
     *
     * @return 已发布公告列表
     */
    List<AnnouncementVO> findPublished();
}

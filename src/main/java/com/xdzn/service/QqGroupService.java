package com.xdzn.service;

import com.xdzn.model.dto.QqGroupDto;
import com.xdzn.model.entity.QqGroup;

import java.util.List;

/**
 * QqGroupService
 * <p>
 * QQ 群配置服务接口（群条目由队长管理，公告发布时选择目标群）。
 *
 * @author xdzn
 */
public interface QqGroupService {

    /**
     * 查询全部 QQ 群（按 id 升序）
     *
     * @return QQ 群列表
     */
    List<QqGroup> list();

    /**
     * 新增 QQ 群
     *
     * @param dto 群配置 DTO
     * @return 创建后的群
     */
    QqGroup create(QqGroupDto dto);

    /**
     * 更新 QQ 群
     *
     * @param id  群 id
     * @param dto 群配置 DTO
     * @return 更新后的群
     */
    QqGroup update(Long id, QqGroupDto dto);

    /**
     * 删除 QQ 群
     *
     * @param id 群 id
     */
    void delete(Long id);
}

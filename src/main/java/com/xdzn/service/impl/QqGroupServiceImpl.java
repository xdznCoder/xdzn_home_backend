package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.QqGroupMapper;
import com.xdzn.model.dto.QqGroupDto;
import com.xdzn.model.entity.QqGroup;
import com.xdzn.service.QqGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * QqGroupServiceImpl
 * <p>
 * QQ 群配置服务实现（群号唯一校验）。
 *
 * @author xdzn
 */
@Service
public class QqGroupServiceImpl extends ServiceImpl<QqGroupMapper, QqGroup>
        implements QqGroupService {

    /**
     * 查询全部 QQ 群（按 id 升序）
     *
     * @return QQ 群列表
     */
    @Override
    public List<QqGroup> list() {
        return lambdaQuery().orderByAsc(QqGroup::getId).list();
    }

    /**
     * 新增 QQ 群（群号唯一校验）
     *
     * @param dto 群配置 DTO
     * @return 创建后的群
     */
    @Override
    public QqGroup create(QqGroupDto dto) {
        checkGroupNoUnique(dto.getGroupNo(), null);
        QqGroup group = new QqGroup();
        BeanUtils.copyProperties(dto, group);
        if (group.getEnabled() == null) {
            group.setEnabled(1);
        }
        save(group);
        return getById(group.getId());
    }

    /**
     * 更新 QQ 群（群号唯一校验，排除自身）
     *
     * @param id  群 id
     * @param dto 群配置 DTO
     * @return 更新后的群
     */
    @Override
    public QqGroup update(Long id, QqGroupDto dto) {
        if (getById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "QQ 群不存在");
        }
        checkGroupNoUnique(dto.getGroupNo(), id);
        QqGroup group = new QqGroup();
        group.setId(id);
        BeanUtils.copyProperties(dto, group);
        updateById(group);
        return getById(id);
    }

    /**
     * 删除 QQ 群
     *
     * @param id 群 id
     */
    @Override
    public void delete(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "QQ 群不存在");
        }
        removeById(id);
    }

    /**
     * 校验 QQ 群号唯一（排除指定 id）
     *
     * @param groupNo   群号
     * @param excludeId 排除的群 id（更新时传自身，新增传 null）
     */
    private void checkGroupNoUnique(String groupNo, Long excludeId) {
        LambdaQueryWrapper<QqGroup> w = new LambdaQueryWrapper<QqGroup>().eq(QqGroup::getGroupNo, groupNo);
        if (excludeId != null) {
            w.ne(QqGroup::getId, excludeId);
        }
        Long exist = count(w);
        if (exist != null && exist > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该 QQ 群号已存在");
        }
    }
}

package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.AnnouncementMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Announcement;
import com.xdzn.model.entity.User;
import com.xdzn.model.vo.AnnouncementVO;
import com.xdzn.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AnnouncementServiceImpl
 * <p>
 * 公告服务实现，提供公告增删改查。
 * 列表查询通过 Spring Cache 缓存，写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    private final UserMapper userMapper;

    public AnnouncementServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Cacheable(value = "announcements", key = "'page:' + #page + ':' + #size + ':' + (#status == null ? 'all' : #status)", unless = "#result == null || #result.getRecords().isEmpty()")
    public PageResult<AnnouncementVO> findAllByPage(int page, int size, String status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishedAt);
        
        Page<Announcement> p = new Page<>(page, size);
        Page<Announcement> result = page(p, wrapper);
        
        List<AnnouncementVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), records);
    }

    @Override
    public AnnouncementVO findById(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            return null;
        }
        return convertToVO(announcement);
    }

    @Override
    @CacheEvict(value = "announcements", allEntries = true)
    public Announcement create(AnnouncementDto dto, Long userId) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(dto, announcement);
        announcement.setCreatedBy(userId);
        if (dto.getIsTop() != null && dto.getIsTop() == 1) {
            announcement.setIsTop(1);
        }
        if ("published".equals(dto.getStatus())) {
            announcement.setPublishedAt(LocalDateTime.now());
        }
        save(announcement);
        return announcement;
    }

    @Override
    @CacheEvict(value = "announcements", allEntries = true)
    public void update(Long id, AnnouncementDto dto) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "公告不存在");
        }
        BeanUtils.copyProperties(dto, announcement, "id");
        if ("published".equals(dto.getStatus()) && announcement.getPublishedAt() == null) {
            announcement.setPublishedAt(LocalDateTime.now());
        }
        updateById(announcement);
    }

    @Override
    @CacheEvict(value = "announcements", allEntries = true)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Cacheable(value = "announcements", key = "'published'", unless = "#result == null || #result.isEmpty()")
    public List<AnnouncementVO> findPublished() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, "published")
               .orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishedAt);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为VO
     */
    private AnnouncementVO convertToVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, vo);
        
        // 查询创建人姓名
        if (announcement.getCreatedBy() != null) {
            User user = userMapper.selectById(announcement.getCreatedBy());
            if (user != null) {
                vo.setCreatorName(user.getName());
            }
        }
        
        return vo;
    }
}

package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.AnnouncementMapper;
import com.xdzn.mapper.AnnouncementTargetMapper;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.mapper.QqGroupMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.AnnouncementDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Announcement;
import com.xdzn.model.entity.AnnouncementTarget;
import com.xdzn.model.entity.Member;
import com.xdzn.model.entity.QqGroup;
import com.xdzn.model.entity.User;
import com.xdzn.model.vo.AnnouncementVO;
import com.xdzn.service.AnnouncementService;
import com.xdzn.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AnnouncementServiceImpl
 * <p>
 * 公告服务实现。
 * <p>
 * 发布公告流程：存公告 → 关联目标 QQ 群（支持多群，实现「区分针对不同群发布」）
 * → 站外推送（每个目标群经 {@link NoticeService} 发 QQ 消息；勾选时批量发成员邮箱）。
 * 更新/删除仅维护公告与关联，不重复站外推送。
 *
 * @author xdzn
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    /**
     * 公告表 Mapper
     */
    private final AnnouncementMapper announcementMapper;

    /**
     * 公告-目标群关联表 Mapper
     */
    private final AnnouncementTargetMapper targetMapper;

    /**
     * QQ 群表 Mapper
     */
    private final QqGroupMapper qqGroupMapper;

    /**
     * 用户表 Mapper（查发布人姓名）
     */
    private final UserMapper userMapper;

    /**
     * 成员表 Mapper（查成员邮箱）
     */
    private final MemberMapper memberMapper;

    /**
     * 站外通知发送服务
     */
    private final NoticeService noticeService;

    /**
     * 构造注入依赖
     */
    public AnnouncementServiceImpl(AnnouncementMapper announcementMapper,
                                   AnnouncementTargetMapper targetMapper,
                                   QqGroupMapper qqGroupMapper,
                                   UserMapper userMapper,
                                   MemberMapper memberMapper,
                                   NoticeService noticeService) {
        this.announcementMapper = announcementMapper;
        this.targetMapper = targetMapper;
        this.qqGroupMapper = qqGroupMapper;
        this.userMapper = userMapper;
        this.memberMapper = memberMapper;
        this.noticeService = noticeService;
    }

    /**
     * 分页查询公告（按创建时间倒序）
     */
    @Override
    public PageResult<AnnouncementVO> findAllByPage(long current, long size) {
        Page<Announcement> page = announcementMapper.selectPage(
                new Page<>(current, size),
                new LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getIsTop)
                        .orderByDesc(Announcement::getCreatedAt));
        List<AnnouncementVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), voList);
    }

    /**
     * 根据 id 查询公告详情
     */
    @Override
    public AnnouncementVO findById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        return announcement == null ? null : toVO(announcement);
    }

    /**
     * 发布公告：存公告 → 关联目标群 → 站外推送（QQ 群 + 可选邮箱）
     */
    @Override
    @Transactional
    public AnnouncementVO create(AnnouncementDto dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : "published");
        announcement.setAuthorId(StpUtil.getLoginIdAsLong());
        announcement.setSendEmail(Boolean.TRUE.equals(dto.getSendEmail()) ? 1 : 0);
        announcementMapper.insert(announcement);

        syncTargets(announcement.getId(), dto.getTargetGroupIds());
        pushExternal(dto, dto.getTargetGroupIds(), announcement.getSendEmail());
        return toVO(announcementMapper.selectById(announcement.getId()));
    }

    /**
     * 更新公告并重设目标群（不重复站外推送）
     */
    @Override
    @Transactional
    public AnnouncementVO update(Long id, AnnouncementDto dto) {
        if (announcementMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "公告不存在");
        }
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        // updateById 忽略 null 字段：isTop/status 传 null 时保持原值
        announcement.setIsTop(dto.getIsTop());
        announcement.setStatus(dto.getStatus());
        announcement.setSendEmail(Boolean.TRUE.equals(dto.getSendEmail()) ? 1 : 0);
        announcementMapper.updateById(announcement);
        syncTargets(id, dto.getTargetGroupIds());
        return toVO(announcementMapper.selectById(id));
    }

    /**
     * 删除公告及其目标群关联
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (announcementMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "公告不存在");
        }
        targetMapper.delete(new LambdaQueryWrapper<AnnouncementTarget>()
                .eq(AnnouncementTarget::getAnnouncementId, id));
        announcementMapper.deleteById(id);
    }

    // ── 内部方法 ──────────────────────

    /**
     * 同步公告的目标群关联（先删旧再插新）
     *
     * @param announcementId 公告 id
     * @param groupIds       目标群 id 列表
     */
    private void syncTargets(Long announcementId, List<Long> groupIds) {
        targetMapper.delete(new LambdaQueryWrapper<AnnouncementTarget>()
                .eq(AnnouncementTarget::getAnnouncementId, announcementId));
        if (groupIds != null) {
            for (Long groupId : groupIds) {
                AnnouncementTarget target = new AnnouncementTarget();
                target.setAnnouncementId(announcementId);
                target.setQqGroupId(groupId);
                targetMapper.insert(target);
            }
        }
    }

    /**
     * 站外推送：向每个目标 QQ 群发消息；勾选时批量发成员邮箱
     *
     * @param dto       公告 DTO
     * @param groupIds  目标群 id 列表
     * @param sendEmail 是否发邮箱
     */
    private void pushExternal(AnnouncementDto dto, List<Long> groupIds, Integer sendEmail) {
        if (groupIds != null) {
            for (Long groupId : groupIds) {
                QqGroup group = qqGroupMapper.selectById(groupId);
                if (group != null) {
                    noticeService.sendQqGroupMessage(group, dto.getTitle(), dto.getContent());
                }
            }
        }
        if (sendEmail != null && sendEmail == 1) {
            List<Member> members = memberMapper.selectList(
                    new LambdaQueryWrapper<Member>()
                            .isNotNull(Member::getEmailContact)
                            .ne(Member::getEmailContact, ""));
            for (Member member : members) {
                noticeService.sendEmail(member.getEmailContact(), dto.getTitle(), dto.getContent());
            }
        }
    }

    /**
     * 公告实体转视图（补发布人姓名、目标群列表）
     *
     * @param announcement 公告实体
     * @return 公告视图
     */
    private AnnouncementVO toVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, vo);
        User author = userMapper.selectById(announcement.getAuthorId());
        vo.setAuthorName(author != null ? author.getName() : null);
        List<Long> groupIds = targetMapper.selectList(
                        new LambdaQueryWrapper<AnnouncementTarget>()
                                .eq(AnnouncementTarget::getAnnouncementId, announcement.getId()))
                .stream().map(AnnouncementTarget::getQqGroupId).toList();
        vo.setTargetGroups(groupIds.isEmpty()
                ? List.of()
                : qqGroupMapper.selectBatchIds(groupIds));
        return vo;
    }
}

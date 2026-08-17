package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.model.dto.MemberDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Member;
import com.xdzn.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * MemberServiceImpl
 * <p>
 * 团队成员服务实现，提供成员增删改查。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code members}，TTL 10 分钟），
 * 写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
        implements MemberService {

    /**
     * 查询全部成员（按排序号升序），结果缓存 10 分钟
     *
     * @return 成员列表
     */
    @Override
    @Cacheable(value = "members", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<Member> findAll() {
        return lambdaQuery().orderByAsc(Member::getOrder).list();
    }

    /**
     * 分页查询成员（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<Member> findAllByPage(long current, long size) {
        Page<Member> page = new Page<>(current, size);
        lambdaUpdate().orderByAsc(Member::getOrder);
        Page<Member> result = page(page, new LambdaQueryWrapper<Member>().orderByAsc(Member::getOrder));
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    /**
     * 根据 id 查询成员
     *
     * @param id 成员 id
     * @return 成员信息；不存在时返回 null
     */
    @Override
    public Member findById(Long id) {
        return getById(id);
    }

    /**
     * 创建成员，并失效成员列表缓存
     *
     * @param dto 成员DTO
     * @return 创建后的成员
     */
    @Override
    @CacheEvict(value = "members", key = "'all'")
    public Member create(MemberDto dto) {
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        save(member);
        return member;
    }

    /**
     * 更新成员，并失效成员列表缓存
     *
     * @param id  成员 id
     * @param dto 成员DTO
     * @return 更新后的成员
     */
    @Override
    @CacheEvict(value = "members", key = "'all'")
    public Member update(Long id, MemberDto dto) {
        Member member = new Member();
        member.setId(id);
        BeanUtils.copyProperties(dto, member);
        updateById(member);
        return getById(id);
    }

    /**
     * 删除成员，并失效成员列表缓存
     *
     * @param id 成员 id
     */
    @Override
    @CacheEvict(value = "members", key = "'all'")
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public Member getMemberByUserId(Long userId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    @CacheEvict(value = "members", key = "'all'")
    public Member updateMemberByUserId(Long userId, MemberDto dto) {
        Member member = getMemberByUserId(userId);
        if (member == null) {
            Member newMember = new Member();
            BeanUtils.copyProperties(dto, newMember);
            newMember.setUserId(userId);
            save(newMember);
            return newMember;
        }
        
        if (dto.getName() != null) member.setName(dto.getName());
        if (dto.getAvatar() != null) member.setAvatar(dto.getAvatar());
        if (dto.getDirection() != null) member.setDirection(dto.getDirection());
        if (dto.getGraduationYear() != null) member.setGraduationYear(dto.getGraduationYear());
        if (dto.getCurrentCompany() != null) member.setCurrentCompany(dto.getCurrentCompany());
        if (dto.getCurrentRole() != null) member.setCurrentRole(dto.getCurrentRole());
        if (dto.getPhone() != null) member.setPhone(dto.getPhone());
        if (dto.getEmailContact() != null) member.setEmailContact(dto.getEmailContact());
        if (dto.getSkills() != null) member.setSkills(dto.getSkills());
        if (dto.getBio() != null) member.setBio(dto.getBio());
        
        updateById(member);
        return member;
    }
}

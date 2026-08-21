package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.MemberDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Member;
import com.xdzn.model.entity.User;
import com.xdzn.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import com.xdzn.redis.RedisService;
import com.xdzn.redis.key.CacheRedisKey;
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
     * 用户表 Mapper（创建成员登录账号用）
     */
    private final UserMapper userMapper;

    /**
     * 统一 Redis 缓存服务
     */
    private final RedisService redisService;

    /**
     * 密码加密器（创建登录账号时用 BCrypt 加密密码）
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 构造注入依赖（baseMapper 由 MyBatis-Plus 自动注入）
     *
     * @param userMapper   用户表 Mapper
     * @param redisService 统一 Redis 缓存服务
     */
    public MemberServiceImpl(UserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 查询全部成员（按排序号升序），结果缓存 10 分钟
     *
     * @return 成员列表
     */
    @Override
    public List<Member> findAll() {
        return redisService.getOrSetList(CacheRedisKey.MEMBERS, "all", Member.class, () -> lambdaQuery().orderByAsc(Member::getOrder).list());
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
        return redisService.getOrSet(CacheRedisKey.MEMBERS_DETAIL, String.valueOf(id), Member.class, () -> getById(id));
    }

    /**
     * 创建成员，并失效成员列表缓存
     *
     * @param dto 成员DTO
     * @return 创建后的成员
     */
    @Override
    @Transactional
    public Member create(MemberDto dto) {
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);

        // 可选：同时为成员创建登录账号（role=member），并将 users.id 关联到 member.user_id
        if (Boolean.TRUE.equals(dto.getCreateAccount())) {
            createAccountForMember(dto, member);
        }

        save(member);
        redisService.delete(CacheRedisKey.MEMBERS, "all");
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
    public Member update(Long id, MemberDto dto) {
        Member member = new Member();
        member.setId(id);
        BeanUtils.copyProperties(dto, member);
        updateById(member);
        redisService.delete(CacheRedisKey.MEMBERS, "all");
        redisService.delete(CacheRedisKey.MEMBERS_DETAIL, String.valueOf(id));
        return getById(id);
    }

    /**
     * 删除成员，并失效成员列表缓存
     *
     * @param id 成员 id
     */
    @Override
    public void delete(Long id) {
        removeById(id);
        redisService.delete(CacheRedisKey.MEMBERS, "all");
        redisService.delete(CacheRedisKey.MEMBERS_DETAIL, String.valueOf(id));
    }

    @Override
    public Member getMemberByUserId(Long userId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public Member updateMemberByUserId(Long userId, MemberDto dto) {
        Member member = getMemberByUserId(userId);
        if (member == null) {
            Member newMember = new Member();
            BeanUtils.copyProperties(dto, newMember);
            newMember.setUserId(userId);
            save(newMember);
            redisService.delete(CacheRedisKey.MEMBERS, "all");
            return newMember;
        }
        
        if (dto.getName() != null) member.setName(dto.getName());
        if (dto.getAvatar() != null) member.setAvatar(dto.getAvatar());
        if (dto.getDirection() != null) member.setDirection(dto.getDirection());
        if (dto.getGraduationYear() != null) member.setGraduationYear(dto.getGraduationYear());
        if (dto.getGrade() != null) member.setGrade(dto.getGrade());
        if (dto.getStudentNo() != null) member.setStudentNo(dto.getStudentNo());
        if (dto.getMajor() != null) member.setMajor(dto.getMajor());
        if (dto.getTeamRole() != null) member.setTeamRole(dto.getTeamRole());
        if (dto.getInternship() != null) member.setInternship(dto.getInternship());
        if (dto.getAwards() != null) member.setAwards(dto.getAwards());
        if (dto.getPhone() != null) member.setPhone(dto.getPhone());
        if (dto.getEmailContact() != null) member.setEmailContact(dto.getEmailContact());
        if (dto.getSkills() != null) member.setSkills(dto.getSkills());
        if (dto.getBio() != null) member.setBio(dto.getBio());
        
        updateById(member);
        redisService.delete(CacheRedisKey.MEMBERS, "all");
        redisService.delete(CacheRedisKey.MEMBERS_DETAIL, String.valueOf(member.getId()));
        return member;
    }

    /**
     * 设置成员登录账号身份（member 普通成员 / alumni 已毕业成员）
     * 通过该成员的 user_id 找到登录账号并更新 role。
     *
     * @param id   成员 id
     * @param role 目标身份（仅允许 member/alumni，不允许设为 captain 避免越权）
     */
    @Override
    public void setMemberRole(Long id, String role) {
        if (role == null || !(role.equals("member") || role.equals("alumni"))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "身份仅允许 member(普通成员)/alumni(已毕业成员)");
        }
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "成员不存在");
        }
        if (member.getUserId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该成员未关联登录账号，无法设置身份");
        }
        User user = userMapper.selectById(member.getUserId());
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "登录账号不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
    }

    /**
     * 查询用户角色（用于成员列表展示身份）
     *
     * @param userId 用户 id
     * @return 角色；用户不存在或 userId 为空返回 null
     */
    @Override
    public String getUserRole(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userMapper.selectById(userId);
        return user == null ? null : user.getRole();
    }

    /**
     * 为成员创建登录账号：邮箱查重 → 建 users（role=member，密码 BCrypt 加密）→ 关联 member.user_id
     *
     * @param dto    成员 DTO（含 accountEmail/accountPassword/createAccount）
     * @param member 待保存的成员实体（会设置 userId）
     */
    private void createAccountForMember(MemberDto dto, Member member) {
        if (dto.getAccountEmail() == null || dto.getAccountEmail().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "创建登录账号需填写邮箱");
        }
        if (dto.getAccountPassword() == null || dto.getAccountPassword().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "创建登录账号需填写密码");
        }
        Long exist = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getAccountEmail()));
        if (exist != null && exist > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该邮箱已被注册");
        }
        User user = new User();
        user.setEmail(dto.getAccountEmail());
        user.setPassword(passwordEncoder.encode(dto.getAccountPassword()));
        user.setName(dto.getName());
        user.setRole("member");
        userMapper.insert(user);
        member.setUserId(user.getId());
    }
}

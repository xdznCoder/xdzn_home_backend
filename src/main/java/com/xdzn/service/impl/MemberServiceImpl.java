package com.xdzn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.MemberMapper;
import com.xdzn.model.entity.Member;
import com.xdzn.service.MemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @param member 成员信息
     * @return 创建后的成员
     */
    @Override
    @CacheEvict(value = "members", key = "'all'")
    public Member create(Member member) {
        save(member);
        return member;
    }

    /**
     * 更新成员，并失效成员列表缓存
     *
     * @param id     成员 id
     * @param member 成员信息
     * @return 更新后的成员
     */
    @Override
    @CacheEvict(value = "members", key = "'all'")
    public Member update(Long id, Member member) {
        member.setId(id);
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
}

package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.entity.Member;

import java.util.List;

/**
 * MemberService
 * <p>
 * 团队成员服务接口，定义成员增删改查的契约。
 *
 * @author xdzn
 */
public interface MemberService extends IService<Member> {

    /**
     * 查询全部成员（按排序号升序）
     *
     * @return 成员列表
     */
    List<Member> findAll();

    /**
     * 根据 id 查询成员
     *
     * @param id 成员 id
     * @return 成员信息；不存在时返回 null
     */
    Member findById(Long id);

    /**
     * 创建成员
     *
     * @param member 成员信息
     * @return 创建后的成员
     */
    Member create(Member member);

    /**
     * 更新成员
     *
     * @param id     成员 id
     * @param member 成员信息
     * @return 更新后的成员
     */
    Member update(Long id, Member member);

    /**
     * 删除成员
     *
     * @param id 成员 id
     */
    void delete(Long id);
}

package com.xdzn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.MemberDto;
import com.xdzn.model.dto.PageResult;
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
     * 分页查询成员（按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResult<Member> findAllByPage(long current, long size);

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
     * @param dto 成员DTO
     * @return 创建后的成员
     */
    Member create(MemberDto dto);

    /**
     * 更新成员
     *
     * @param id  成员 id
     * @param dto 成员DTO
     * @return 更新后的成员
     */
    Member update(Long id, MemberDto dto);

    /**
     * 删除成员
     *
     * @param id 成员 id
     */
    void delete(Long id);

    /**
     * 根据用户ID查询成员信息
     *
     * @param userId 用户ID
     * @return 成员信息
     */
    Member getMemberByUserId(Long userId);

    /**
     * 更新当前登录用户的成员信息
     *
     * @param userId 用户ID
     * @param dto 更新数据
     * @return 更新后的成员
     */
    Member updateMemberByUserId(Long userId, MemberDto dto);

    /**
     * 设置成员登录账号身份（member 普通成员 / alumni 已毕业成员），仅 captain 可调用
     *
     * @param id   成员 id
     * @param role 目标身份
     */
    void setMemberRole(Long id, String role);

    /**
     * 查询用户角色（用于成员列表展示身份）
     *
     * @param userId 用户 id
     * @return 角色；用户不存在或 userId 为空返回 null
     */
    String getUserRole(Long userId);
}

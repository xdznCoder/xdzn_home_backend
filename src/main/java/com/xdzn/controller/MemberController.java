package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.entity.Member;
import com.xdzn.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MemberController
 * <p>
 * 团队成员相关接口：成员列表、详情、增删改。
 *
 * @author xdzn
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    /**
     * 成员服务
     */
    private final MemberService memberService;

    /**
     * 构造注入成员服务
     *
     * @param memberService 成员服务
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 查询全部成员
     *
     * @return 成员列表
     */
    @GetMapping
    public Result<List<Member>> findAll() {
        return Result.ok(memberService.findAll());
    }

    /**
     * 根据 id 查询成员详情
     *
     * @param id 成员 id
     * @return 成员信息；不存在时返回 404
     */
    @GetMapping("/{id}")
    public Result<Member> findById(@PathVariable Long id) {
        Member member = memberService.findById(id);
        if (member == null) return Result.notFound();
        return Result.ok(member);
    }

    /**
     * 创建成员
     *
     * @param member 成员信息
     * @return 创建后的成员
     */
    @PostMapping
    public Result<Member> create(@RequestBody Member member) {
        return Result.ok(memberService.create(member));
    }

    /**
     * 更新成员
     *
     * @param id     成员 id
     * @param member 成员信息
     * @return 更新后的成员
     */
    @PutMapping("/{id}")
    public Result<Member> update(@PathVariable Long id, @RequestBody Member member) {
        return Result.ok(memberService.update(id, member));
    }

    /**
     * 删除成员
     *
     * @param id 成员 id
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Result.ok();
    }
}

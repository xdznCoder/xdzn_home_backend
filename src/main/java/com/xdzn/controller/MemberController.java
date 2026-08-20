package com.xdzn.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xdzn.common.Result;
import com.xdzn.model.dto.MemberDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.Member;
import com.xdzn.model.vo.MemberPublicVO;
import com.xdzn.model.vo.MemberVO;
import com.xdzn.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MemberController
 * <p>
 * 团队成员相关接口：成员列表、详情、增删改。
 *
 * @author xdzn
 */
@Tag(name = "成员接口", description = "官网团队成员展示与管理的增删改查（写操作需 admin 权限）")
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
     * 查询全部成员（公开视角）
     *
     * @return 成员列表（脱敏）
     */
    @Operation(summary = "查询全部成员", description = "返回全部团队成员（按排序号升序），结果经 Spring Cache 缓存 10 分钟")
    @GetMapping
    public Result<List<MemberPublicVO>> findAll() {
        List<Member> members = memberService.findAll();
        List<MemberPublicVO> voList = members.stream().map(m -> {
            MemberPublicVO vo = new MemberPublicVO();
            BeanUtils.copyProperties(m, vo);
            vo.setRole(memberService.getUserRole(m.getUserId()));
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(voList);
    }

    /**
     * 分页查询成员（公开视角）
     *
     * @param current 当前页码，默认 1
     * @param size    每页大小，默认 10
     * @return 分页结果（脱敏）
     */
    @Operation(summary = "分页查询成员", description = "分页查询团队成员（按排序号升序）")
    @GetMapping("/page")
    public Result<PageResult<MemberPublicVO>> findAllByPage(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") long size) {
        PageResult<Member> pageMember = memberService.findAllByPage(current, size);
        List<MemberPublicVO> voList = pageMember.getRecords().stream().map(m -> {
            MemberPublicVO vo = new MemberPublicVO();
            BeanUtils.copyProperties(m, vo);
            vo.setRole(memberService.getUserRole(m.getUserId()));
            return vo;
        }).collect(Collectors.toList());
        long pages = pageMember.getTotal() / size + (pageMember.getTotal() % size == 0 ? 0 : 1);
        return Result.ok(new PageResult<>(current, size, pageMember.getTotal(), pages, voList));
    }

    /**
     * 根据 id 查询成员详情
     *
     * @param id 成员 id
     * @return 成员信息；不存在时返回 404
     */
    @Operation(summary = "查询成员详情", description = "根据 id 查询单个成员")
    @GetMapping("/{id}")
    public Result<MemberPublicVO> findById(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable Long id) {
        Member member = memberService.findById(id);
        if (member == null) return Result.notFound();
        MemberPublicVO vo = new MemberPublicVO();
        BeanUtils.copyProperties(member, vo);
        vo.setRole(memberService.getUserRole(member.getUserId()));
        return Result.ok(vo);
    }

    /**
     * 创建成员
     *
     * @param dto 成员DTO
     * @return 创建后的成员（公开视角）
     */
    @Operation(summary = "创建成员", description = "新增团队成员，需 admin 权限")
    @PostMapping
    public Result<MemberPublicVO> create(@Valid @RequestBody MemberDto dto) {
        Member member = memberService.create(dto);
        MemberPublicVO vo = new MemberPublicVO();
        BeanUtils.copyProperties(member, vo);
        return Result.ok(vo);
    }

    /**
     * 更新成员
     *
     * @param id  成员 id
     * @param dto 成员DTO
     * @return 更新后的成员（公开视角）
     */
    @Operation(summary = "更新成员", description = "按 id 更新成员信息，需 admin 权限")
    @PutMapping("/{id}")
    public Result<MemberPublicVO> update(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MemberDto dto) {
        Member member = memberService.update(id, dto);
        MemberPublicVO vo = new MemberPublicVO();
        BeanUtils.copyProperties(member, vo);
        return Result.ok(vo);
    }

    /**
     * 删除成员
     *
     * @param id 成员 id
     * @return 操作结果
     */
    @Operation(summary = "删除成员", description = "删除成员，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable Long id) {
        memberService.delete(id);
        return Result.ok();
    }

    /**
     * 设置成员登录账号身份（member 普通成员 / alumni 已毕业成员），仅 captain 可调用
     *
     * @param id   成员 id
     * @param body 请求体：{ "role": "member" | "alumni" }
     * @return 操作结果
     */
    @Operation(summary = "设置成员身份", description = "设置成员登录账号身份（member 普通/alumni 已毕业），需 captain 权限")
    @PutMapping("/{id}/role")
    public Result<Void> setMemberRole(
            @Parameter(description = "成员 id", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        memberService.setMemberRole(id, body.get("role"));
        return Result.ok();
    }

    /**
     * 获取当前登录用户的成员信息
     *
     * @return 成员VO
     */
    @Operation(summary = "获取当前用户的成员信息", description = "根据登录用户的userId返回对应的成员信息")
    @GetMapping("/me")
    public Result<MemberVO> getCurrentMember() {
        Long userId = StpUtil.getLoginIdAsLong();
        Member member = memberService.getMemberByUserId(userId);
        if (member == null) return Result.notFound();
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);
        return Result.ok(vo);
    }

    /**
     * 更新当前登录用户的成员信息
     *
     * @param dto 成员DTO
     * @return 更新后的成员VO
     */
    @Operation(summary = "更新当前用户的成员信息", description = "更新对应的成员信息(部分更新)")
    @PutMapping("/me")
    public Result<MemberVO> updateCurrentMember(@Valid @RequestBody MemberDto dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        Member member = memberService.updateMemberByUserId(userId, dto);
        if (member == null) return Result.notFound();
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);
        return Result.ok(vo);
    }
}

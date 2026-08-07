package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.entity.Testimonial;
import com.xdzn.service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TestimonialController
 * <p>
 * 用户评价相关接口：评价列表、详情、增删改。
 *
 * @author xdzn
 */
@Tag(name = "用户评价接口", description = "官网用户评价展示与管理的增删改查（写操作需 admin 权限）")
@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    /**
     * 评价服务
     */
    private final TestimonialService testimonialService;

    /**
     * 构造注入评价服务
     *
     * @param testimonialService 评价服务
     */
    public TestimonialController(TestimonialService testimonialService) {
        this.testimonialService = testimonialService;
    }

    /**
     * 查询全部评价
     *
     * @return 评价列表
     */
    @Operation(summary = "查询全部评价", description = "返回全部用户评价（按排序号升序），结果经 Spring Cache 缓存 10 分钟")
    @GetMapping
    public Result<List<Testimonial>> findAll() {
        return Result.ok(testimonialService.findAll());
    }

    /**
     * 根据 id 查询评价详情
     *
     * @param id 评价 id
     * @return 评价信息；不存在时返回 404
     */
    @Operation(summary = "查询评价详情", description = "根据 id 查询单个用户评价")
    @GetMapping("/{id}")
    public Result<Testimonial> findById(
            @Parameter(description = "评价 id", required = true, example = "1")
            @PathVariable Long id) {
        Testimonial t = testimonialService.findById(id);
        if (t == null) return Result.notFound();
        return Result.ok(t);
    }

    /**
     * 创建评价
     *
     * @param testimonial 评价信息
     * @return 创建后的评价
     */
    @Operation(summary = "创建评价", description = "新增用户评价，需 admin 权限")
    @PostMapping
    public Result<Testimonial> create(@RequestBody Testimonial testimonial) {
        return Result.ok(testimonialService.create(testimonial));
    }

    /**
     * 更新评价
     *
     * @param id          评价 id
     * @param testimonial 评价信息
     * @return 更新后的评价
     */
    @Operation(summary = "更新评价", description = "按 id 更新用户评价，需 admin 权限")
    @PutMapping("/{id}")
    public Result<Testimonial> update(
            @Parameter(description = "评价 id", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Testimonial testimonial) {
        return Result.ok(testimonialService.update(id, testimonial));
    }

    /**
     * 删除评价
     *
     * @param id 评价 id
     * @return 操作结果
     */
    @Operation(summary = "删除评价", description = "删除用户评价，需 admin 权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "评价 id", required = true, example = "1")
            @PathVariable Long id) {
        testimonialService.delete(id);
        return Result.ok();
    }
}

package com.xdzn.controller;

import com.xdzn.common.Result;
import com.xdzn.model.dto.ProjectVO;
import com.xdzn.model.entity.Project;
import com.xdzn.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ProjectController
 * <p>
 * 项目相关接口：项目列表、详情、增删改。
 * 创建/更新时支持携带技术栈 id 列表（techStackIds 或 tech_stack_ids），
 * 用于同步项目与技术栈的多对多关联关系。
 *
 * @author xdzn
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    /**
     * 项目服务
     */
    private final ProjectService projectService;

    /**
     * 构造注入项目服务
     *
     * @param projectService 项目服务
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 查询全部项目（含技术栈信息）
     *
     * @return 项目视图对象列表
     */
    @GetMapping
    public Result<List<ProjectVO>> findAll() {
        return Result.ok(projectService.findAll());
    }

    /**
     * 根据 id 查询项目详情
     *
     * @param id 项目 id
     * @return 项目视图对象；不存在时返回 404
     */
    @GetMapping("/{id}")
    public Result<ProjectVO> findById(@PathVariable Long id) {
        ProjectVO vo = projectService.findById(id);
        if (vo == null) return Result.notFound();
        return Result.ok(vo);
    }

    /**
     * 创建项目
     *
     * @param body 请求体，含项目字段与技术栈 id 列表
     * @return 创建后的项目视图对象
     */
    @PostMapping
    public Result<ProjectVO> create(@RequestBody Map<String, Object> body) {
        Project project = parseProject(body);
        List<Long> techStackIds = parseTechStackIds(body);
        return Result.ok(projectService.create(project, techStackIds));
    }

    /**
     * 更新项目
     *
     * @param id   项目 id
     * @param body 请求体，含项目字段与技术栈 id 列表
     * @return 更新后的项目视图对象
     */
    @PutMapping("/{id}")
    public Result<ProjectVO> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Project project = parseProject(body);
        List<Long> techStackIds = parseTechStackIds(body);
        return Result.ok(projectService.update(id, project, techStackIds));
    }

    /**
     * 删除项目（同时删除其技术栈关联关系）
     *
     * @param id 项目 id
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }

    /**
     * 解析请求体中的技术栈 id 列表，统一转换为 Long 类型
     * <p>
     * 兼容 Jackson 将 JSON 数字解析为 Integer / Long / Double 的多种情况，
     * 以及前端可能传入的 techStackIds / tech_stack_ids 两种字段名。
     *
     * @param body 请求体
     * @return 技术栈 id 列表（元素均为 Long）
     */
    private List<Long> parseTechStackIds(Map<String, Object> body) {
        List<?> rawIds = (List<?>) body.getOrDefault("techStackIds",
                body.getOrDefault("tech_stack_ids", List.of()));
        return rawIds.stream()
                .map(v -> ((Number) v).longValue())
                .toList();
    }

    /**
     * 从请求体中解析项目字段并构造成 {@link Project}
     * <p>
     * 仅解析存在的字段，缺失字段保持默认值，避免前端缺字段时覆盖数据库已有数据。
     *
     * @param body 请求体
     * @return 项目实体（未设置 id）
     */
    private Project parseProject(Map<String, Object> body) {
        Project project = new Project();
        if (body.containsKey("title")) project.setTitle((String) body.get("title"));
        if (body.containsKey("description")) project.setDescription((String) body.get("description"));
        if (body.containsKey("color")) project.setColor((String) body.get("color"));
        if (body.containsKey("link")) project.setLink((String) body.get("link"));
        if (body.containsKey("image")) project.setImage((String) body.get("image"));
        if (body.containsKey("order")) project.setOrder(((Number) body.get("order")).intValue());
        return project;
    }
}

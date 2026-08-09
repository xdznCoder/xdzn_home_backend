package com.xdzn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xdzn.mapper.ProjectMapper;
import com.xdzn.mapper.ProjectTechStackMapper;
import com.xdzn.mapper.TechStackItemMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ProjectDto;
import com.xdzn.model.dto.ProjectVO;
import com.xdzn.model.entity.Project;
import com.xdzn.model.entity.ProjectTechStack;
import com.xdzn.model.entity.TechStackItem;
import com.xdzn.service.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ProjectServiceImpl
 * <p>
 * 项目服务实现，提供项目增删改查与「项目-技术栈」多对多关联的同步。
 * 列表查询通过 Spring Cache 缓存（缓存名 {@code projects}，TTL 10 分钟），
 * 写操作自动失效缓存。
 *
 * @author xdzn
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project>
        implements ProjectService {

    /**
     * 项目-技术栈关联表 Mapper
     */
    private final ProjectTechStackMapper ptsMapper;

    /**
     * 技术栈条目表 Mapper
     */
    private final TechStackItemMapper tsMapper;

    /**
     * 构造注入关联表与技术栈表 Mapper
     *
     * @param ptsMapper 项目-技术栈关联表 Mapper
     * @param tsMapper  技术栈条目表 Mapper
     */
    public ProjectServiceImpl(ProjectTechStackMapper ptsMapper, TechStackItemMapper tsMapper) {
        this.ptsMapper = ptsMapper;
        this.tsMapper = tsMapper;
    }

    /**
     * 查询全部项目（含技术栈信息），结果缓存 10 分钟
     *
     * @return 项目视图对象列表
     */
    @Override
    @Cacheable(value = "projects", key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<ProjectVO> findAll() {
        List<Project> projects = lambdaQuery().orderByAsc(Project::getOrder).list();
        if (projects.isEmpty()) return List.of();

        List<Long> projectIds = projects.stream().map(Project::getId).toList();
        Map<Long, List<String>> techMap = buildTechMap(projectIds);

        return projects.stream()
                .map(p -> toVO(p, techMap.getOrDefault(p.getId(), List.of())))
                .collect(Collectors.toList());
    }

    /**
     * 分页查询项目（含技术栈信息，按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    @Override
    public PageResult<ProjectVO> findAllByPage(long current, long size) {
        Page<Project> page = new Page<>(current, size);
        Page<Project> result = page(page, new LambdaQueryWrapper<Project>().orderByAsc(Project::getOrder));

        if (result.getRecords().isEmpty()) {
            return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), List.of());
        }

        List<Long> projectIds = result.getRecords().stream().map(Project::getId).toList();
        Map<Long, List<String>> techMap = buildTechMap(projectIds);

        List<ProjectVO> voList = result.getRecords().stream()
                .map(p -> toVO(p, techMap.getOrDefault(p.getId(), List.of())))
                .collect(Collectors.toList());

        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 根据 id 查询项目详情（含技术栈信息）
     *
     * @param id 项目 id
     * @return 项目视图对象；不存在时返回 null
     */
    @Override
    public ProjectVO findById(Long id) {
        Project project = getById(id);
        if (project == null) return null;

        Map<Long, List<String>> techMap = buildTechMap(List.of(id));
        return toVO(project, techMap.getOrDefault(id, List.of()));
    }

    /**
     * 创建项目并同步其技术栈关联，失效项目列表缓存
     *
     * @param dto 项目DTO
     * @return 创建后的项目视图对象
     */
    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "'all'")
    public ProjectVO create(ProjectDto dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        save(project);
        syncTechStack(project.getId(), dto.getTechStackIds());
        return toVO(project, getTechStackNames(dto.getTechStackIds()));
    }

    /**
     * 更新项目并同步其技术栈关联，失效项目列表缓存
     *
     * @param id  项目 id
     * @param dto 项目DTO
     * @return 更新后的项目视图对象
     */
    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "'all'")
    public ProjectVO update(Long id, ProjectDto dto) {
        Project project = new Project();
        project.setId(id);
        BeanUtils.copyProperties(dto, project);
        updateById(project);
        syncTechStack(id, dto.getTechStackIds());
        return toVO(getById(id), getTechStackNames(dto.getTechStackIds()));
    }

    /**
     * 删除项目及其技术栈关联，失效项目列表缓存
     *
     * @param id 项目 id
     */
    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "'all'")
    public void delete(Long id) {
        // 删除关联
        ptsMapper.delete(new LambdaQueryWrapper<ProjectTechStack>()
                .eq(ProjectTechStack::getProjectId, id));
        removeById(id);
    }

    // ── 内部方法 ──────────────────────

    /**
     * 批量构建「项目 id → 技术栈名称列表」的映射
     * <p>
     * 先查关联表得到项目与技术栈的对应关系，再批量查询技术栈名称，
     * 避免循环内逐条查询。
     *
     * @param projectIds 项目 id 列表
     * @return 项目 id → 技术栈名称列表映射
     */
    private Map<Long, List<String>> buildTechMap(List<Long> projectIds) {
        List<ProjectTechStack> links = ptsMapper.selectList(
                new LambdaQueryWrapper<ProjectTechStack>()
                        .in(ProjectTechStack::getProjectId, projectIds));

        if (links.isEmpty()) return Map.of();

        List<Long> tsIds = links.stream()
                .map(ProjectTechStack::getTechStackId).distinct().toList();
        Map<Long, String> tsNameMap = tsMapper.selectBatchIds(tsIds).stream()
                .collect(Collectors.toMap(TechStackItem::getId, TechStackItem::getName));

        return links.stream().collect(Collectors.groupingBy(
                ProjectTechStack::getProjectId,
                Collectors.mapping(l -> tsNameMap.get(l.getTechStackId()), Collectors.toList())
        ));
    }

    /**
     * 根据技术栈 id 列表查询名称列表
     *
     * @param ids 技术栈 id 列表
     * @return 技术栈名称列表
     */
    private List<String> getTechStackNames(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return tsMapper.selectBatchIds(ids).stream()
                .map(TechStackItem::getName).collect(Collectors.toList());
    }

    /**
     * 同步项目与技术栈的关联关系（先删旧再插新）
     *
     * @param projectId     项目 id
     * @param techStackIds  技术栈 id 列表
     */
    private void syncTechStack(Long projectId, List<Long> techStackIds) {
        // 删旧
        ptsMapper.delete(new LambdaQueryWrapper<ProjectTechStack>()
                .eq(ProjectTechStack::getProjectId, projectId));
        // 插新
        if (techStackIds != null && !techStackIds.isEmpty()) {
            for (Long tsId : techStackIds) {
                ProjectTechStack pts = new ProjectTechStack();
                pts.setProjectId(projectId);
                pts.setTechStackId(tsId);
                ptsMapper.insert(pts);
            }
        }
    }

    /**
     * 将项目实体转换为视图对象并填充技术栈名称列表
     *
     * @param project   项目实体
     * @param techStack 技术栈名称列表
     * @return 项目视图对象
     */
    private ProjectVO toVO(Project project, List<String> techStack) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        vo.setTechStack(techStack);
        return vo;
    }
}

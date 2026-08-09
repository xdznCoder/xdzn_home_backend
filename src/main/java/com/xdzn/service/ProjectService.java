package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ProjectDto;
import com.xdzn.model.dto.ProjectVO;
import com.xdzn.model.entity.Project;

import java.util.List;

/**
 * ProjectService
 * <p>
 * 项目服务接口，定义项目增删改查（含技术栈关联同步）的契约。
 *
 * @author xdzn
 */
public interface ProjectService extends IService<Project> {

    /**
     * 查询全部项目（含技术栈信息）
     *
     * @return 项目视图对象列表
     */
    List<ProjectVO> findAll();

    /**
     * 分页查询项目（含技术栈信息，按排序号升序）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResult<ProjectVO> findAllByPage(long current, long size);

    /**
     * 根据 id 查询项目详情（含技术栈信息）
     *
     * @param id 项目 id
     * @return 项目视图对象；不存在时返回 null
     */
    ProjectVO findById(Long id);

    /**
     * 创建项目并同步其技术栈关联
     *
     * @param dto 项目DTO
     * @return 创建后的项目视图对象
     */
    ProjectVO create(ProjectDto dto);

    /**
     * 更新项目并同步其技术栈关联
     *
     * @param id  项目 id
     * @param dto 项目DTO
     * @return 更新后的项目视图对象
     */
    ProjectVO update(Long id, ProjectDto dto);

    /**
     * 删除项目及其技术栈关联
     *
     * @param id 项目 id
     */
    void delete(Long id);
}

package com.xdzn.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 根据 id 查询项目详情（含技术栈信息）
     *
     * @param id 项目 id
     * @return 项目视图对象；不存在时返回 null
     */
    ProjectVO findById(Long id);

    /**
     * 创建项目并同步其技术栈关联
     *
     * @param project      项目实体
     * @param techStackIds 技术栈 id 列表
     * @return 创建后的项目视图对象
     */
    ProjectVO create(Project project, List<Long> techStackIds);

    /**
     * 更新项目并同步其技术栈关联
     *
     * @param id           项目 id
     * @param project      项目实体
     * @param techStackIds 技术栈 id 列表
     * @return 更新后的项目视图对象
     */
    ProjectVO update(Long id, Project project, List<Long> techStackIds);

    /**
     * 删除项目及其技术栈关联
     *
     * @param id 项目 id
     */
    void delete(Long id);
}

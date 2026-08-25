package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xdzn.common.BusinessException;
import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.ResourceCategoryMapper;
import com.xdzn.mapper.ResourceMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.dto.ResourceDto;
import com.xdzn.model.dto.ResourceExcelRow;
import com.xdzn.model.entity.Resource;
import com.xdzn.model.entity.ResourceCategory;
import com.xdzn.model.entity.User;
import com.xdzn.model.vo.ResourceVO;
import com.xdzn.service.ResourceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ResourceServiceImpl
 * <p>
 * 资源分享服务实现。
 * <p>
 * 检索：关键词同时匹配标题 / 标签（资源表直接 LIKE），
 * 以及分类名、上传人姓名（先查关联表得 id 集合再 IN）。
 * 权限：编辑 / 删除仅上传人本人或 captain（服务内校验）。
 *
 * @author xdzn
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    /**
     * 资源表 Mapper
     */
    private final ResourceMapper resourceMapper;

    /**
     * 分类表 Mapper（查分类名）
     */
    private final ResourceCategoryMapper categoryMapper;

    /**
     * 用户表 Mapper（查上传人姓名）
     */
    private final UserMapper userMapper;

    /**
     * 构造注入依赖
     *
     * @param resourceMapper 资源表 Mapper
     * @param categoryMapper 分类表 Mapper
     * @param userMapper     用户表 Mapper
     */
    /**
     * 通用 Excel 导出服务
     */
    private final ExcelService excelService;

    public ResourceServiceImpl(ResourceMapper resourceMapper,
                               ResourceCategoryMapper categoryMapper,
                               UserMapper userMapper,
                               ExcelService excelService) {
        this.resourceMapper = resourceMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.excelService = excelService;
    }

    /**
     * 分页检索资源（关键词匹配标题/标签/分类名/上传人，可按分类筛选）
     *
     * @param current    当前页码
     * @param size       每页大小
     * @param keyword    关键词
     * @param categoryId 分类 id
     * @return 分页结果
     */
    @Override
    public PageResult<ResourceVO> findAllByPage(long current, long size, String keyword, Long categoryId) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Resource::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(x -> {
                // 标题 / 标签直接 LIKE
                x.like(Resource::getTitle, keyword).or().like(Resource::getTags, keyword);
                // 分类名匹配：先查匹配的分类 id
                List<Long> catIds = categoryMapper.selectList(
                                new LambdaQueryWrapper<ResourceCategory>().like(ResourceCategory::getName, keyword))
                        .stream().map(ResourceCategory::getId).toList();
                if (!catIds.isEmpty()) {
                    x.or().in(Resource::getCategoryId, catIds);
                }
                // 上传人姓名匹配：先查匹配的用户 id
                List<Long> userIds = userMapper.selectList(
                                new LambdaQueryWrapper<User>().like(User::getName, keyword))
                        .stream().map(User::getId).toList();
                if (!userIds.isEmpty()) {
                    x.or().in(Resource::getUploaderId, userIds);
                }
            });
        }
        wrapper.orderByDesc(Resource::getCreatedAt);

        Page<Resource> page = resourceMapper.selectPage(new Page<>(current, size), wrapper);
        List<ResourceVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), voList);
    }

    /**
     * 上传资源（上传人取当前登录用户）
     *
     * @param dto 资源 DTO
     * @return 创建后的资源视图
     */
    @Override
    @Transactional
    public ResourceVO create(ResourceDto dto) {
        Resource resource = new Resource();
        BeanUtils.copyProperties(dto, resource);
        resource.setUploaderId(StpUtil.getLoginIdAsLong());
        resourceMapper.insert(resource);
        return toVO(resourceMapper.selectById(resource.getId()));
    }

    /**
     * 更新资源（上传人本人或 captain）
     *
     * @param id  资源 id
     * @param dto 资源 DTO
     * @return 更新后的资源视图
     */
    @Override
    @Transactional
    public ResourceVO update(Long id, ResourceDto dto) {
        Resource old = resourceMapper.selectById(id);
        if (old == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "资源不存在");
        }
        checkOwnerOrCaptain(old);
        Resource resource = new Resource();
        resource.setId(id);
        BeanUtils.copyProperties(dto, resource);
        resourceMapper.updateById(resource);
        return toVO(resourceMapper.selectById(id));
    }

    /**
     * 删除资源（上传人本人或 captain）
     *
     * @param id 资源 id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Resource old = resourceMapper.selectById(id);
        if (old == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "资源不存在");
        }
        checkOwnerOrCaptain(old);
        resourceMapper.deleteById(id);
    }

    /**
     * 根据 id 查询资源详情
     *
     * @param id 资源 id
     * @return 资源视图；不存在时返回 null
     */
    @Override
    public ResourceVO findById(Long id) {
        Resource resource = resourceMapper.selectById(id);
        return resource == null ? null : toVO(resource);
    }

    // ── 内部方法 ──────────────────────

    /**
     * 校验操作权限：captain 或资源上传人本人
     *
     * @param resource 资源实体
     */
    private void checkOwnerOrCaptain(Resource resource) {
        if (StpUtil.hasRole("captain")) {
            return;
        }
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (resource.getUploaderId() != null && resource.getUploaderId().equals(currentUserId)) {
            return;
        }
        throw new BusinessException(HttpStatus.FORBIDDEN, "无权操作该资源");
    }

    /**
     * 将资源实体转换为视图对象（补分类名、上传人姓名）
     *
     * @param resource 资源实体
     * @return 资源视图
     */
    private ResourceVO toVO(Resource resource) {
        ResourceVO vo = new ResourceVO();
        BeanUtils.copyProperties(resource, vo);
        if (resource.getCategoryId() != null) {
            ResourceCategory category = categoryMapper.selectById(resource.getCategoryId());
            vo.setCategoryName(category != null ? category.getName() : null);
        }
        if (resource.getUploaderId() != null) {
            User uploader = userMapper.selectById(resource.getUploaderId());
            vo.setUploaderName(uploader != null ? uploader.getName() : null);
        }
        return vo;
    }

    /**
     * 导出资源列表到 Excel
     *
     * @param response HTTP 响应
     */
    @Override
    public void export(HttpServletResponse response) {
        List<Resource> resources = resourceMapper.selectList(
                new LambdaQueryWrapper<Resource>().orderByDesc(Resource::getCreatedAt));
        List<ResourceExcelRow> rows = resources.stream().map(this::toExcelRow).collect(Collectors.toList());
        excelService.export(response, rows, ResourceExcelRow.class, "资源", "资源分享");
    }

    /**
     * 资源转导出行
     *
     * @param resource 资源实体
     * @return 导出行
     */
    private ResourceExcelRow toExcelRow(Resource resource) {
        ResourceVO vo = toVO(resource);
        ResourceExcelRow row = new ResourceExcelRow();
        row.setTitle(vo.getTitle());
        row.setCategoryName(vo.getCategoryName());
        row.setTags(vo.getTags());
        row.setUploaderName(vo.getUploaderName());
        row.setAttachmentName(vo.getAttachmentName());
        row.setCreatedAt(vo.getCreatedAt() != null ? vo.getCreatedAt().toString() : "");
        row.setDescription(vo.getDescription());
        return row;
    }
}

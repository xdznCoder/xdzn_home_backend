package com.xdzn.service.impl;

import cn.hutool.core.io.IoUtil;
import com.xdzn.common.BusinessException;
import com.xdzn.mapper.FileMapper;
import com.xdzn.model.entity.FileEntity;
import com.xdzn.model.vo.FileVO;
import com.xdzn.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * FileServiceImpl
 * <p>
 * 通用文件上传下载服务实现（轻量级本地磁盘存储）：
 * <ul>
 *     <li>上传：校验空文件/大小上限，生成 UUID 存储名（+安全扩展名），写入上传目录，记录元数据</li>
 *     <li>下载：按 id 读取元数据与磁盘文件流输出，设置 MIME 与中文文件名（RFC 5987）</li>
 *     <li>删除：移除磁盘文件与元数据记录</li>
 * </ul>
 * 支持任意常见格式（图片/文档/压缩包等），不依赖外部对象存储组件。
 *
 * @author xdzn
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * 文件表 Mapper
     */
    private final FileMapper fileMapper;

    /**
     * 上传目录（配置 app.upload-dir）
     */
    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 单文件大小上限（字节，配置 app.max-file-size）
     */
    @Value("${app.max-file-size:20971520}")
    private long maxFileSize;

    /**
     * 构造注入依赖
     *
     * @param fileMapper 文件表 Mapper
     */
    public FileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 上传文件
     *
     * @param file 待上传文件
     * @return 文件视图
     */
    @Override
    public FileVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件大小超出限制");
        }

        String originalName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename() : "unnamed";
        // UUID 存储名 + 安全扩展名，防路径穿越与重名覆盖
        String storedName = UUID.randomUUID().toString().replace("-", "") + extractExtension(originalName);

        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "创建上传目录失败");
        }
        try {
            file.transferTo(new File(dir, storedName));
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "保存文件失败");
        }

        FileEntity record = new FileEntity();
        record.setOriginalName(originalName);
        record.setStoredName(storedName);
        record.setContentType(file.getContentType());
        record.setSize(file.getSize());
        fileMapper.insert(record);
        return toVO(record);
    }

    /**
     * 下载/访问文件
     *
     * @param id       文件记录 id
     * @param response HTTP 响应
     */
    @Override
    public void download(Long id, HttpServletResponse response) {
        FileEntity record = fileMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文件不存在");
        }
        File file = new File(new File(uploadDir).getAbsoluteFile(), record.getStoredName());
        if (!file.exists()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文件不存在");
        }
        try (InputStream in = new FileInputStream(file)) {
            response.setContentType(record.getContentType() != null
                    ? record.getContentType() : "application/octet-stream");
            response.setContentLengthLong(record.getSize());
            // 中文文件名 URL 编码（RFC 5987）
            String encoded = URLEncoder.encode(record.getOriginalName(), StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + encoded);
            IoUtil.copy(in, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "读取文件失败");
        }
    }

    /**
     * 删除文件
     *
     * @param id 文件记录 id
     */
    @Override
    public void delete(Long id) {
        FileEntity record = fileMapper.selectById(id);
        if (record == null) {
            return;
        }
        File file = new File(new File(uploadDir).getAbsoluteFile(), record.getStoredName());
        if (file.exists()) {
            file.delete();
        }
        fileMapper.deleteById(id);
    }

    // ── 内部方法 ──────────────────────

    /**
     * 从原始文件名提取安全扩展名（仅字母数字，防注入）
     *
     * @param originalName 原始文件名
     * @return 扩展名（含点，小写）；无合法扩展名时返回空串
     */
    private String extractExtension(String originalName) {
        if (originalName == null) {
            return "";
        }
        int dot = originalName.lastIndexOf('.');
        if (dot < 0 || dot == originalName.length() - 1) {
            return "";
        }
        String ext = originalName.substring(dot);
        return ext.matches("\\.[A-Za-z0-9]+") ? ext.toLowerCase() : "";
    }

    /**
     * 转换为文件视图对象（附带访问地址）
     *
     * @param record 文件元数据
     * @return 文件视图
     */
    private FileVO toVO(FileEntity record) {
        FileVO vo = new FileVO();
        vo.setId(record.getId());
        vo.setOriginalName(record.getOriginalName());
        vo.setContentType(record.getContentType());
        vo.setSize(record.getSize());
        vo.setUrl("/api/files/" + record.getId());
        return vo;
    }
}

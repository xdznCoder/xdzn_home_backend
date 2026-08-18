package com.xdzn.service;

import com.xdzn.model.vo.FileVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileService
 * <p>
 * 通用文件上传下载服务接口，提供上传（返回访问地址）、下载与删除。
 * 实际文件存储在本地磁盘上传目录，元数据记录在 files 表。
 *
 * @author xdzn
 */
public interface FileService {

    /**
     * 上传文件（校验大小/类型，安全命名存储）
     *
     * @param file 待上传文件
     * @return 文件视图（含访问地址 url）
     */
    FileVO upload(MultipartFile file);

    /**
     * 下载/访问文件（按 MIME 类型输出，中文文件名经 URL 编码）
     *
     * @param id       文件记录 id
     * @param response HTTP 响应
     */
    void download(Long id, HttpServletResponse response);

    /**
     * 删除文件（磁盘文件 + 元数据记录）
     *
     * @param id 文件记录 id
     */
    void delete(Long id);
}

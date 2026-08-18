package com.xdzn.model.vo;

import lombok.Data;

/**
 * FileVO
 * <p>
 * 文件视图对象，上传成功后返回，供业务方将 {@code url} 存入附件等字段。
 *
 * @author xdzn
 */
@Data
public class FileVO {

    /**
     * 文件记录 id
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件访问地址（如 /api/files/{id}）
     */
    private String url;
}

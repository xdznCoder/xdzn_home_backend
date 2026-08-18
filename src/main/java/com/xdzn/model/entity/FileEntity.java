package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FileEntity
 * <p>
 * 文件元数据实体，对应数据库表 {@code files}。
 * 通用文件上传服务的元数据记录，实际文件内容按 {@code storedName} 存储在本地磁盘上传目录。
 *
 * @author xdzn
 */
@Data
@TableName("files")
public class FileEntity {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 原始文件名（用户上传时的名称）
     */
    private String originalName;

    /**
     * 存储文件名（UUID + 安全扩展名，防路径穿越与重名覆盖）
     */
    private String storedName;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 上传时间
     */
    private LocalDateTime createdAt;
}

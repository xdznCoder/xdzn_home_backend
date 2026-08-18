package com.xdzn.service;

/**
 * ImageHostService
 * <p>
 * 图床服务抽象接口：上传图片字节到外部图床，返回公开可访问的 URL。
 * 供成员头像、项目封面等官网展示图片使用（图片存图床，表项只存 URL）。
 *
 * @author xdzn
 */
public interface ImageHostService {

    /**
     * 上传图片到图床
     *
     * @param imageBytes       图片字节
     * @param originalFilename 原始文件名（用于确定扩展名）
     * @return 图床公开访问 URL
     */
    String uploadImage(byte[] imageBytes, String originalFilename);
}

package com.xdzn.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xdzn.common.BusinessException;
import com.xdzn.service.ImageHostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * SmmsImageHostService
 * <p>
 * SM.MS 图床实现（免费图床服务）。
 * 调用 {@code https://sm.ms/api/v2/upload} 上传图片，需配置 API token。
 * 支持常见图片格式（jpg/png/gif/webp 等），返回图床公开 URL。
 *
 * @author xdzn
 */
@Service
public class SmmsImageHostService implements ImageHostService {

    /**
     * SM.MS 图床上传接口地址
     */
    private static final String SMMS_API = "https://sm.ms/api/v2/upload";

    /**
     * SM.MS API token（配置 app.image-hosting.smms-token）
     */
    @Value("${app.image-hosting.smms-token:}")
    private String smmsToken;

    /**
     * 上传图片到 SM.MS 图床
     *
     * @param imageBytes       图片字节
     * @param originalFilename 原始文件名
     * @return 图床公开访问 URL
     */
    @Override
    public String uploadImage(byte[] imageBytes, String originalFilename) {
        if (!StringUtils.hasText(smmsToken)) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "图床未配置（app.image-hosting.smms-token）");
        }
        try {
            HttpResponse resp = HttpRequest.post(SMMS_API)
                    .header("Authorization", smmsToken)
                    .form("smfile", imageBytes, originalFilename)
                    .timeout(15000)
                    .execute();
            JSONObject json = JSONUtil.parseObj(resp.body());
            if (Boolean.TRUE.equals(json.getBool("success"))) {
                JSONObject data = json.getJSONObject("data");
                if (data != null) {
                    String url = data.getStr("url");
                    if (StringUtils.hasText(url)) {
                        return url;
                    }
                }
            }
            // SM.MS 对已存在的相同图片，data 直接返回该图片的 URL 字符串
            Object data = json.get("data");
            if (data instanceof String url && StringUtils.hasText(url)) {
                return url;
            }
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "图床上传失败: " + json.getStr("message", "未知错误"));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "图床服务异常");
        }
    }
}

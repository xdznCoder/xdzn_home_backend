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

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GithubImageHostService
 * <p>
 * GitHub 图床实现（免费）：通过 GitHub Contents API 将图片提交到指定仓库，
 * 使用 jsDelivr CDN（{@code cdn.jsdelivr.net/gh/...}）提供公开加速访问。
 * <p>
 * 需要配置 GitHub Personal Access Token（repo 权限）与目标仓库，
 * 推荐在团队组织下建一个专门存图的仓库（如 {@code xdzn-images}）。
 *
 * @author xdzn
 */
@Service
public class GithubImageHostService implements ImageHostService {

    /**
     * GitHub API 地址
     */
    private static final String GITHUB_API = "https://api.github.com/repos/";

    /**
     * GitHub token（配置 app.image-hosting.github-token，需 repo 权限）
     */
    @Value("${app.image-hosting.github-token:}")
    private String githubToken;

    /**
     * 目标仓库，如 xdznCoder/xdzn-images（配置 app.image-hosting.github-repo）
     */
    @Value("${app.image-hosting.github-repo:xdznCoder/xdzn-images}")
    private String githubRepo;

    /**
     * 仓库内图片存放目录前缀（配置 app.image-hosting.github-path）
     */
    @Value("${app.image-hosting.github-path:images}")
    private String githubPath;

    /**
     * 仓库分支（配置 app.image-hosting.github-branch）
     */
    @Value("${app.image-hosting.github-branch:main}")
    private String githubBranch;

    /**
     * 上传图片到 GitHub 图床
     * <p>
     * 文件名用 UUID 保证唯一，按「年份/月份」分子目录便于管理；
     * 返回 jsDelivr CDN 地址（更快、公开可访问）。
     *
     * @param imageBytes       图片字节
     * @param originalFilename 原始文件名
     * @return jsDelivr CDN URL
     */
    @Override
    public String uploadImage(byte[] imageBytes, String originalFilename) {
        if (!StringUtils.hasText(githubToken)) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "GitHub 图床未配置（app.image-hosting.github-token）");
        }
        String ext = extractExtension(originalFilename);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        // 按日期分子目录：images/2026/08/uuid.png
        LocalDate now = LocalDate.now();
        String path = String.format("%s/%d/%02d/%s", githubPath, now.getYear(), now.getMonthValue(), fileName);

        // GitHub Contents API：PUT /repos/{owner}/{repo}/contents/{path}
        String apiUrl = GITHUB_API + githubRepo + "/contents/" + path;
        Map<String, Object> body = new HashMap<>();
        body.put("message", "upload image " + fileName);
        body.put("content", Base64.getEncoder().encodeToString(imageBytes));
        body.put("branch", githubBranch);

        try {
            HttpResponse resp = HttpRequest.put(apiUrl)
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github+json")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(body))
                    .timeout(20000)
                    .execute();
            if (resp.getStatus() >= 200 && resp.getStatus() < 300) {
                return buildJsDelivrUrl(path);
            }
            JSONObject json = JSONUtil.parseObj(resp.body());
            throw new BusinessException(HttpStatus.BAD_GATEWAY,
                    "GitHub 图床上传失败: " + json.getStr("message", "HTTP " + resp.getStatus()));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "GitHub 图床服务异常");
        }
    }

    // ── 内部方法 ──────────────────────

    /**
     * 构建 jsDelivr CDN 访问地址
     *
     * @param path 仓库内文件路径
     * @return CDN URL
     */
    private String buildJsDelivrUrl(String path) {
        String[] parts = githubRepo.split("/");
        String owner = parts.length > 0 ? parts[0] : "xdznCoder";
        String repo = parts.length > 1 ? parts[1] : "xdzn-images";
        return "https://cdn.jsdelivr.net/gh/" + owner + "/" + repo + "@" + githubBranch + "/" + path;
    }

    /**
     * 从原始文件名提取安全扩展名，缺省返回 .png
     *
     * @param name 原始文件名
     * @return 扩展名（含点，小写）
     */
    private String extractExtension(String name) {
        if (name == null) {
            return ".png";
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return ".png";
        }
        String ext = name.substring(dot);
        return ext.matches("\\.[A-Za-z0-9]+") ? ext.toLowerCase() : ".png";
    }
}

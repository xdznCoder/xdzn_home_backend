package com.xdzn.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig
 * <p>
 * 跨域配置，允许前端站点访问 {@code /api/**} 接口。
 * 允许的前端来源由配置项 {@code app.frontend-url} 指定，并支持携带 Cookie（凭据）。
 *
 * @author xdzn
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 允许跨域访问的前端地址（读取自配置 app.frontend-url）
     */
    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * 注册跨域映射规则
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

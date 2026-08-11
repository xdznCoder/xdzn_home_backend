package com.xdzn.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SaTokenConfig
 * <p>
 * Sa-Token 鉴权配置，注册路由拦截器并声明接口访问规则：
 * <ul>
 *     <li>{@code /api/admin/**}：需登录且角色为 admin</li>
 *     <li>{@code /api/auth/me}：需登录（查询当前用户信息）</li>
 *     <li>{@code /api/**} 的 POST/PUT/PATCH/DELETE 写操作：需登录且角色为 admin
 *         （例外：{@code /api/auth/**} 认证接口与 {@code /api/joins} 提交报名对匿名开放）</li>
 * </ul>
 *
 * @author xdzn
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 声明 Sa-Token 拦截器，内置各路由的权限校验规则
     *
     * @return Sa-Token 拦截器
     */
    @Bean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor(handle -> {
            // 所有 /api/admin/** 需要登录 + admin 角色
            SaRouter.match("/api/admin/**")
                    .check(r -> StpUtil.checkLogin())
                    .check(r -> StpUtil.checkRole("admin"));

            // 当前用户信息接口需要登录
            SaRouter.match("/api/auth/me")
                    .check(r -> StpUtil.checkLogin());

            // 成员 Excel 导出 / 模板下载（GET）需要 admin 角色（导入为 POST，由下方写操作规则覆盖）
            SaRouter.match("/api/members/export", "/api/members/import/template")
                    .check(r -> StpUtil.checkLogin())
                    .check(r -> StpUtil.checkRole("admin"));

            // CMS 写接口需要登录 + admin 角色
            SaRouter.match("/api/**")
                    .matchMethod("POST", "PUT", "PATCH", "DELETE")
                    .notMatch("/api/auth/**", "/api/joins")
                    .check(r -> StpUtil.checkLogin())
                    .check(r -> StpUtil.checkRole("admin"));
        });
    }

    /**
     * 注册拦截器到所有 {@code /api/**} 路径
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(saInterceptor())
                .addPathPatterns("/api/**");
    }
}

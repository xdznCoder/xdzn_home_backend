package com.xdzn.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4jConfig
 * <p>
 * 接口文档配置（Knife4j，基于 SpringDoc OpenAPI）。
 * <ul>
 *     <li>配置文档标题、描述、版本等基本信息</li>
 *     <li>配置全局鉴权方案：请求头 {@code Authorization: Bearer <token>}，
 *         与 Sa-Token 的 {@code token-name=Authorization}、{@code token-prefix=Bearer} 一致</li>
 * </ul>
 * <p>
 * 文档地址：{@code http://localhost:3001/doc.html}。
 * 受保护接口可在文档右上角「Authorize」填入登录返回的 access_token 后调试。
 *
 * @author xdzn
 */
@Configuration
public class Knife4jConfig {

    /**
     * 自定义 OpenAPI 文档元信息与全局鉴权方案
     *
     * @return OpenAPI 配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 全局鉴权方案：HTTP Bearer（对应 Sa-Token 的 Authorization: Bearer <token>）
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("虚动智能官网后端 API")
                        .description("""
                                虚动智能官网后端接口文档，涵盖展示主页（Public）与内部团队管理（Internal）接口。

                                ## 鉴权说明
                                管理 / 内部接口需要在请求头携带 `Authorization: Bearer <access_token>`，
                                token 由登录 / 注册接口返回。点击文档右上角「Authorize」填入后即可调试受保护接口。

                                ## 常见错误
                                - `401 未登录`：未携带或 token 已过期
                                - `403`：无管理员权限
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("虚动智能")))
                // 全局默认带鉴权标识
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerScheme));
    }
}

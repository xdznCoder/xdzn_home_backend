package com.xdzn.xdzn_home_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * XdznHomeBackendApplication
 * <p>
 * 虚动智能官网后端启动类。
 * <p>
 * 由于 Mapper / 服务等组件分布在 {@code com.xdzn} 各子包下（如 mapper、service、redis）：
 * <ul>
 *     <li>{@code @ComponentScan("com.xdzn")}：显式扫描基础包以外的 Spring 组件</li>
 *     <li>{@code @MapperScan("com.xdzn.mapper")}：显式扫描 MyBatis-Plus Mapper 接口
 *         （默认自动配置仅扫描主类所在包，无法覆盖到 mapper 子包）</li>
 * </ul>
 * {@code @EnableAsync} 启用 Spring 异步支持（预留招新通知邮件等异步场景）。
 *
 * @author xdzn
 */
@SpringBootApplication
@ComponentScan("com.xdzn")
@MapperScan("com.xdzn.mapper")
@EnableAsync
public class XdznHomeBackendApplication {

    /**
     * 应用入口
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(XdznHomeBackendApplication.class, args);
    }
}

package com.xdzn.common.config;

import cn.dev33.satoken.stp.StpInterface;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StpInterfaceImpl
 * <p>
 * Sa-Token 权限数据源实现：向 Sa-Token 提供账号的角色 / 权限列表。
 * <p>
 * Sa-Token 的 {@code checkRole} / {@code checkPermission} 通过本接口查询数据
 * （而非读取登录时写入会话的属性）。本项目角色存储在 users 表 {@code role} 字段，
 * 登录后由 Sa-Token 每次鉴权时经 {@link #getRoleList} 查询。
 *
 * @author xdzn
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 用户表 Mapper，用于按登录 id 查询角色
     */
    private final UserMapper userMapper;

    /**
     * 构造注入依赖
     *
     * @param userMapper 用户表 Mapper
     */
    public StpInterfaceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 返回账号拥有的权限码列表
     * <p>
     * 本项目暂无权限码体系（仅角色校验），返回空列表。
     *
     * @param loginId   登录 id
     * @param loginType 登录类型（默认 "login"）
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回账号拥有的角色列表（来自 users 表 role 字段）
     *
     * @param loginId   登录 id（用户主键 Long）
     * @param loginType 登录类型
     * @return 角色列表，如 ["admin"]、["member"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // JWT 模式下 loginId 可能以 String 形式传入，统一转 Long 后查询
        Long userId = Long.valueOf(String.valueOf(loginId));
        User user = userMapper.selectById(userId);
        if (user == null) {
            return List.of();
        }
        return List.of(user.getRole());
    }
}

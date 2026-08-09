package com.xdzn.common.config;

import cn.dev33.satoken.stp.StpInterface;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StpInterfaceImpl
 * <p>
 * Sa-Token 角色/权限加载接口实现类。
 * 在 Sa-Token 鉴权时,系统会自动调用本类的方法来获取登录用户的角色信息。
 *
 * @author xdzn
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 用户表 Mapper
     */
    private final UserMapper userMapper;

    /**
     * 构造注入 UserMapper
     *
     * @param userMapper 用户表 Mapper
     */
    public StpInterfaceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取登录用户的角色列表
     * <p>
     * 根据登录ID查询数据库,返回用户的role字段(如"admin"/"member")。
     *
     * @param loginId   登录ID(即用户ID)
     * @param loginType 登录类型
     * @return 角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        User user = userMapper.selectById(userId);
        if (user != null && user.getRole() != null) {
            return Collections.singletonList(user.getRole());
        }
        return new ArrayList<>();
    }

    /**
     * 获取登录用户的权限列表
     * <p>
     * 本项目暂不使用细粒度权限控制,返回空列表。
     *
     * @param loginId   登录ID(即用户ID)
     * @param loginType 登录类型
     * @return 权限列表(空)
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }
}
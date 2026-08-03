package com.xdzn.common.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.xdzn.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SaTokenExceptionHandler
 * <p>
 * Sa-Token 鉴权异常处理器，将未登录 / 无权限异常转换为统一的 {@link Result} 响应。
 *
 * @author xdzn
 */
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 处理未登录异常（NotLoginException）→ 401
     *
     * @return 未登录响应
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLogin() {
        return Result.unauthorized("未登录");
    }

    /**
     * 处理无权限异常（NotRoleException）→ 403
     *
     * @return 无权限响应
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRole() {
        return Result.forbidden("Admin role required");
    }
}

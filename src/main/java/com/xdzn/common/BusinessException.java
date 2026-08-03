package com.xdzn.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * BusinessException
 * <p>
 * 业务异常，服务层校验失败或业务规则不满足时抛出。
 * 携带对应的 HTTP 状态码，由 {@link GlobalExceptionHandler} 统一转换为接口响应。
 *
 * @author xdzn
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 对应的 HTTP 状态码
     */
    private final HttpStatus httpStatus;

    /**
     * 构造业务异常
     *
     * @param httpStatus HTTP 状态码
     * @param message    错误信息
     */
    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

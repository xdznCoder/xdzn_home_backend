package com.xdzn.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler
 * <p>
 * 全局异常处理器，统一拦截 Controller 层抛出的异常并转换为 {@link Result} 响应：
 * <ul>
 *     <li>{@link MethodArgumentNotValidException}：请求参数校验失败 → 400</li>
 *     <li>{@link BusinessException}：业务异常 → 按其携带的 HTTP 状态码返回</li>
 *     <li>{@link Exception}：兜底未知异常 → 500</li>
 * </ul>
 *
 * @author xdzn
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求体参数校验异常，聚合所有字段的错误信息
     *
     * @param ex 参数校验异常
     * @return 400 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.badRequest(msg);
    }

    /**
     * 处理业务异常，按其携带的 HTTP 状态码返回对应错误
     *
     * @param ex 业务异常
     * @return 错误响应（状态码与异常一致）
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(Result.error(ex.getHttpStatus().value(), ex.getMessage()));
    }

    /**
     * 兜底处理未知异常，返回 500 并记录完整堆栈
     *
     * @param ex 未知异常
     * @return 500 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnknown(Exception ex) {
        log.error("Unhandled exception", ex);
        return Result.error("服务器内部错误");
    }
}

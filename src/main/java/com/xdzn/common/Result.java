package com.xdzn.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result
 * <p>
 * 统一接口响应体，约定为 {@code {code, msg, data}} 结构，
 * 供所有 Controller 返回业务结果，保证前端解析规则一致。
 *
 * @param <T> 业务数据类型
 * @author xdzn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 业务状态码：200 表示成功，其余为失败（与 HTTP 状态码语义一致）
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    // ── 成功 ────────────────────────

    /**
     * 成功响应（携带数据）
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应体
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "ok", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应体
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    // ── 失败 ────────────────────────

    /**
     * 失败响应（自定义状态码）
     *
     * @param code 业务状态码
     * @param msg  错误信息
     * @param <T>  数据类型
     * @return 失败响应体
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败响应（默认状态码 500）
     *
     * @param msg 错误信息
     * @param <T> 数据类型
     * @return 失败响应体
     */
    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    // ── 常用错误快捷 ────────────────

    /**
     * 未认证响应（401）
     *
     * @param msg 错误信息
     * @param <T> 数据类型
     * @return 未认证响应体
     */
    public static <T> Result<T> unauthorized(String msg) {
        return error(401, msg);
    }

    /**
     * 无权限响应（403）
     *
     * @param msg 错误信息
     * @param <T> 数据类型
     * @return 无权限响应体
     */
    public static <T> Result<T> forbidden(String msg) {
        return error(403, msg);
    }

    /**
     * 资源不存在响应（404）
     *
     * @param <T> 数据类型
     * @return 404 响应体
     */
    public static <T> Result<T> notFound() {
        return error(404, "资源不存在");
    }

    /**
     * 参数校验失败响应（400）
     *
     * @param msg 错误信息
     * @param <T> 数据类型
     * @return 400 响应体
     */
    public static <T> Result<T> badRequest(String msg) {
        return error(400, msg);
    }
}

package com.edu.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一返回结果包装类
 * <p>
 * 所有 Controller 接口统一返回此格式：
 * { "code": 200, "message": "success", "data": {} }
 *
 * @param <T> data 字段的类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /** 状态码：200=成功，其他为错误码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 返回数据 */
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 静态工厂方法 ==========

    /** 操作成功，无返回数据 */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 操作成功，带返回数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 操作成功，自定义提示消息 + 数据 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /** 操作失败，指定错误码和错误信息 */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // ========== Getter / Setter ==========

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

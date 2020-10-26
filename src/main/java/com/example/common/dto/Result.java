package com.example.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Result<T> implements Serializable {

    /**
     * 正常
     */
    public static final int OK = 0;
    /**
     * 服务内部异常
     */
    public static final int SERVER_ERROR = -1;
    /**
     * 参数非法
     */
    public static final int ILLEGAL_PARAMETER = -2;

    private Integer code;

    private String msg;

    private T data;

    public Result() {

    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(OK, null, data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(OK, msg, data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(OK, null, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(SERVER_ERROR, null, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(SERVER_ERROR, msg, null);
    }

    public static <T> Result<T> illegalParameterError() {
        return new Result<>(ILLEGAL_PARAMETER, null, null);
    }

    public static <T> Result<T> illegalParameterError(String msg) {
        return new Result<>(ILLEGAL_PARAMETER, msg, null);
    }
}

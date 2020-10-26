package com.example.common.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResultWithTotal<T> extends Result<T> {

    private long total;

    public ResultWithTotal() {
    }

    public ResultWithTotal(Integer code, String msg, T data) {
        super(code, msg, data);
    }

    public ResultWithTotal(Integer code, String msg, T data, long total) {
        this(code, msg, data);
        this.total = total;
    }

    public static <T> ResultWithTotal<T> ok(String msg, T data, long total) {
        return new ResultWithTotal<>(OK, msg, data, total);
    }

    public static <T> ResultWithTotal<T> ok(T data, long total) {
        return new ResultWithTotal<>(OK, null, data, total);
    }
}

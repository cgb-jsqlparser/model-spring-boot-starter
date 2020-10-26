package com.example.exceptions;

/**
 * MybatisPlus 异常类
 *
 * @author hubin
 * @since 2016-01-23
 */
public class ModelAdapterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModelAdapterException(String message) {
        super(message);
    }

    public ModelAdapterException(Throwable throwable) {
        super(throwable);
    }

    public ModelAdapterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

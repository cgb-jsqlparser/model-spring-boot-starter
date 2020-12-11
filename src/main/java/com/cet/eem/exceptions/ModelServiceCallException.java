package com.cet.eem.exceptions;

/**
 * 异常类
 *
 * @author hubin
 * @since 2016-01-23
 */
public class ModelServiceCallException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModelServiceCallException(String message) {
        super(message);
    }

    public ModelServiceCallException(Throwable throwable) {
        super(throwable);
    }

    public ModelServiceCallException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

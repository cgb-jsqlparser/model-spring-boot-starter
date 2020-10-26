package com.example.model.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName : ModelServiceCallException
 * @Description :
 * @Author : zhangh
 * @Date: 2020-07-22 15:31
 */
@Slf4j
public class ModelServiceCallException extends RuntimeException {

    public ModelServiceCallException() {
        super();
    }

    public ModelServiceCallException(String message) {
        super(message);
    }
}

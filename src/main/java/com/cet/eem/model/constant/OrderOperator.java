package com.cet.eem.model.constant;

import lombok.Getter;

public enum OrderOperator {

    /**
     * 升序
     */
    ASC("asc"),
    /**
     * 降序
     */
    DESC("desc");

    @Getter
    private String value;

    OrderOperator(String value) {
        this.value = value;
    }
}

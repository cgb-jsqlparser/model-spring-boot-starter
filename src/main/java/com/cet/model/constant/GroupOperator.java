package com.cet.model.constant;

import lombok.Getter;

public enum GroupOperator {

    /**
     * 最大值
     */
    MAX("MAX"),
    /**
     * 平均值
     */
    AVG("AVG"),
    /**
     * 最小值
     */
    MIN("MIN");

    @Getter
    private String value;

    GroupOperator(String value) {
        this.value = value;
    }
}

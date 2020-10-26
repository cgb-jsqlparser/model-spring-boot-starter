package com.example.model.constant;

import lombok.Getter;

public enum ConditionOperator {
    /**
     * equals
     */
    EQ("EQ"),
    /**
     * <
     */
    LT("LT"),
    /**
     * >
     */
    GT("GT"),
    /**
     * <=
     */
    LE("LE"),
    /**
     * >=
     */
    GE("GE"),
    /**
     * <>=
     */
    NE("NE"),
    /**
     * like
     */
    LIKE("LIKE"),
    /**
     * in
     */
    IN("IN");

    @Getter
    private String value;

    ConditionOperator(String value) {
        this.value = value;
    }
}

package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Order {

    private String propertyLabel;
    /**
     * orderType：asc、desc
     */
    private String orderType;
    /**
     *  优先级，数值越小优先级越高
     */
    private Integer priority;

    public Order() {

    }

    public Order(String propertyLabel, String orderType, Integer priority) {
        this.propertyLabel = propertyLabel;
        this.orderType = orderType;
        this.priority = priority;
    }
}

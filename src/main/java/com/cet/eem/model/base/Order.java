package com.cet.eem.model.base;

/**
 * 排序
 * @author CKai
 */
public class Order {
    public String propertyLabel;
    /**
     * orderType：asc、desc
     */
    public String orderType;
    /**
     *  优先级，数值越小优先级越高
     */
    public Integer priority;

    public Order() {

    }

    public Order(String propertyLabel, String orderType, Integer priority) {
        this.propertyLabel = propertyLabel;
        this.orderType = orderType;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Order{" +
                "propertyLabel='" + propertyLabel + '\'' +
                ", orderType='" + orderType + '\'' +
                ", priority=" + priority +
                '}';
    }
}

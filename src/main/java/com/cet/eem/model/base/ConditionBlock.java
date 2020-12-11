package com.cet.eem.model.base;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 条件块
 *
 * @author CKai
 */
@Getter
@Setter
public class ConditionBlock {
    /**
     * 等于
     */
    public static final String OPERATOR_EQ = "EQ";
    /**
     * 小于
     */
    public static final String OPERATOR_LT = "LT";
    /**
     * 大于
     */
    public static final String OPERATOR_GT = "GT";
    /**
     * 小于等于
     */
    public static final String OPERATOR_LE = "LE";
    /**
     * 大于等于
     */
    public static final String OPERATOR_GE = "GE";
    /**
     * 不等于
     */
    public static final String OPERATOR_NE = "NE";
    /**
     * 模糊查询
     */
    public static final String OPERATOR_LIKE = "LIKE";
    /**
     * 在某个集合中
     */
    public static final String OPERATOR_IN = "IN";

    /**
     * 升序
     */
    public static final String ASC = "asc";
    /**
     * 降序
     */
    public static final String DESC = "desc";

    @NotNull
    private String prop;

    @NotNull
    private String operator;

    /**
     * 对参数进行分组，不同分组之间是并的关系，组内是或的关系
     */
    private Integer tagid;
    /**
     * 条件的限定范围，只支持两种数据类型，数值和字符串，其他不考虑
     */
    @NotNull
    private Object limit;

    public ConditionBlock() {
    }

    public ConditionBlock(String prop, String operator, Object limit) {
        this.prop = prop;
        this.operator = operator;
        this.limit = limit;
    }

    public ConditionBlock(String prop, String operator, Object limit, Integer tagid) {
        this.prop = prop;
        this.operator = operator;
        this.limit = limit;
        this.tagid = tagid;
    }

    @Override
    public String toString() {
        return "ConditionBlock{" +
                "prop='" + prop + '\'' +
                ", operator='" + operator + '\'' +
                ", limit=" + limit +
                ", tagid=" + tagid +
                '}';
    }
}

package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionBlock {

    /**
     * 字段名称
     */
    private String prop;
    /**
     * 操作类型
     */
    private String operator;
    /**
     * 对参数进行分组，不同分组之间是并的关系，组内是或的关系
     */
    @JsonProperty("tagid")
    private int tagId;
    /**
     * 条件的限定范围，只支持两种数据类型，数值 字符串 数组
     */
    @JsonInclude
    private Object limit;

    public ConditionBlock() {
    }

    public ConditionBlock(String prop, String operator, Object limit) {
        this.prop = prop;
        this.operator = operator;
        this.limit = limit;
    }
}

package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ConditionBlockCompose {
    /**
     * 表达式列表
     */
    private List<ConditionBlock> expressions;
    /**
     * 查询条件composemethod为false(默认)使用先或后与(a or b or …) and (c or d or …) and …格式,
     * 通过tagid(>0)指定分组, tagid<=0 无分组.composemethod为true则先与后或
     */
    @JsonProperty("composemethod")
    private boolean composeMethod;

    public ConditionBlockCompose() {
        this.expressions = new ArrayList<>(4);
    }

}

package com.cet.eem.model.base;

import com.cet.eem.common.CommonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件组合
 *
 * @author CKai
 */
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
    private boolean composemethod;

    public ConditionBlockCompose() {
        this.expressions = new ArrayList<>(CommonUtils.MAP_INIT_SIZE_4);
    }


    public ConditionBlockCompose(List<ConditionBlock> filters) {
        this.expressions = filters;
    }

    public ConditionBlockCompose(List<ConditionBlock> filters, boolean composeMethod) {
        this.expressions = filters;
        this.composemethod = composeMethod;
    }

    @Override
    public String toString() {
        return "ConditionBlockCompose{" +
                "expressions=" + expressions +
                '}';
    }
}

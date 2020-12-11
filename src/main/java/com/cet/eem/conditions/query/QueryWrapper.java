package com.cet.eem.conditions.query;


import com.cet.eem.model.base.ConditionBlock;
import com.cet.eem.model.base.ConditionBlockCompose;
import com.cet.eem.model.model.IModel;
import com.cet.eem.toolkit.Assert;

import java.util.List;
import java.util.function.Consumer;

/**
 * Entity 对象封装操作类
 *
 * @author hubin miemie HCL
 * @since 2018-05-25
 */
@SuppressWarnings("serial")
public class QueryWrapper<T> extends AbstractQueryWrapper<T, String, QueryWrapper<T>> {


    public static <T extends IModel> QueryWrapper<T> of(Class<T> tClass) {
        return new QueryWrapper<>(tClass);
    }

    private QueryWrapper(Class<T> tClass) {
        super(tClass);
    }


    @Override
    public QueryWrapper<T> or() {
        ConditionBlockCompose filter = this.queryCondition.getFilter();
        List<ConditionBlock> expressions = filter.getExpressions();
        boolean composeMethod = filter.isComposemethod();
        if (expressions.size() > 1 && composeMethod) {
            throw new IllegalStateException("not allow use different logical connector");
        }
        filter.setComposemethod(false);
        return this;
    }

    @Override
    public QueryWrapper<T> and(Consumer<QueryWrapper<T>> consumer) {
        boolean composeMethod = this.queryCondition.getFilter().isComposemethod();
        Assert.isFalse(composeMethod, "not allow use different logical connector");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(this.tClass);
        queryWrapper.globalTagId = this.globalTagId + 1;
        consumer.accept(queryWrapper);
        List<ConditionBlock> expressions = queryWrapper.queryCondition.getFilter().getExpressions();
        this.queryCondition.getFilter().getExpressions().addAll(expressions);
        return this;
    }

    @Override
    public QueryWrapper<T> or(Consumer<QueryWrapper<T>> consumer) {
        boolean composeMethod = this.queryCondition.getFilter().isComposemethod();
        Assert.isTrue(composeMethod, "not allow use different logical connector");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(this.tClass);
        queryWrapper.globalTagId = this.globalTagId + 1;
        consumer.accept(queryWrapper);
        List<ConditionBlock> expressions = queryWrapper.getQueryCondition().getFilter().getExpressions();
        this.queryCondition.getFilter().getExpressions().addAll(expressions);
        return this;
    }

    @Override
    protected String keyToString(String s) {
        return s;
    }
}

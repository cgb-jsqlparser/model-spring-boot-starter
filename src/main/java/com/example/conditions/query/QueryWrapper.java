package com.example.conditions.query;

import com.example.model.dto.ConditionBlock;
import com.example.model.dto.ConditionBlockCompose;
import com.example.model.model.AbstractModelEntity;
import com.example.toolkit.Assert;

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


    public static <T extends AbstractModelEntity> QueryWrapper<T> of(Class<T> tClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.tClass = tClass;
        return queryWrapper;
    }

    private QueryWrapper() {
    }


    @Override
    public QueryWrapper<T> or() {
        ConditionBlockCompose filter = getFilter();
        List<ConditionBlock> expressions = filter.getExpressions();
        boolean composeMethod = filter.isComposeMethod();
        if (expressions.size() > 1 && composeMethod) {
            throw new IllegalStateException("not allow use different logical connector");
        }
        filter.setComposeMethod(false);
        return this;
    }

    @Override
    public QueryWrapper<T> and(Consumer<QueryWrapper<T>> consumer) {
        boolean composeMethod = getFilter().isComposeMethod();
        Assert.isFalse(composeMethod, "not allow use different logical connector");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.globalTagId = this.globalTagId + 1;
        consumer.accept(queryWrapper);
        List<ConditionBlock> expressions = queryWrapper.getFilter().getExpressions();
        this.getFilter().getExpressions().addAll(expressions);
        return this;
    }

    @Override
    public QueryWrapper<T> or(Consumer<QueryWrapper<T>> consumer) {
        boolean composeMethod = getFilter().isComposeMethod();
        Assert.isTrue(composeMethod, "not allow use different logical connector");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.globalTagId = this.globalTagId + 1;
        consumer.accept(queryWrapper);
        List<ConditionBlock> expressions = queryWrapper.getFilter().getExpressions();
        this.getFilter().getExpressions().addAll(expressions);
        return this;
    }

    @Override
    protected String keyToString(String s) {
        return s;
    }
}

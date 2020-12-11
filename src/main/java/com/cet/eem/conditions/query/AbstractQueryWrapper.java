/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cet.eem.conditions.query;


import com.cet.eem.conditions.Wrapper;
import com.cet.eem.conditions.interfaces.Compare;
import com.cet.eem.conditions.interfaces.Func;
import com.cet.eem.conditions.interfaces.Join;
import com.cet.eem.conditions.interfaces.Nested;
import com.cet.eem.metadata.TableInfoHelper;
import com.cet.eem.model.base.*;
import com.cet.eem.model.constant.ConditionOperator;
import com.cet.eem.toolkit.CollectionUtils;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * 查询条件封装
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class AbstractQueryWrapper<T, R, Children extends AbstractQueryWrapper<T, R, Children>> extends Wrapper<T>
        implements Compare<Children, R>, Nested<Children, Children>, Join<T, R, Children>, Func<Children, R>, Query<Children, R> {



    /**
     * 排序优先级
     */
    protected int orderPriority;

    /**
     * 全局tagId
     */
    protected int globalTagId = 1;

    public AbstractQueryWrapper(Class<T> tClass) {
        this.tClass = tClass;
        String modelLabel = TableInfoHelper.getModelLabel(tClass);
        this.queryCondition = new QueryCondition.Builder(modelLabel);
    }

    @Override
    public Children select(R... columns) {
        FlatQueryConditionDTO rootCondition = this.queryCondition.getRootCondition();
        List<String> columnsList = new ArrayList<>();
        for (R column : columns) {
            columnsList.add(keyToString(column));
        }
        rootCondition.setProps(columnsList);
        return (Children) this;
    }

    @Override
    public <V> Children allEq(Map<R, V> params, boolean null2IsNull) {
        if (CollectionUtils.isNotEmpty(params)) {
            for (Map.Entry<R, V> stringVEntry : params.entrySet()) {
                R key = stringVEntry.getKey();
                V value = stringVEntry.getValue();
                if (Objects.isNull(value) && null2IsNull) {
                    continue;
                }
                eq(key, value);
            }
        }
        return (Children) this;
    }

    @Override
    public <V> Children allEq(BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        return null;
    }

    @Override
    public Children eq(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.EQ.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children ne(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.NE.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children gt(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.GT.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children ge(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.GE.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children lt(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LT.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children le(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LE.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children like(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LIKE.getValue(), val);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().addAll(Arrays.asList(conditionBlock, conditionBlock));
        return (Children) this;
    }

    @Override
    public Children in(R column, Collection<?> coll) {
        ConditionBlockCompose conditionBlockCompose = this.queryCondition.getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.IN.getValue(), coll);
        conditionBlock.setTagid(globalTagId);
        conditionBlockCompose.getExpressions().addAll(Arrays.asList(conditionBlock, conditionBlock));
        return (Children) this;
    }


    @Override
    public Children orderBy(boolean isAsc, R... columns) {
        String orderType = isAsc ? "asc" : "desc";
        List<Order> orders = this.queryCondition.getOrders();
        for (R column : columns) {
            Order order = new Order(keyToString(column), orderType, orderPriority++);
            orders.add(order);
        }
        return (Children) this;
    }

    @Override
    public Children or() {
        ConditionBlockCompose filter = this.queryCondition.getFilter();
        List<ConditionBlock> expressions = filter.getExpressions();
        boolean composeMethod = filter.isComposemethod();
        if (expressions.size() > 1 && composeMethod) {
            throw new IllegalStateException("not allow use different logical connector");
        }
        filter.setComposemethod(false);
        return (Children) this;
    }


    @Override
    public Children join(Supplier<? extends AbstractQueryWrapper<T, R, Children>> supplier) {
        AbstractQueryWrapper<T, R, Children> abstractQueryWrapper = supplier.get();
        FlatQueryConditionDTO rootCondition = abstractQueryWrapper.getQueryCondition().getRootCondition();
        ConditionBlockCompose filter = rootCondition.getFilter();
        if (filter != null) {
            List<SingleModelConditionDTO> subLayerConditions = this.queryCondition.getSubLayer();
            SingleModelConditionDTO singleModelConditionDTO = new SingleModelConditionDTO();
            singleModelConditionDTO.setFilter(filter);
            singleModelConditionDTO.setModelLabel(null);
            singleModelConditionDTO.setProps(rootCondition.getProps());
            subLayerConditions.add(singleModelConditionDTO);
        }
        return (Children) this;
    }

    protected abstract String keyToString(R r);
}

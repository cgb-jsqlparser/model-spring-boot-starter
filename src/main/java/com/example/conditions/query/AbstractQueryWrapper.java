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
package com.example.conditions.query;


import com.example.conditions.Wrapper;
import com.example.conditions.interfaces.Compare;
import com.example.conditions.interfaces.Func;
import com.example.conditions.interfaces.Join;
import com.example.conditions.interfaces.Nested;
import com.example.model.constant.ConditionOperator;
import com.example.model.dto.*;
import com.example.model.model.AbstractModelEntity;
import com.example.toolkit.CollectionUtils;

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
     * 对应实体的实体类
     */
    protected Class<T> tClass;


    /**
     * 查询条件
     */
    protected QueryCondition queryCondition = null;

    /**
     * 排序优先级
     */
    protected int orderPriority;

    /**
     * 全局tagId
     */
    protected int globalTagId = 1;

    public AbstractQueryWrapper() {
        this.queryCondition = new QueryCondition();
        this.paramMap.put("QUERY", queryCondition);
    }

    @Override
    public Children select(R... columns) {
        FlatQueryConditionDTO rootCondition = getRootCondition();
        List<String> columnsList = new ArrayList<>();
        for (R column : columns) {
            columnsList.add(keyToString(column));
        }
        rootCondition.setProps(columnsList);
        return (Children) this;
    }

    protected FlatQueryConditionDTO getRootCondition() {
        FlatQueryConditionDTO flatQueryConditionDTO = this.queryCondition.getRootCondition();
        if (flatQueryConditionDTO == null) {
            flatQueryConditionDTO = new FlatQueryConditionDTO();
            this.queryCondition.setRootCondition(flatQueryConditionDTO);
        }
        return flatQueryConditionDTO;
    }

    protected ConditionBlockCompose getFilter() {
        FlatQueryConditionDTO rootCondition = getRootCondition();
        ConditionBlockCompose filter = rootCondition.getFilter();
        if (filter == null) {
            filter = new ConditionBlockCompose();
            filter.setComposeMethod(true);
            rootCondition.setFilter(filter);
        }

        return filter;
    }

    protected List<GroupBy> getGroupBys() {
        FlatQueryConditionDTO rootCondition = getRootCondition();
        List<GroupBy> groupbys = rootCondition.getGroupbys();
        if (groupbys == null) {
            groupbys = new ArrayList<>(4);
            rootCondition.setGroupbys(groupbys);
        }
        return groupbys;
    }

    protected List<Order> getOrders() {
        FlatQueryConditionDTO rootCondition = getRootCondition();
        List<Order> orders = rootCondition.getOrders();
        if (orders == null) {
            orders = new ArrayList<>(4);
            rootCondition.setOrders(orders);
        }
        return orders;
    }

    protected List<SingleModelConditionDTO> getSubLayerConditions() {
        List<SingleModelConditionDTO> singleModelConditionDTOList = this.queryCondition.getSubLayerConditions();
        if (singleModelConditionDTOList == null) {
            singleModelConditionDTOList = new ArrayList<>(8);
            this.queryCondition.setSubLayerConditions(singleModelConditionDTOList);
        }
        return singleModelConditionDTOList;
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
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.EQ.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children ne(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.NE.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children gt(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.GT.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children ge(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.GE.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children lt(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LT.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children le(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LE.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().add(conditionBlock);
        return (Children) this;
    }

    @Override
    public Children like(R column, Object val) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.LIKE.getValue(), val);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().addAll(Arrays.asList(conditionBlock, conditionBlock));
        return (Children) this;
    }

    @Override
    public Children in(R column, Collection<?> coll) {
        ConditionBlockCompose conditionBlockCompose = getFilter();
        ConditionBlock conditionBlock = new ConditionBlock(keyToString(column), ConditionOperator.IN.getValue(), coll);
        conditionBlock.setTagId(globalTagId);
        conditionBlockCompose.getExpressions().addAll(Arrays.asList(conditionBlock, conditionBlock));
        return (Children) this;
    }


    @Override
    public Children orderBy(boolean isAsc, R... columns) {
        String orderType = isAsc ? "asc" : "desc";
        List<Order> orders = getOrders();
        for (R column : columns) {
            Order order = new Order(keyToString(column), orderType, orderPriority++);
            orders.add(order);
        }
        return (Children) this;
    }

    @Override
    public Children or() {
        ConditionBlockCompose filter = getFilter();
        List<ConditionBlock> expressions = filter.getExpressions();
        boolean composeMethod = filter.isComposeMethod();
        if (expressions.size() > 1 && composeMethod) {
            throw new IllegalStateException("not allow use different logical connector");
        }
        filter.setComposeMethod(false);
        return (Children) this;
    }


    @Override
    public Children join(Supplier<? extends AbstractQueryWrapper<T, R, Children>> supplier) {
        AbstractQueryWrapper<T, R, Children> abstractQueryWrapper = supplier.get();
        FlatQueryConditionDTO rootCondition = abstractQueryWrapper.getRootCondition();
        ConditionBlockCompose filter = rootCondition.getFilter();
        if (filter != null) {
            List<SingleModelConditionDTO> subLayerConditions = this.getSubLayerConditions();
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

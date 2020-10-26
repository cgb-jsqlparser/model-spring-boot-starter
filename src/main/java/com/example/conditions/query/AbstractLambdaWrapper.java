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


import com.example.exceptions.ModelAdapterException;
import com.example.toolkit.Assert;
import com.example.toolkit.LambdaUtils;
import com.example.toolkit.PropertyNamer;
import com.example.toolkit.support.ColumnCache;
import com.example.toolkit.support.SFunction;
import com.example.toolkit.support.SerializedLambda;

import java.util.Map;

/**
 * Lambda 语法使用 Wrapper
 * <p>统一处理解析 lambda 获取 column</p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("serial")
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
        extends AbstractQueryWrapper<T, SFunction<T, ?>, Children> {

    private Map<String, ColumnCache> columnMap = null;
    private boolean initColumnMap = false;


    public AbstractLambdaWrapper() {
    }

    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @param lambda lambda 表达式
     * @return 列
     * @throws ModelAdapterException 获取不到列信息时抛出异常
     * @see SerializedLambda#getImplClass()
     * @see SerializedLambda#getImplMethodName()
     */
    public String getColumn(SerializedLambda lambda) throws ModelAdapterException {
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Class<?> aClass = lambda.getInstantiatedType();
        if (!initColumnMap) {
            columnMap = LambdaUtils.getColumnMap(aClass);
            initColumnMap = true;
        }
        Assert.notNull(columnMap, "can not find lambda cache for this entity [%s]", aClass.getName());
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
                fieldName, aClass.getName());
        return columnCache.getModelColumn();
    }
}
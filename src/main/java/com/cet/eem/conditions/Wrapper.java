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
package com.cet.eem.conditions;


import com.cet.eem.metadata.TableFieldInfo;
import com.cet.eem.metadata.TableInfoHelper;
import com.cet.eem.model.base.QueryCondition;
import com.cet.eem.toolkit.ReflectionKit;
import lombok.Getter;

import java.util.Objects;

/**
 * 条件构造抽象类
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("all")
public abstract class Wrapper<T> {

    /**
     * 对应实体的实体类
     */
    protected Class<T> tClass;

    @Getter
    protected QueryCondition.Builder queryCondition = null;

    /**
     * 获取操作的模型名
     *
     * @return
     */
    public String getModelLabel() {
        if (Objects.isNull(tClass)) {
            return null;
        }
        return TableInfoHelper.getModelLabel(tClass);
    }

    /**
     * 根据实体FieldStrategy属性来决定判断逻辑
     */
    private boolean fieldStrategyMatch(T entity, TableFieldInfo e) {
        switch (e.getWhereStrategy()) {
            case NULL_EXCLUDE:
                return Objects.nonNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
            case NULL_INCLUDE:
                return Objects.isNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
            default:
                return Objects.nonNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
        }
    }
}
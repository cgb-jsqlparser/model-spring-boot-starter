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
package com.cet.eem.conditions.interfaces;

import java.io.Serializable;
import java.util.Collection;

/**
 * 查询条件封装
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("unchecked")
public interface Func<Children, R> extends Serializable {

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * <p>例: in("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 如果集合为 empty 则不会进行 sql 拼接 </li>
     *
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    Children in(R column, Collection<?> coll);

    /**
     * ignore
     */
    default Children orderByAsc(R... columns) {
        return orderBy(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     * <p>例: orderByDesc("id", "name")</p>
     *
     * @param columns   字段数组
     * @return children
     */
    default Children orderByDesc(R... columns) {
        return orderBy(false, columns);
    }

    /**
     * 排序：ORDER BY 字段, ...
     * <p>例: orderBy(true, "id", "name")</p>
     *
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段数组
     * @return children
     */
    Children orderBy(boolean isAsc, R... columns);
}

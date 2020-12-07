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

import com.cet.eem.conditions.query.AbstractQueryWrapper;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 查询条件封装
 * <p>拼接</p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public interface Join<T, R, Children extends AbstractQueryWrapper<T, R, Children>> extends Serializable {

    /**
     * join 嵌套
     * <p>
     * 例: or(i -&gt; i.eq("name", "李白").ne("status", "活着"))
     * </p>
     *
     * @param supplier 消费函数
     * @return children
     */
    Children join(Supplier<? extends AbstractQueryWrapper<T, R, Children>> supplier);

}

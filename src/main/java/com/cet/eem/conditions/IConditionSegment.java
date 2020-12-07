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


import com.cet.eem.common.model.QueryCondition;

/**
 * 条件片段接口
 *
 * @author zhangh
 * @since 2020-09-16
 */
@FunctionalInterface
public interface IConditionSegment {

    /**
     * 把条件片段添加到条件中
     *
     * @param queryCondition 查询条件
     */
    void addSegmentToCondition(QueryCondition queryCondition);
}

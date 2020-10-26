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
package com.example.metadata;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据库表反射信息
 *
 * @author hubin
 * @since 2016-01-23
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
public class TableInfo {

    /**
     * 实体类型
     */
    private Class<?> entityType;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表字段信息列表
     */
    private List<TableFieldInfo> fieldList;

    /**
     * 从属于该模型下的模型的模型名
     */
    private List<String> ownModelNameList;


    public TableInfo(Class<?> entityType) {
        this.entityType = entityType;
    }
}

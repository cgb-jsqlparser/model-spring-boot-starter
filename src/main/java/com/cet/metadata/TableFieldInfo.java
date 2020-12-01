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
package com.cet.metadata;

import com.cet.annotation.FieldStrategy;
import com.cet.annotation.ModelFieldStrategy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * 数据库表字段反射信息
 *
 * @author hubin sjy willenfoo tantan
 * @since 2016-09-09
 */
@Getter
@ToString
@EqualsAndHashCode
public class TableFieldInfo {

    /**
     * 属性
     *
     * @since 3.3.1
     */
    private final Field field;
    /**
     * 数据库字段名
     */
    private final String column;
    /**
     * 属性名
     */
    private final String property;
    /**
     * 属性类型
     */
    private final Class<?> propertyType;

    /**
     * 字段验证策略之 insert
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy insertStrategy;
    /**
     * 字段验证策略之 update
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy updateStrategy;
    /**
     * 字段验证策略之 where
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy whereStrategy;

    /**
     * 全新的 存在 TableField 注解时使用的构造函数
     */
    @SuppressWarnings("unchecked")
    public TableFieldInfo(String column, Field field) {
        field.setAccessible(true);
        this.field = field;
        this.property = field.getName();
        this.propertyType = field.getType();
        this.column = column;
        boolean annotationPresent = field.isAnnotationPresent(ModelFieldStrategy.class);
        if (annotationPresent) {
            ModelFieldStrategy annotation = field.getAnnotation(ModelFieldStrategy.class);
            this.insertStrategy = annotation.insertStrategy();
            this.updateStrategy = annotation.updateStrategy();
            this.whereStrategy = annotation.whereStrategy();
        } else {
            this.insertStrategy = FieldStrategy.NULL_EXCLUDE;
            this.updateStrategy = FieldStrategy.NULL_EXCLUDE;
            this.whereStrategy = FieldStrategy.NULL_EXCLUDE;
        }
    }
}
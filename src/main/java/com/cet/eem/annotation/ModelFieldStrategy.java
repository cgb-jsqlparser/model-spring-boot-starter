package com.cet.eem.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ModelFieldStrategy {

    /**
     * 插入字段策略
     *
     * @return
     */
    FieldStrategy insertStrategy() default FieldStrategy.NULL_EXCLUDE;

    /**
     * 更新字段策略
     *
     * @return
     */
    FieldStrategy updateStrategy() default FieldStrategy.NULL_EXCLUDE;

    /**
     * 查询where条件策略
     *
     * @return
     */
    FieldStrategy whereStrategy() default FieldStrategy.NULL_EXCLUDE;
}

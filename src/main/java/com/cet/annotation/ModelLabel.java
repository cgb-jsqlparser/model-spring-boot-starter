package com.cet.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ModelLabel {

    /**
     * moedlLabel名称
     *
     * @return
     */
    String value() default "";
}

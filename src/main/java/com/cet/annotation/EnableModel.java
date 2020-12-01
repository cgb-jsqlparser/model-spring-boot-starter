package com.cet.annotation;

import com.cet.scanner.MetadataRegistrar;
import com.cet.scanner.ModelDaoRegistrar;
import com.cet.scanner.PackageInfo;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({PackageInfo.class, ModelDaoRegistrar.class, MetadataRegistrar.class})
public @interface EnableModel {

    /**
     * 扫描接口的路径
     *
     * @return
     */
    String[] value() default {};
}

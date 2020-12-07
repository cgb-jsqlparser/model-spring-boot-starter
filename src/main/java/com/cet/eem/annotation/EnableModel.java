package com.cet.eem.annotation;

import com.cet.eem.scanner.MetadataRegistrar;
import com.cet.eem.scanner.ModelDaoRegistrar;
import com.cet.eem.scanner.PackageInfo;
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

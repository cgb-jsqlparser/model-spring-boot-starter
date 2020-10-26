package com.example.annotation;

import com.example.scanner.MetadataRegistrar;
import com.example.scanner.ModelDaoRegistrar;
import com.example.scanner.PackageInfo;
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

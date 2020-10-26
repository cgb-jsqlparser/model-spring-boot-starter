package com.example.scanner;

import com.example.annotation.EnableModel;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PackageInfo implements ImportBeanDefinitionRegistrar {

    private static Set<String> basePackages;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        basePackages = getBasePackages(importingClassMetadata);
    }

    public static Set<String> getBasePackages() {
        return basePackages;
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableModel.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }
}

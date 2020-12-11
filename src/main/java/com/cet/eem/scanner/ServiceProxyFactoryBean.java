package com.cet.eem.scanner;

import com.cet.eem.model.feign.ModelDataService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName : ServiceProxyFactoryBean
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-09 17:56
 */
public class ServiceProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaces;

    /**
     * 使用@Repository直接托管给spring的数据访问层
     */
    private final T springRepository;

    private final ModelDataService modelDataService;

    @SuppressWarnings("unchecked")
    public ServiceProxyFactoryBean(String interfacesClassName, ApplicationContext applicationContext) {
        try {
            this.interfaces = (Class<T>) Class.forName(interfacesClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, T> beansOfType = applicationContext.getBeansOfType(interfaces);
        springRepository = beansOfType.values().stream().findAny().orElse(null);
        modelDataService = applicationContext.getBean(ModelDataService.class);
        Assert.notNull(modelDataService, "ModelDataService Fegin Client Not Found");
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T getObject() {
        if (Objects.nonNull(springRepository)) {
            return springRepository;
        }
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces},
                new ServiceProxy(interfaces, modelDataService));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaces;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

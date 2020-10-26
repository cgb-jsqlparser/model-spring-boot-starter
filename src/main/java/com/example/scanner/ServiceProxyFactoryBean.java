package com.example.scanner;

import com.example.model.feign.ModelDataService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @ClassName : ServiceProxyFactoryBean
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-09 17:56
 */
public class ServiceProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaces;

    private T extend;

    private ModelDataService modelDataService;

    public ServiceProxyFactoryBean(String interfacesClassName, ApplicationContext applicationContext) {
        try {
            this.interfaces = (Class<T>) Class.forName(interfacesClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, T> beansOfType = applicationContext.getBeansOfType(interfaces);
        extend = beansOfType.values().stream().findAny().orElse(null);
        modelDataService = applicationContext.getBean(ModelDataService.class);
        Assert.notNull(modelDataService, "ModelDataService Fegin Client Not Found");
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        if (extend != null) {
            return extend;
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

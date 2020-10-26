package com.example.scanner;

import com.example.dao.ModelDaoImpl;
import com.example.model.feign.ModelDataService;
import com.example.model.model.AbstractModelEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName : ServiceProxy
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-09 17:56
 */
public class ServiceProxy<T extends AbstractModelEntity> implements InvocationHandler {
    private Class<T> interfaces;

    private ModelDataService modelDataService;

    ServiceProxy(Class<T> interfaces, ModelDataService modelDataService) {
        this.interfaces = interfaces;
        this.modelDataService = modelDataService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(interfaces)) {
            return method.getName();
        } else {
            return method.invoke(new ModelDaoImpl<T>(interfaces, modelDataService), args);
        }
    }
}
package com.cet.scanner;

import com.cet.dao.ModelDaoImpl;
import com.cet.eem.common.feign.ModelDataService;
import com.cet.model.model.IModel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName : ServiceProxy
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-09 17:56
 */
public class ServiceProxy<T extends IModel> implements InvocationHandler {

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
package com.cet.eem.scanner;

import com.cet.eem.dao.ModelDaoImpl;
import com.cet.eem.common.feign.ModelDataService;
import com.cet.eem.model.feign.ModelDataServiceSubstitution;
import com.cet.eem.model.model.IModel;

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

    private ModelDataServiceSubstitution modelDataService;

    ServiceProxy(Class<T> interfaces, ModelDataServiceSubstitution modelDataService) {
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
package com.example.scanner;

import com.example.metadata.TableInfoHelper;
import com.example.model.feign.ModelDataService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @ClassName : MetadataRegistrar
 * @Description :
 * @Author : zhangh
 * @Date: 2020-10-14 17:36
 */
public class MetadataRegistrar implements BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        initTableInfoHelper(beanFactory);
    }

    private void initTableInfoHelper(BeanFactory beanFactory) {
        ModelDataService modelDataService = beanFactory.getBean(ModelDataService.class);
        TableInfoHelper.init(modelDataService);
    }
}

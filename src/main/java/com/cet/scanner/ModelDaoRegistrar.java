package com.cet.scanner;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName : ModelDaoRegistrar
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-18 12:57
 */
public class ModelDaoRegistrar implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ServiceInterfacesScanner serviceInterfacesScanner = new ServiceInterfacesScanner(beanDefinitionRegistry, applicationContext);
        serviceInterfacesScanner.doScan(PackageInfo.getBasePackages().stream().toArray(String[]::new));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //do nothing
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}

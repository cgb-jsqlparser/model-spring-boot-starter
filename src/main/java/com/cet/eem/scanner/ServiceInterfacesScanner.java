package com.cet.eem.scanner;

import com.cet.eem.dao.BaseModelDao;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Arrays;
import java.util.Set;

/**
 * @ClassName : ServiceInterfacesScanner
 * @Description :
 * @Author : zhangh
 * @Date: 2020-09-09 17:57
 */
public class ServiceInterfacesScanner extends ClassPathBeanDefinitionScanner {

    private ApplicationContext applicationContext;

    public ServiceInterfacesScanner(BeanDefinitionRegistry registry, ApplicationContext applicationContext) {
        //false表示不使用ClassPathBeanDefinitionScanner默认的TypeFilter
        super(registry, false);
        this.applicationContext = applicationContext;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        this.addFilter();
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            throw new NullPointerException("No interfaces");
        }
        this.createBeanDefinition(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    /**
     * 只扫描顶级接口
     *
     * @param beanDefinition bean定义
     * @return boolean
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        String[] interfaceNames = metadata.getInterfaceNames();
        return metadata.isInterface() && metadata.isIndependent() && Arrays.asList(interfaceNames).contains(BaseModelDao.class.getName());
    }

    /**
     * 扫描所有类
     */
    private void addFilter() {
        AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(BaseModelDao.class);
        addIncludeFilter(assignableTypeFilter);
    }

    /**
     * 为扫描到的接口创建代理对象
     *
     * @param beanDefinitionHolders beanDefinitionHolders
     */
    private void createBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            GenericBeanDefinition beanDefinition = ((GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition());
            //将bean的真实类型改变为FactoryBean
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(applicationContext);
            beanDefinition.setBeanClass(ServiceProxyFactoryBean.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

}

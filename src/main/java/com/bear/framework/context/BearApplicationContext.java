package com.bear.framework.context;

import com.bear.framework.beans.BearBeanFactory;
import com.bear.framework.beans.BearBeanWrapper;
import com.bear.framework.beans.config.BearBeanDefinition;
import com.bear.framework.beans.support.BearBeanDefinitionReader;
import com.bear.framework.beans.support.BearDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * IOC
 */
public class BearApplicationContext extends BearDefaultListableBeanFactory implements BearBeanFactory {

    private String[] configLocations;

    private BearBeanDefinitionReader reader;

    BearApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
    }

    @Override
    protected void refresh() {
        // 1、定位配置文件
        reader = new BearBeanDefinitionReader(this.configLocations);

        // 2、加载配置文件， 扫描相关的类，把他们封装成BearBeanDefinition
        List<BearBeanDefinition> beanDefinitionList = reader.loadBeanDefinitions();

        // 3、注册，吧配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitionList);

        // 4、把不是延时加载的类，提前初始化
        doAutowired();
    }

    // 只处理非延时加载的情况
    private void doAutowired() {
        for (Map.Entry<String, BearBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInt()) {
                getBean(beanName);
            }
        }
    }

    private void doRegisterBeanDefinition(List<BearBeanDefinition> beanDefinitionList) {
        for (BearBeanDefinition beanDefinition : beanDefinitionList) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) {
        // 1、初始化
        instanceBean(beanName, new BearBeanDefinition());

        // 2、注入
        populateBean(beanName, new BearBeanDefinition(), new BearBeanWrapper());

        return null;
    }

    private void populateBean(String beanName, BearBeanDefinition bearBeanDefinition, BearBeanWrapper bearBeanWrapper) {

    }

    private void instanceBean(String beanName, BearBeanDefinition bearBeanDefinition) {

    }
}

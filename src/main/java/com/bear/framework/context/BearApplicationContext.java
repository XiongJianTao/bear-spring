package com.bear.framework.context;

import com.bear.framework.beans.BearBeanFactory;
import com.bear.framework.beans.BearBeanWrapper;
import com.bear.framework.beans.config.BearBeanDefinition;
import com.bear.framework.beans.support.BearBeanDefinitionReader;
import com.bear.framework.beans.support.BearDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC
 */
public class BearApplicationContext extends BearDefaultListableBeanFactory implements BearBeanFactory {

    private String[] configLocations;

    private BearBeanDefinitionReader reader;

    // 单例的IOC容器缓存
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    //通用的IOC容器
    private Map<String, BearBeanWrapper> factoryBeanInstance = new ConcurrentHashMap<>();

    BearApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<BearBeanDefinition> beanDefinitionList) {
        for (BearBeanDefinition beanDefinition : beanDefinitionList) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        // 1、初始化
        BearBeanWrapper bearBeanWrapper = instanceBean(beanName, new BearBeanDefinition());

        // 2、拿到BearBeanWrapper之后，把BearBeanWrapper
//        if (this.factoryBeanInstance.containsKey(beanName)) {
//            throw new Exception("The " + beanName + "is exists");
//        }
        this.factoryBeanInstance.put(beanName, bearBeanWrapper);

        // 3、注入
        populateBean(beanName, new BearBeanDefinition(), bearBeanWrapper);

        return null;
    }

    private void populateBean(String beanName, BearBeanDefinition bearBeanDefinition, BearBeanWrapper bearBeanWrapper) {

    }

    private BearBeanWrapper instanceBean(String beanName, BearBeanDefinition bearBeanDefinition) {
        // 1、拿到要實例化的對象的类名
        String className = bearBeanDefinition.getBeanClassName();
        // 2、反射实例化，得到一个对象
        Object instance = null;
        try {
            // 假设默认是单例
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonObjects.put(className, instance);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        // 3、把这个对象封装到BeanWrapped中
        BearBeanWrapper beanWrapper = new BearBeanWrapper(instance);

        // 4、把BearBeanWrapper存到IOC容器中

        return beanWrapper;
    }
}

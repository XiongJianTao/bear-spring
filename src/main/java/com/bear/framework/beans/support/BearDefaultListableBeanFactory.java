package com.bear.framework.beans.support;

import com.bear.framework.beans.config.BearBeanDefinition;
import com.bear.framework.context.BearAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bear
 * @version 1.0
 * @className BearDefaultListAbleBeanFactory
 * @description TODO
 * @date 2020/8/26 16:47
 */
public class BearDefaultListableBeanFactory extends BearAbstractApplicationContext {

    // 存储注册信息的BearBeanDefinition
    public final Map<String, BearBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    protected void refresh() {

    }
}

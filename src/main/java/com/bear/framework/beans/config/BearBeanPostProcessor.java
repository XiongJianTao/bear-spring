package com.bear.framework.beans.config;

/**
 * @author bear
 * @version 1.0
 * @className BearBeanPostProcessor
 * @description TODO
 * @date 2020/8/29 22:44
 */
public class BearBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}

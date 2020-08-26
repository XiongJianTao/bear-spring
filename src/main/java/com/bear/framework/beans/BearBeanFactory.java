package com.bear.framework.beans;

/**
 * 单例模式的顶层设计
 */
public interface BearBeanFactory {
    /**
     * 根据beanName从IOC容器中获得一个实例bean
     *
     * @param beanName 实例名称
     * @return
     */
    Object getBean(String beanName);
}

package com.bear.framework.beans.config;

import lombok.Data;

/**
 * @author bear
 * @version 1.0
 * @className BearBeanDefinition
 * @description 注册信息
 * @date 2020/8/26 16:51
 */
@Data
public class BearBeanDefinition {

    private String beanClassName;

    private boolean lazyInt = false;

    private String factoryBeanName;

    private boolean isSingleton = true;

}

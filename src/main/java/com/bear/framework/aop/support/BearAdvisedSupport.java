package com.bear.framework.aop.support;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bear
 * @version 1.0
 * @className BearAdvisedSupport
 * @description TODO
 * @date 2020/9/12 21:41
 */
public class BearAdvisedSupport {

    private Class<?> targetClass;

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return null;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?>  targetClass) {
        return null;
    }
}

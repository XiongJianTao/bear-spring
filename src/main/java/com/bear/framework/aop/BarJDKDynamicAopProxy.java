package com.bear.framework.aop;

import com.bear.framework.aop.intercept.BearMethodInvocation;
import com.bear.framework.aop.support.BearAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bear
 * @version 1.0
 * @className JDKDynamicAopProxy
 * @description TODO
 * @date 2020/9/12 21:36
 */
public class BarJDKDynamicAopProxy implements BearAopProxy, InvocationHandler {

    private BearAdvisedSupport advised;


    public BarJDKDynamicAopProxy(BearAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        BearMethodInvocation invocation = new BearMethodInvocation(proxy, null, method, args, this.advised.getTargetClass(), null);
        return invocation.proceed();
    }
}

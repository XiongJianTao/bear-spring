package com.bear.framework.aop;

/**
 * @author bear
 * @version 1.0
 * @className BearAopProxy
 * @description TODO
 * @date 2020/9/12 21:36
 */
public interface BearAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}

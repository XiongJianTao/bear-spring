package com.bear.framework.aop.intercept;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author bear
 * @version 1.0
 * @className BearMethodInvocarion
 * @description TODO
 * @date 2020/9/12 21:48
 */
public class BearMethodInvocation {

    public Object proceed() throws Throwable {
        return null;
    }

    public BearMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                                   Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

    }
}

package com.bear.framework.aop.intercept;

public interface BearMethodInterceptor {

    Object invoke(BearMethodInvocation invocation) throws Throwable;
}

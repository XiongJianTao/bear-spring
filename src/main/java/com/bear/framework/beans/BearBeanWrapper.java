package com.bear.framework.beans;

/**
 * @author bear
 * @version 1.0
 * @className BearBeanWrapper
 * @description TODO
 * @date 2020/8/26 18:29
 */
public class BearBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public BearBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public BearBeanWrapper(Object wrappedInstance, Class<?> wrappedClass) {
        this.wrappedInstance = wrappedInstance;
        this.wrappedClass = wrappedClass;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedClass;
    }
}

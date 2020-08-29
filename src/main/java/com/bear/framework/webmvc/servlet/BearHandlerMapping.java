package com.bear.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Handler;
import java.util.regex.Pattern;

/**
 * @author bear
 * @version 1.0
 * @className BearHandlerMapping
 * @description TODO
 * @date 2020/8/29 23:27
 */
public class BearHandlerMapping {
    // 保存对应方法的实例
    protected  Object controller;
    // 保存映射的方法
    protected Method method;
    // url正则匹配
    protected Pattern pattern;
    // 参数顺序
    protected Map<String, Integer> paramIndexMapping;

    protected BearHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}

package com.bear.mvcframework.v1.servlet;

import com.bear.mvcframework.v1.annotation.BearRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HandlerMapping<T> {
        private String url;
        private Method method;
        private Class<?>[] paramTypes;
        private T Controller;

        // 形参列表 参数名字作为key，参数位置作为值
        private Map<String, Integer> paramIndexMapping;

        public HandlerMapping(String url, T controller, Method method) {
            this.url = url;
            this.method = method;
            this.Controller = controller;
            this.paramTypes = method.getParameterTypes();
            this.paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation annotation : pa[i]) {
                    if (annotation instanceof BearRequestParam) {
                        String paramName = ((BearRequestParam) annotation).value();
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                    paramIndexMapping.put(parameterType.getName(), i);
                }
            }
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public T getController() {
            return Controller;
        }

        public void setController(T controller) {
            Controller = controller;
        }

        public Map<String, Integer> getParamIndexMapping() {
            return paramIndexMapping;
        }

        public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
            this.paramIndexMapping = paramIndexMapping;
        }

        public Class<?>[] getParamTypes() {
            return paramTypes;
        }

        public void setParamTypes(Class<?>[] paramTypes) {
            this.paramTypes = paramTypes;
        }
    }
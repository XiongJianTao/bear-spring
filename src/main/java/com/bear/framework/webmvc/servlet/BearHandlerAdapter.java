package com.bear.framework.webmvc.servlet;

import com.bear.framework.annotation.BearRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bear
 * @version 1.0
 * @className BearHandlerAdapter
 * @description TODO
 * @date 2020/8/29 23:46
 */
public class BearHandlerAdapter {

    boolean supports(Object handler) {
        return handler instanceof BearHandlerMapping;
    }

    BearModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BearHandlerMapping handlerMapping = (BearHandlerMapping) handler;

        // 把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if (annotation instanceof BearRequestParam) {
                    String paramName = ((BearRequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        // 提取方法中的request和response参数
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }

        // 获得方法的形参列表
        Map<String, String[]> params = request.getParameterMap();
        // 实参列表
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!handlerMapping.paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            Integer index = handlerMapping.paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(parameterTypes[index],value);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            Integer index = handlerMapping.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = request;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            Integer index = handlerMapping.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = response;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == BearModelAndView.class;
        if (isModelAndView) {
            return (BearModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(Class<?> parameterType, String value) {
        return parameterType.cast(value);
    }

}

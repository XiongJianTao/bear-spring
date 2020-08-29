package com.bear.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        return null;
    }

}

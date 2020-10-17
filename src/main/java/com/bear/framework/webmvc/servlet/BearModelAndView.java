package com.bear.framework.webmvc.servlet;

import java.util.Map;

/**
 * @author bear
 * @version 1.0
 * @className BearModelAndView
 * @description TODO
 * @date 2020/8/29 23:51
 */
public class BearModelAndView {

    private String view;

    private Map<String, ?> model;

    public BearModelAndView(String view) {
        this.view = view;
    }

    public BearModelAndView(String view, Map<String, ?> model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}

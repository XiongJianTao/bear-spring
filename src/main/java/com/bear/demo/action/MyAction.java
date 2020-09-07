package com.bear.demo.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bear.demo.service.IModifyService;
import com.bear.demo.service.IQueryService;
import com.bear.framework.annotation.BearAutowired;
import com.bear.framework.annotation.BearController;
import com.bear.framework.annotation.BearRequestMapping;
import com.bear.framework.annotation.BearRequestParam;
import com.bear.framework.webmvc.servlet.BearModelAndView;

/**
 * 公布接口url
 *
 * @author Tom
 */
@BearController
@BearRequestMapping("/web")
public class MyAction {

    @BearAutowired
    IQueryService queryService;
    @BearAutowired
    IModifyService modifyService;

    @BearRequestMapping("/query.json")
    public BearModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                  @BearRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response, result);
    }

    @BearRequestMapping("/add*.json")
    public BearModelAndView add(HttpServletRequest request, HttpServletResponse response,
                                @BearRequestParam("name") String name, @BearRequestParam("addr") String addr) {
        String result = null;
        try {
            result = modifyService.add(name, addr);
            return out(response, result);
        } catch (Exception e) {
//			e.printStackTrace();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("detail", e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new BearModelAndView("500", model);
        }

    }

    @BearRequestMapping("/remove.json")
    public BearModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                   @BearRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @BearRequestMapping("/edit.json")
    public BearModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                                 @BearRequestParam("id") Integer id,
                                 @BearRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }


    private BearModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.bear.mvc.controller;

import com.bear.mvcframework.v1.annotation.BearController;
import com.bear.mvcframework.v1.annotation.BearRequestMapping;
import com.bear.mvcframework.v1.annotation.BearRequestParam;

@BearController
public class HelloController {

    @BearRequestMapping("/hello")
    public String Hello(@BearRequestParam("name") String name) {
        return "Hello World" + name;
    }

    @BearRequestMapping("/add")
    public Integer add(@BearRequestParam("a") Integer a, @BearRequestParam("b") Integer b) {
        return a + b;
    }
}

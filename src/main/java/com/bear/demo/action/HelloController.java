package com.bear.demo.action;

import com.bear.framework.annotation.BearController;
import com.bear.framework.annotation.BearRequestMapping;

/**
 * @author bear
 * @version 1.0
 * @className HelloController
 * @description TODO
 * @date 2020/8/29 10:26
 */
@BearController
public class HelloController {

    @BearRequestMapping("/hello")
    public String hello() {
        return "hello world";
    }
}

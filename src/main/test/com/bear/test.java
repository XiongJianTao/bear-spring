package com.bear;

import com.bear.framework.context.BearApplicationContext;

public class test {
    public static void main(String[] args) throws Exception {
        BearApplicationContext applicationContext = new BearApplicationContext("classpath:application.properties");
        applicationContext.getBean("HelloController");
        System.out.println(applicationContext);
    }
}

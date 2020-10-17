package com.bear.demo.aspect;

/**
 * @author bear
 * @version 1.0
 * @className LogAspect
 * @description TODO
 * @date 2020/9/12 21:31
 */
public class LogAspect {

    public void before() {
        // 往对象里面记录调用的开始时间

    }

    public void after() {
        // 系统当前时间 - 之前记录的开始时间
    }

    public void afterThrowing() {
        // 异常检测
    }

}

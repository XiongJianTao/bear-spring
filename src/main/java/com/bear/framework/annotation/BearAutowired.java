package com.bear.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bear
 * @version 1.0
 * @className BearAutowired
 * @description TODO
 * @date 2020/8/29 10:10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BearAutowired {
    String value() default "";
}

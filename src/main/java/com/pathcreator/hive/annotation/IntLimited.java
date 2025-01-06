package com.pathcreator.hive.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntLimited {
    String description() default "The method is limited by the size of int";
}
package com.luwq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限校验注解，可标注在 Controller 类或方法上。
 * 超管（*:*:*）自动放行。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /** 需要的权限码，任一满足即可（默认），或用 anyOf=false 要求全部满足 */
    String[] value();

    /** true-满足任一即可；false-需全部满足 */
    boolean anyOf() default true;
}

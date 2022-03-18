package com.lyz.rpc.consumer;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc 类引用的注解
 * 自动注入 spring 容器
 * @author liyizhen
 * @date 2022/3/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Autowired
public @interface RpcReference {
    String version() default "1.0.0";
}

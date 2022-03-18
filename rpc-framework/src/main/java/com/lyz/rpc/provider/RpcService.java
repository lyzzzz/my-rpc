package com.lyz.rpc.provider;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc 服务类的声明注解
 * 自动注入 spring 容器
 * @author liyizhen
 * @date 2022/3/17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component // 注入 spring 容器
public @interface RpcService {
    Class<?> interfaceClass() default Object.class;

    String version() default "1.0.0";
}

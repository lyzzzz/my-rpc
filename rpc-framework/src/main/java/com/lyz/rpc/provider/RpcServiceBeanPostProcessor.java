package com.lyz.rpc.provider;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * rpc 服务类的后置处理器
 * @author liyizhen
 * @date 2022/3/17
 */
public class RpcServiceBeanPostProcessor implements BeanPostProcessor {
    private final RpcServiceRegistry rpcServiceRegistry;

    public RpcServiceBeanPostProcessor(RpcServiceRegistry rpcServiceRegistry) {
        this.rpcServiceRegistry = rpcServiceRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
        if (annotation != null) {
            Class<?> interfaceClass = annotation.interfaceClass();
            String version = annotation.version();

            rpcServiceRegistry.register(interfaceClass.getName(), version, bean);
        }
        return bean;
    }
}

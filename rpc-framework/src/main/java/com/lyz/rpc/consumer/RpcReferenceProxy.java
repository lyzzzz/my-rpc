package com.lyz.rpc.consumer;

import com.lyz.rpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * rpc 引用代理
 * @author liyizhen
 * @date 2022/3/18
 */
public class RpcReferenceProxy implements InvocationHandler {
    private final RegistryService registryService;
    private final Class<?> interfaceClass;
    private final String version;

    public RpcReferenceProxy(RegistryService registryService, Class<?> interfaceClass, String version) {
        this.registryService = registryService;
        this.interfaceClass = interfaceClass;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO 构造协议 发送请求 连接 consumer

        return null;
    }
}

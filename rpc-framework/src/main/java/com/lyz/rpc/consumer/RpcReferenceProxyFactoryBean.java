package com.lyz.rpc.consumer;

import com.lyz.rpc.registry.RegistryService;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * rpc 引用类的代理
 * @author liyizhen
 * @date 2022/3/18
 */
public class RpcReferenceProxyFactoryBean implements FactoryBean<Object> {
    @Setter
    private final RegistryService registryService;
    @Setter
    private Class<?> interfaceClass;
    @Setter
    private String version;
    private Object object;

    public RpcReferenceProxyFactoryBean(RegistryService registryService) {
        this.registryService = registryService;
    }

    public void init() {
        this.object =
                Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                        new Class<?>[]{ interfaceClass },
                        new RpcReferenceProxy(registryService, interfaceClass, version));
    }

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}

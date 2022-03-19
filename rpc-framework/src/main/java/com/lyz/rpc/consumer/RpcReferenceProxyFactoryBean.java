package com.lyz.rpc.consumer;

import com.lyz.rpc.registry.RegistryService;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * rpc 引用类的代理
 * @author liyizhen
 * @date 2022/3/18
 */
public class RpcReferenceProxyFactoryBean implements FactoryBean<Object> {
    private RegistryService registryService;
    private Class<?> interfaceClass;
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

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

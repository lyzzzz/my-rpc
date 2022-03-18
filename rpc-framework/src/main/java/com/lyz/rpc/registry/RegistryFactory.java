package com.lyz.rpc.registry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

/**
 * TODO 研究 FactoryBean 的初始化时机
 * 注册中心工厂
 * @author liyizhen
 * @date 2022/3/14
 */
public class RegistryFactory implements FactoryBean<RegistryService> {
    private static String CONFIG_PATH_REGISTRY_TYPE = "rpc.registry.type";
    private static String CONFIG_PATH_REGISTRY_CONFIG = "rpc.registry.config";

    private Environment environment;

    RegistryFactory(Environment environment) {
        this.environment = environment;
    }

    @Override
    public RegistryService getObject() throws Exception {
        RegistryType type = environment.getProperty(CONFIG_PATH_REGISTRY_TYPE, RegistryType.class);
        if (type == null) {
            throw new IllegalArgumentException("没有指定注册中心类型！");
        }

        RegistryService registryService = null;
        switch (type) {
            case MEMORY:
                registryService = buildMemoryRegistryService();
                break;
            case EUREKA:
                break;
            case ZOOKEEPER:
                break;
            default:
                throw new IllegalArgumentException("错误的注册中心类型");
        }
        return registryService;
    }

    private RegistryService buildMemoryRegistryService() {
        BindResult<MemoryRegistryConfig> bindResult = Binder.get(environment).bind(CONFIG_PATH_REGISTRY_CONFIG, MemoryRegistryConfig.class);
        return new MemoryRegistryService(bindResult.get());
    }

    @Override
    public Class<?> getObjectType() {
        return RegistryService.class;
    }
}

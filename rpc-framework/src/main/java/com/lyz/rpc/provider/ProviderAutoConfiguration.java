package com.lyz.rpc.provider;

import com.lyz.rpc.registry.RegistryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 提供者自动配置
 * @author liyizhen
 * @date 2022/3/16
 */
@EnableConfigurationProperties(ProviderConfig.class)
@ConditionalOnProperty(value = "rpc.provider.enabled", havingValue = "true")
public class ProviderAutoConfiguration {
    @Bean
    ProviderServer providerServer(RegistryService registryService,
                                  RpcServiceRegistry rpcServiceRegistry,
                                  ProviderConfig providerConfig) {
        return new ProviderServer(registryService,
                rpcServiceRegistry,
                providerConfig.getHost(),
                providerConfig.getPort(),
                providerConfig.getInstanceId(),
                providerConfig.getVersion(),
                providerConfig.getMetadata());
    }

    @Bean
    RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor(RpcServiceRegistry rpcServiceRegistry) {
        return new RpcServiceBeanPostProcessor(rpcServiceRegistry);
    }

    @Bean
    RpcServiceRegistry rpcServiceRegistry() {
        return new RpcServiceRegistry();
    }
}

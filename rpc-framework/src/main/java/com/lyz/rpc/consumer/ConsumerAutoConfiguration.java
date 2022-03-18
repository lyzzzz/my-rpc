package com.lyz.rpc.consumer;

import com.lyz.rpc.registry.RegistryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author liyizhen
 * @date 2022/3/18
 */
@ConditionalOnProperty(value = "rpc.consumer.enabled", havingValue = "true")
public class ConsumerAutoConfiguration {
    @Bean
    RpcReferenceBeanFactoryPostProcessor rpcReferenceBeanFactoryPostProcessor(RegistryService registryService) {
        return new RpcReferenceBeanFactoryPostProcessor(registryService);
    }
}

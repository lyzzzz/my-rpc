package com.lyz.rpc.registry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

/**
 * 注册中心自动配置
 * @author liyizhen
 * @date 2022/3/14
 */
@ConditionalOnProperty(value = "rpc.registry.enabled", havingValue = "true")
public class RegistryAutoConfiguration {
    @Lazy(value = false)
    @Bean
    RegistryFactory registryFactory(Environment environment) {
        return new RegistryFactory(environment);
    }
}

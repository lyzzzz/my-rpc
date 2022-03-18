package com.lyz.registry;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author liyizhen
 * @date 2022/3/16
 */
@Slf4j
@RestController
public class MemoryRegistryController {
    public final Registry registry;

    public MemoryRegistryController(Registry registry) {
        this.registry = registry;
    }

    @PostMapping({ "/register/", "/register" })
    public Mono<?> register(@RequestBody RegisterInstance registerInstance) {
        log.info("收到注册请求 instanceId:{},host:{},port:{}",
                registerInstance.getInstanceId(), registerInstance.getHost(), registerInstance.getPort());

        Registry.Instance instance = new Registry.Instance();
        BeanUtils.copyProperties(registerInstance, instance);
        registry.register(instance);

        return Mono.empty();
    }

    @PostMapping({ "/unregister/", "/unregister" })
    public Mono<?> unregister(@RequestBody UnregisterInstance unregisterInstance) {
        log.info("收到解决注册请求 host:{},port:{}", unregisterInstance.getInstanceId());

        registry.unregister(unregisterInstance.getInstanceId());

        return Mono.empty();
    }

    @Data
    public static class RegisterInstance {
        private String instanceId;
        private String version;
        private String host;
        private Integer port;
        private Map<String, String> metadata;
    }

    @Data
    public static class UnregisterInstance {
        private String instanceId;
    }
}

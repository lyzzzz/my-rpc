package com.lyz.registry;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyizhen
 * @date 2022/3/16
 */
@Component
public class Registry {
    private ConcurrentHashMap<String, Instance> instances = new ConcurrentHashMap<>();

    public Instance register(Instance instance) {
        instances.put(instance.getInstanceId(), instance);
        instance.createdTime = System.currentTimeMillis();
        return instance;
    }

    public Instance unregister(String instanceId) {
        return instances.remove(instanceId);
    }

    public Optional<Instance> getInstance(String instanceId) {
        return Optional.ofNullable(instances.get(instanceId));
    }

    @Data
    public static class Instance {
        private String instanceId;
        private String version;
        private String host;
        private Integer port;
        private Long createdTime;
        private Map<String, String> metadata;
    }
}

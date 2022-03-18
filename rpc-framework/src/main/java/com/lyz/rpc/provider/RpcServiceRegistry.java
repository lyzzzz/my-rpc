package com.lyz.rpc.provider;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc 服务注册中心
 * @author liyizhen
 * @date 2022/3/17
 */
public class RpcServiceRegistry {
    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public Optional<Object> getRpcService(String serviceName, String version) {
        return Optional.ofNullable(
                map.get(getKey(serviceName, version))
        );
    }

    public void register(String serviceName, String version, Object bean) {
        map.put(getKey(serviceName, version), bean);
    }

    public void unregister(String serviceName, String version) {
        map.remove(getKey(serviceName, version));
    }

    private String getKey(String serviceName, String version) {
        return serviceName + "+" + version;
    }
}

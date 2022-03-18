package com.lyz.rpc.registry;

import com.lyz.rpc.core.InstanceInfo;

/**
 * 注册中心通用接口
 * @author liyizhen
 * @date 2022/3/14
 */
public interface RegistryService {
    /**
     * 注册实例
     * @param instanceInfo 实例信息
     */
    void register(InstanceInfo instanceInfo);

    /**
     * 解除注册实例
     * @param instanceInfo 实例信息
     */
    void unregister(InstanceInfo instanceInfo);

    /**
     * 销毁注册中心
     */
    void destroy();
}

package com.lyz.rpc.registry;

/**
 * 支持的注册中心类型
 * @author liyizhen
 * @date 2022/3/14
 */
public enum RegistryType {
    /**
     * 内存注册中心
     */
    MEMORY,
    /**
     * zookeeper 注册中心
     */
    ZOOKEEPER,
    /**
     * eureka 注册中心
     */
    EUREKA
}

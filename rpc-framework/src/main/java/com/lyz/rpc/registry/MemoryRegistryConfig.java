package com.lyz.rpc.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内存类型注册中心配置
 * @author liyizhen
 * @date 2022/3/15
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
class MemoryRegistryConfig {
    private String host;
    private Integer port;
}

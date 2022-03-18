package com.lyz.rpc.core;

import lombok.Data;

import java.util.Map;

/**
 * 实例信息 bean
 * @author liyizhen
 * @date 2022/3/14
 */
@Data
public class InstanceInfo {
    private String instanceId;
    private String version;
    private String host;
    private Integer port;
    private Map<String, String> metadata;
}

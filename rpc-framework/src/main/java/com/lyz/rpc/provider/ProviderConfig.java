package com.lyz.rpc.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 提供者设置
 * @author liyizhen
 * @date 2022/3/16
 */
@ConfigurationProperties(prefix = "rpc.provider")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProviderConfig {
    private String host;
    private Integer port;
    private String instanceId;
    private String version;
    private Map<String, String> metadata;
}
